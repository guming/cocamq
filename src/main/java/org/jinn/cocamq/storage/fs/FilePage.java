package org.jinn.cocamq.storage.fs;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

/**
 * append buf to the mqfile by the topic
 * @author guming
 *
 */
public class FilePage implements FileHandler, Closeable {
	private static final Logger logger = Logger.getLogger(FilePage.class);
	private final FileChannel channel;
	private String filelocate;
	private String topic;
	private final AtomicLong msgcount=new AtomicLong(0);
	private final AtomicLong offset=new AtomicLong(0);
	private final AtomicLong siteInfile=new AtomicLong(0);
	private final AtomicBoolean isWrite=new AtomicBoolean(true);
	private final AtomicLong highWaterMark; // write to disk
	private long limitsize=1024*1024*1024;
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(final String topic) {
		this.topic = topic;
	}

	public FilePage(String filelocate,final FileChannel channel,boolean checkWrite,final long offset) throws IOException {
		this.filelocate = filelocate;
		this.channel=channel;
		this.highWaterMark = new AtomicLong(0);
		this.siteInfile.set(Math.min(channel.size(), limitsize));
		if(checkWrite){
			this.offset.set(offset);
		}else{
			isWrite.set(false);
		}
		logger.info("file page offset start:"+this.offset.get());
		logger.info("file page channel size:"+channel.size()+",filelocate:"+filelocate);
	}
	/**
	 * write to disk
	 */
    public void flush() throws IOException {
        this.channel.force(true);
        this.highWaterMark.set(offset.get());
    }
    /**
     * append the file
     */
	public long append(final ByteBuffer msgbuf) throws IOException{
		try {
			int writesize=0;
			long current=siteInfile.get()+msgbuf.capacity();
			if(current>limitsize){
				isWrite.compareAndSet(true, false);
				return -1l;
			}
			while(msgbuf.hasRemaining()){
				writesize+=channel.write(msgbuf);
			}
			this.siteInfile.addAndGet(writesize);
			this.msgcount.incrementAndGet();
//			logger.info("siteInfile:"+siteInfile.get());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
		}
		return offset.get();
	}
	@Override
	public boolean checkWrite(){
		return isWrite.get();
	}
	@Override
	public long getSizeInfile(){
		return siteInfile.get();
	}
	@Override
	public long getLimitsize() {
		return limitsize;
	}
	public void setLimitsize(long limitsize) {
		this.limitsize = limitsize;
	}
	public AtomicLong getOffset() {
		return offset;
	}
	
	public String getFilelocate() {
		return filelocate;
	}

	public void setFilelocate(String filelocate) {
		this.filelocate = filelocate;
	}

	public AtomicLong getMsgcount() {
		return msgcount;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		channel.close();
	}
	@Override
	public void read(final WritableByteChannel target,long position,long end) throws IOException {
		// TODO Auto-generated method stub
		logger.info("transferto position:"+channel.position()+",start:"+position+",end:"+end+",filelocate:"+filelocate+",target:");
		this.channel.transferTo(position, end, target);
	}

	@Override
	public FileChannel getChannel() {
		// TODO Auto-generated method stub
		return this.channel;
	}

}
