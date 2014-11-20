package org.jinn.cocamq.storage.fs;

import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * the manager of topicFileSegments
 * @author guming
 */
public class FileSegmentManager extends Thread implements Closeable{
	
	private static final Logger logger = Logger.getLogger(FileSegmentManager.class);
	
	private String topic;
	
	private FileSegmentList topicSegments = new FileSegmentList();
	
	private static final String BASE_DIR = "/opt/data/mq/";
	
	private static final String FILE_PREFIX="000000000000";
	
	private static final String FILE_SUFFIX = ".mqd";
	
	private AtomicLong highwatermark=new AtomicLong(0);
	
	private ReentrantLock writeLock = new ReentrantLock();
	
	public FileSegmentManager(String topic, int offsetstart) {
		// TODO Auto-generated constructor stub
		this.topic=topic;
		loadAllFileSegments(topic, offsetstart);
	}

	public FileSegmentList getTopicSegments() {
		return topicSegments;
	}

	public FileSegment getTopicSegmentLast() {
		if(getTopicSegments().last().filePage.checkWrite()){
			return getTopicSegments().last();
		}else{
			try {
				return append2FileSegmentList();
			} catch (Exception e) {
				// TODO: handle exception
				logger.warn("append2FileSegmentList failed", e);
				return getTopicSegments().last();
			}
		}
	}
	
	public long appendBuffer(final ByteBuffer msgbuf){
		try {
			writeLock.lock();
			FileSegment last=getTopicSegmentLast();
			return last.filePage.append(msgbuf);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("appendBuffer error", e);
		}finally{
			writeLock.unlock();
		}
		return 0;
	}
	
	public void transferTo(final WritableByteChannel socketChanel,long offset) throws IOException {
		FileSegment last=getTopicSegmentLast();
		last.write(socketChanel, offset);
	}
	
	public FileSegment append2FileSegmentList() throws IOException {
		if(!getTopicSegments().last().filePage.checkWrite()){
			getTopicSegments().last().filePage.flush();
			highwatermark.set(getTopicSegments().last().getFileIndex());
			long idx=highwatermark.get()+1024L;
			FileSegment fs = new FileSegment(0, new File(BASE_DIR+topic
					+ File.separator+FILE_PREFIX+idx+FILE_SUFFIX));
			getTopicSegments().append(fs);
		}
		return getTopicSegmentLast();
	}
	
	private void loadAllFileSegments(String topic, int offsetstart) {
		final List<FileSegment> fsList = new ArrayList<FileSegment>();
		String path = BASE_DIR + topic+File.separator;
		mkdirs(path);
		String[] fileNames = scanItemFileName(path, FILE_SUFFIX);
		if (null != fileNames&&fileNames.length>0) {
			for (int i = 0; i < fileNames.length; i++) {
				final long start = Long.parseLong(fileNames[i].substring(0,
						fileNames[i].length() - FILE_SUFFIX.length()));
				FileSegment fs = new FileSegment(start, new File(path
						+ fileNames[i]));
				if (fsList.size() == 0) {
					fsList.add(fs);
				} else {
					Collections.sort(fsList, new Comparator<FileSegment>() {
						@Override
						public int compare(FileSegment o1, FileSegment o2) {
							// TODO Auto-generated method stub
							if (o1.start == o2.start) {
								return 0;
							} else if (o1.start > o2.start) {
								return 1;
							} else {
								return -1;
							}
						}
					});
					fsList.add(fs);
				}
			}
			topicSegments=new FileSegmentList(fsList.toArray(new FileSegment[fsList.size()]));
			highwatermark.set(getTopicSegments().last().getFileIndex());
		}else{
			FileSegment fs = new FileSegment(0, new File(path
					+ FILE_PREFIX+"0000"+FILE_SUFFIX));
			FileSegment[] fss=new FileSegment[]{fs};
			topicSegments=new FileSegmentList(fss);
		}
	}

