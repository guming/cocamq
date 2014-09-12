package org.jinn.cocamq.commons;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jinn.cocamq.entity.Message;
import org.jinn.cocamq.entity.MessageJson;
import org.jinn.cocamq.util.JSONUtil;


public class MessagePack {
	
	 private static final Logger logger = Logger.getLogger(MessagePack.class);
	
	 public static final int HEADER_LEN = 16;
	 
	 public static final int MAX_READ_BUFFER_SIZE = Integer.parseInt(System.getProperty("notify.remoting.max_read_buffer_size", "2097152"));
	 /**
	  * msg length + checksum + id + data
	  * @param req
	  * @return
	  * @throws Exception
	  */
	 public static final ByteBuffer packMessageBuffer(final Message req) throws MessageException {
	        // message length + checksum + type + data
            byte[] c_bytes= req.getContent().getBytes();
            int c_len=req.getContent().getBytes().length;
	        final ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + 8 + c_len);
	        buffer.putInt(c_len);
	        int checkSum = CheckCRC32.crc32(c_bytes);
	        // compare crc32
//	        if (req.getId() != -1) {
//	            if (checkSum != req.getId()) {
//	                throw new MessageException(
//	                        "Checksum failure,message may be corrupted when transfering on networking.");
//	            }
//	        }
	        buffer.putInt(checkSum);
	        buffer.putLong(1001L);
	        buffer.put(c_bytes);
	        buffer.flip();
	        return buffer;
	    }
	 
//	 public static final ByteBuffer packMessagesBuffer(final long msgId, List<Message> msgList) throws MessageException {
//		 final ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + 8 + req.getData().length*msgList.size());
//		 for (Message req : msgList) {
//			   // message length + checksum + id + data
//		        buffer.putInt(req.getData().length);
//		        int checkSum = CheckCRC32.crc32(req.getData());
//		        // compare crc32
//		        if (req.getCRC32() != -1) {
//		            if (checkSum != req.getCRC32()) {
//		                throw new MessageException(
//		                        "Checksum failure,message may be corrupted when transfering on networking.");
//		            }
//		        }
//		        buffer.putInt(checkSum);
//		        buffer.putLong(msgId);
//		        buffer.put(req.getData());
//		        buffer.flip();
//	    }
//		 return buffer;
//	 }
	 public static final MessageJson unpackMessage(final byte[] data, final int offset)
	            throws Exception {
	        final ByteBuffer buf = ByteBuffer.wrap(data, offset, HEADER_LEN);
	        final int msgLen = buf.getInt();
	        final int checksum = buf.getInt();
	        vailidateMessage(offset + HEADER_LEN, msgLen, checksum, data);
	        int msgOffset = offset + HEADER_LEN;
	        int payLoadLen = msgLen;
	        if (payLoadLen > MAX_READ_BUFFER_SIZE) {
	            throw new Exception("Too much long payload length:" + payLoadLen);
	        }
	        final byte[] dest = new byte[payLoadLen];
	        System.arraycopy(data, msgOffset, dest, 0, payLoadLen);
            MessageJson msg= (MessageJson) JSONUtil.jsonToBean(new String(dest),MessageJson.class);
            return msg;
	  }
	 
	 public static final List<MessageJson> unpackMessages(final byte[] data, final int offset,ClientConfig cc)
	            throws Exception {
		    List<MessageJson> msgList=new ArrayList<MessageJson>();
		    int length=data.length;
		    int length_org=data.length;
		    int myoffset=0;
		    int count =0;
		    while(length>0){
		        final ByteBuffer buf = ByteBuffer.wrap(data, myoffset, HEADER_LEN);
		        final int msgLen = buf.getInt();
		        final int checksum = buf.getInt();
		        int msgOffset = myoffset + HEADER_LEN;
//		        vailidateMessage(msgOffset, msgLen, checksum, data);
		        int payLoadLen = msgLen;
		        if (payLoadLen > MAX_READ_BUFFER_SIZE) {
		            throw new Exception("Too much long payload length:" + payLoadLen);
		        }
		        final byte[] dest = new byte[payLoadLen];
		        if(msgOffset+payLoadLen+8<=length_org){
		        	System.arraycopy(data, msgOffset, dest, 0, payLoadLen);
		        	logger.warn("message:"+new String(dest));
                    MessageJson msg= (MessageJson) JSONUtil.jsonToBean(new String(dest),MessageJson.class);
			        msgList.add(msg);
			        length-= HEADER_LEN+payLoadLen;
			        myoffset+= HEADER_LEN+payLoadLen;
			        count++;
		        }
		        else{
		        	logger.warn("message not completed");
		        	length=-1;
		        }
		    }
		    cc.setOffset(myoffset+offset);
		    System.out.println("---------------"+count);
	        return msgList;
	  }


	  public static final void vailidateMessage(final int offset, final int msgLen, final int checksum, final byte[] data)
	            throws Exception {
	        if (checksum != CheckCRC32.crc32(data, offset, msgLen)) {
	            throw new Exception("Invalid message");
	        }
	  }
}
