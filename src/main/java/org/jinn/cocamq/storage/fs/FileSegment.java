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
public class FileSegment{
	
	private static final Logger logger = Logger.getLogger(FileSegment.class);
    final long start;
    final File file;//the real file
    public FileHandler filePage;//the wrapper of file
    
    public FileSegment(final long start, final File file) {
        super();
        this.start = start;
        this.file = file;
        logger.info("Created filePageWrapper " + this.file.getAbsolutePath());
        try {
            final FileChannel channel = new RandomAccessFile(this.file, "rw").getChannel();
            this.filePage = new FilePage(this.file.getAbsolutePath(),channel,true,start);
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
    	final long start = Long.parseLong(fileName.substring(0,
				fileName.length() - ".mq".length()));
    	return start;
    }
    
	public void write(final WritableByteChannel socketChanel,long offset) throws IOException {
		// TODO Auto-generated method stub
		if(offset<1){
			offset=this.start;
		}
		this.filePage.getChannel().transferTo(offset, size(), socketChanel);
	}
    public FileSegment slice(final long offset, final long limit) throws IOException {
        return new FileSegment(offset,this.file);
    }
}