	public static String[] scanItemFileName(String path, final String fileType) {
		File file = new File(path);
		if (!file.isDirectory()) { 
			return null;
		}
		
		String[] fileNames = null;
		fileNames = file.list(new FilenameFilter() {
			public boolean isFileType(File dir, String fileName) {
				boolean flag = false;
				if (!new File(dir, fileName).isDirectory()
						&& fileName.toLowerCase().endsWith(fileType)) {
																		// true
					flag = true;
				}
				return flag;
			}

			@Override
			public boolean accept(File dir, String name) {
				return isFileType(dir, name);
			}
		});
		return fileNames;
	}

	static class FileSegmentList {

		AtomicReference<FileSegment[]> contents = new AtomicReference<FileSegment[]>();

		public FileSegmentList(final FileSegment[] s) {
			this.contents.set(s);
		}

		public FileSegmentList() {
			super();
			this.contents.set(new FileSegment[0]);
		}

		public void append(final FileSegment segment) {
			while (true) {
				final FileSegment[] src = this.contents.get();
				final FileSegment[] dest = new FileSegment[src.length + 1];
				System.arraycopy(src, 0, dest, 0, src.length);
				dest[src.length] = segment;
				if (this.contents.compareAndSet(src, dest)) {
					return;
				}
			}
		}

		public void delete(final FileSegment segment) {
			while (true) {
				final FileSegment[] curr = this.contents.get();
				int index = -1;
				for (int i = 0; i < curr.length; i++) {
					if (curr[i] == segment) {
						index = i;
						break;
					}

				}
				if (index == -1) {
					return;
				}
				final FileSegment[] update = new FileSegment[curr.length - 1];
				// first copy	
				System.arraycopy(curr, 0, update, 0, index);
				// second copy
				if (index + 1 < curr.length) {
					System.arraycopy(curr, index + 1, update, index,
							curr.length - index - 1);
				}
				if (this.contents.compareAndSet(curr, update)) {
					return;
				}
			}
		}

		public FileSegment[] view() {
			return this.contents.get();
		}

		public FileSegment last() {
			final FileSegment[] copy = this.view();
			if (copy.length > 0) {
				return copy[copy.length - 1];
			}
			return null;
		}

		public FileSegment first() {
			final FileSegment[] copy = this.view();
			if (copy.length > 0) {
				return copy[0];
			}
			return null;
		}
	}
	private static void mkdirs(String path) {
		    File file = new File(path);
		    if (!file.exists()) {
		      if (file.mkdirs()) {
		        logger.info("mkdirs \"" + path + "\" success");
		      } else {
		        logger.info("mkdirs \"" + path + "\" fail");
		      }
		    } else {
		      logger.info("dir \"" + path + "\" already exists");
		    }
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		getTopicSegmentLast().filePage.flush();
	}
	
    public FileSegment slice(final long offset, final int maxSize) throws IOException {
        final FileSegment segment = this.findSegment(offset);
        if (segment == null) {
            return null;
        }
        else {
            return segment.slice(offset - segment.start, offset - segment.start + maxSize);
        }
    }
	
	public FileSegment findSegment(final long offset) {
		final FileSegment[] segments=getTopicSegments().view();
        if (segments == null || segments.length < 1) {
            return null;
        }
        //
        final FileSegment last = segments[segments.length - 1];
        //
        if (offset < segments[0].start) {
            throw new ArrayIndexOutOfBoundsException();
        }
        //
        if (offset >= last.start + last.size()) {
            return null;
        }
        //
        int low = 0;
        int high = segments.length - 1;
        while (low <= high) {
            final int mid = high + low >>> 1;
        final FileSegment found = segments[mid];
        if (found.contains(offset)) {
            return found;
        }
        else if (offset < found.start) {
            high = mid - 1;
        }
        else {
            low = mid + 1;
        }
        }
        return null;
    }
}
