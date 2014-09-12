package org.jinn.cocamq.storage.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;
import org.jinn.cocamq.commons.MessageException;
import org.jinn.cocamq.commons.MessagePack;
import org.jinn.cocamq.entity.Message;

/**
 * file storage
 * @author guming
 *
 */
public class PageStorage {
	private static final Logger logger = Logger.getLogger(PageStorage.class);
	private static final String BASE_DIR="/Users/gumingcn/dev/mqfile/";
	PageSegmentSet fsm;

	public PageStorage(String topic) {
		this.fsm = new PageSegmentSet(topic,0);;
	}
	
	public boolean ifNeedFlush(){
		return true;//temp
	}
	
	public void append(Message msg) throws IOException{
		try {
			ByteBuffer buf=MessagePack.packMessageBuffer(msg);
			fsm.appendBuffer(buf);
		}catch (MessageException e) {
			logger.error("append2file error", e);
		} 
	}
	
	public void read(final WritableByteChannel socketChanel,long offset,long range){
		PageSegment fs=fsm.findSegment(offset);
		try {
			if(fs!=null)
			fs.read(socketChanel, offset,range);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch blockd
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
		}
	}
}
