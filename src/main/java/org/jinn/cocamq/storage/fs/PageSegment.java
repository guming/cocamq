package org.jinn.cocamq.storage.fs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

/**
 * File segment
 * @author guming
 *
 */
public class PageSegment{
	private static final Logger logger = Logger.getLogger(PageSegment.class);
    final long start;
    final File file;//the real file
    public FileHandler filePage;//the wrapper of file
	private long readlimit=1024*1024;
	private static final String FILE_SUFFIX = ".mq";
    public PageSegment(final long start, final File file,final boolean checkWrite) {
        super();
        this.start = start;
        this.file = file;
        logger.info("Created filePageWrapper " + this.file.getAbsolutePath());
        try {
            final FileChannel channel = new RandomAccessFile(this.file, "rw").getChannel();
            if(channel.size()>0&&checkWrite)
            	channel.position(channel.size());
            logger.info("channel position:"+channel.position()+",segment start:"+start);
            this.filePage = new FilePage(this.file.getAbsolutePath(),channel,checkWrite,start);
        }
        catch (final IOException e) {
        	logger.error("wrapper failed", e);
        }
    }
    /**
     * get the  offset of filepage
     * @return
     */
    public long size() {
        return this.filePage.getSizeInfile();
    }
    /**
     * check the offset
     * @param offset
     * @return
     */
    public boolean contains(final long offset) {
        if (this.size() == 0 && offset == this.start || this.size() > 0 && offset >= this.start
                && offset <= this.start + this.size() - 1) {
            return true;
        }
        else {
            return false;
        }
    }
    /**
     * get the index of file
     * @return
     */
    public long getFileIndex(){
    	String fileName=file.getName();
//    	System.out.println("fileName:"+fileName);
    	final long start = Long.parseLong(fileName.substring(0,
				fileName.length() - FILE_SUFFIX.length()));
//    	System.out.println("start:"+start);
    	return start;
    }
    
	public void read(final WritableByteChannel socketChanel,long offset,final long range) throws IOException {
		// TODO Auto-generated method stub
		if(offset<this.start){
			offset=this.start;
		}
		long start_off=offset-this.start;
		long end_off=range>this.readlimit?readlimit:range;
		logger.info("start_off:"+start_off+",end_off"+end_off);
		this.filePage.read(socketChanel,start_off,end_off);
	}
    public PageSegment slice(final long offset, final long limit) throws IOException {
        return new PageSegment(offset,this.file,false);
    }
    public void flush() throws IOException {
        this.filePage.flush();
    }
}
