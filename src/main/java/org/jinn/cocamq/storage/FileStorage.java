package org.jinn.cocamq.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import org.apache.log4j.Logger;
import org.jinn.cocamq.commons.ClientConfig;
import org.jinn.cocamq.commons.MessageException;
import org.jinn.cocamq.commons.MessagePack;
import org.jinn.cocamq.entity.Message;
import org.jinn.cocamq.entity.MessageJson;
import org.jinn.cocamq.storage.fs.FileSegment;
import org.jinn.cocamq.storage.fs.FileSegmentManager;

/**
 * file storage
 * @author guming
 *
 */
public class FileStorage {
	private static final Logger logger = Logger.getLogger(FileStorage.class);
	private static final String BASE_DIR="/Users/gumingcn/dev/mqfile/";
	FileSegmentManager fsm;

	public FileStorage(String topic) {
		FileSegmentManager fsm=new FileSegmentManager(topic,0);
		this.fsm = fsm;
	}

	public void appendToFile(Message msg) throws IOException{
		try {
			ByteBuffer buf=MessagePack.packMessageBuffer(msg);
			fsm.appendBuffer(buf);
		}catch (MessageException e) {
			logger.error("append2file error", e);
		} 
	}
	
	public void readFromFile(long offset,int index){
		FileSegment fs=fsm.findSegment(offset);
		try {
//			RandomAccessFile raf=new RandomAccessFile(new File(BASE_DIR+"/comments/"+"0000000000001024.mq"), "r");
			final FileChannel channel = fs.filePage.getChannel();
			channel.position(channel.position());
			 final ByteBuffer buf = ByteBuffer.allocate((int) channel.size());
	            while (buf.hasRemaining()) {
	                channel.read(buf);
	            }
	            List<MessageJson> msgs=MessagePack.unpackMessages(buf.array(), (int)offset,new ClientConfig());
	            for (MessageJson message : msgs) {
	            	System.out.println(message.toString());
				}
	            buf.flip();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
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
