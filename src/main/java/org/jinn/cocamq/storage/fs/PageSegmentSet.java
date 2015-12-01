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
public class PageSegmentSet extends Thread implements Closeable{
	
	private static final Logger logger = Logger.getLogger(PageSegmentSet.class);
	
	private String topic;
	
	private FileSegmentList topicSegments = new FileSegmentList();
	
	private static final String BASE_DIR = "/data/mqfile/";
	
	private static final String FILE_PREFIX="000000000000";
	
	private static final String FILE_SUFFIX = ".mq";
	
	private AtomicLong highwatermark=new AtomicLong(0);
	
	private ReentrantLock writeLock = new ReentrantLock();
	
	public PageSegmentSet(String topic, int offsetstart) {
		// TODO Auto-generated constructor stub
		this.topic=topic;
		try {
			loadAllFileSegments(topic, offsetstart);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FileSegmentList getTopicSegments() {
		return topicSegments;
	}

	public PageSegment getTopicSegmentLast() {
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
			PageSegment last=getTopicSegmentLast();
			return last.filePage.append(msgbuf);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("appendBuffer error", e);
		}finally{
			writeLock.unlock();
		}
		return 0;
	}
	
	public void transferTo(final WritableByteChannel socketChanel,long offset,long range) throws IOException {
		PageSegment last=getTopicSegmentLast();
		last.read(socketChanel, offset,range);
	}
	
	public PageSegment append2FileSegmentList() throws IOException {
		if(!getTopicSegments().last().filePage.checkWrite()){
			getTopicSegments().last().filePage.flush();
			highwatermark.set(getTopicSegments().last().getFileIndex());
			long idx=highwatermark.get()+1024L;
			PageSegment fs = new PageSegment(0, new File(BASE_DIR+topic
					+ File.separator+FILE_PREFIX+idx+FILE_SUFFIX),true);
			getTopicSegments().append(fs);
		}
		return getTopicSegmentLast();
	}
	
	private void loadAllFileSegments(String topic, int offsetstart) throws IOException {
		final List<PageSegment> fsList = new ArrayList<PageSegment>();
		String path = BASE_DIR + topic+File.separator;
		mkdirs(path);
		String[] fileNames = scanItemFileName(path, FILE_SUFFIX);
		if (null != fileNames&&fileNames.length>0) {
			for (int i = 0; i < fileNames.length; i++) {
				final long start = Long.parseLong(fileNames[i].substring(0,
						fileNames[i].length() - FILE_SUFFIX.length()));
				PageSegment fs = new PageSegment(start*1024, new File(path
						+ fileNames[i]),false);//1024*
				if (fsList.size() == 0) {
					fsList.add(fs);
				} else {
					Collections.sort(fsList, new Comparator<PageSegment>() {
						@Override
						public int compare(PageSegment o1, PageSegment o2) {
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
			final PageSegment last=fsList.remove(fsList.size()-1);
			last.filePage.close();
			PageSegment theLast = new PageSegment(last.start,last.file,true);//
			fsList.add(theLast);
			topicSegments=new FileSegmentList(fsList.toArray(new PageSegment[fsList.size()]));
			highwatermark.set(getTopicSegments().last().getFileIndex());
		}else{
			PageSegment fs = new PageSegment(0, new File(path
					+ FILE_PREFIX+"0000"+FILE_SUFFIX),true);
			PageSegment[] fss=new PageSegment[]{fs};
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
						&& fileName.toLowerCase().endsWith(fileType)) { // the file last must be .mq
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

		AtomicReference<PageSegment[]> contents = new AtomicReference<PageSegment[]>();

		public FileSegmentList(final PageSegment[] s) {
			this.contents.set(s);
		}

		public FileSegmentList() {
			super();
			this.contents.set(new PageSegment[0]);
		}

		public void append(final PageSegment segment) {
			while (true) {
				final PageSegment[] src = this.contents.get();
				final PageSegment[] dest = new PageSegment[src.length + 1];
				System.arraycopy(src, 0, dest, 0, src.length);
				dest[src.length] = segment;
				if (this.contents.compareAndSet(src, dest)) {
					return;
				}
			}
		}

		public void delete(final PageSegment segment) {
			while (true) {
				final PageSegment[] curr = this.contents.get();
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
				final PageSegment[] update = new PageSegment[curr.length - 1];
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

		public PageSegment[] view() {
			return this.contents.get();
		}

		public PageSegment last() {
			final PageSegment[] copy = this.view();
			if (copy.length > 0) {
				return copy[copy.length - 1];
			}
			return null;
		}

		public PageSegment first() {
			final PageSegment[] copy = this.view();
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
	
	 /**
     * @param offset
     * @param maxSize
     * @return
     * @throws java.io.IOException
     */
    public PageSegment slice(final long offset, final int maxSize) throws IOException {
        final PageSegment segment = this.findSegment(offset);
        if (segment == null) {
            return null;
        }
        else {
            return segment.slice(offset - segment.start, offset - segment.start + maxSize);
        }
    }
    public void flush() throws IOException {
        this.getTopicSegmentLast().flush();
    }
	public PageSegment findSegment(final long offset) {
		final PageSegment[] segments=getTopicSegments().view();
        if (segments == null || segments.length < 1) {
            return null;
        }
        final PageSegment last = segments[segments.length - 1];
        if (offset < segments[0].start) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (offset >= last.start + last.size()) {
            return null;
        }
        int low = 0;
        int high = segments.length - 1;
        while (low <= high) {
            final int mid = high + low >>> 1;
        final PageSegment found = segments[mid];
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
