package org.jinn.cocamq.storage.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import org.apache.log4j.Logger;
import org.jinn.cocamq.storage.exception.MessageException;
import org.jinn.cocamq.protocol.message.MessagePack;
import org.jinn.cocamq.protocol.message.Message;

/**
 * file storage
 * @author guming
 *
 */
public class PageManager {
	private static final Logger logger = Logger.getLogger(PageManager.class);
	PageSegmentSet fsm;
    MessagePack messagePack=new MessagePack() {
        @Override
        public void convert(byte[] bytes,List list) {

        }
    };
	public PageManager(String topic) {
		this.fsm = new PageSegmentSet(topic,0);;
	}
	
	public boolean ifNeedFlush(){
		return true;//temp
	}
	
	public void append(Message msg) throws IOException{
		try {
			ByteBuffer buf=messagePack.packMessageBuffer(msg);
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
