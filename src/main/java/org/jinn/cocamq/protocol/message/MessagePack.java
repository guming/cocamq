package org.jinn.cocamq.protocol.message;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;
import org.jinn.cocamq.util.CheckCRC32;
import org.jinn.cocamq.client.ClientConfig;
import org.jinn.cocamq.storage.exception.MessageException;


public abstract class MessagePack {
	
	 private static final Logger logger = Logger.getLogger(MessagePack.class);
	
	 public static final int HEADER_LEN = 16;

//	 public static final int MAX_READ_BUFFER_SIZE = Integer.parseInt(System.getProperty("notify.remoting.max_read_buffer_size", "2097152"));

	 /**
	  * msg length + checksum + tag + data
	  * @param req
	  * @return
	  * @throws Exception
	  */
	 public final ByteBuffer packMessageBuffer(final Message req) throws MessageException {
	        // message length + checksum + type + data
            byte[] c_bytes= req.getPacket().getBytes();
            int c_len=c_bytes.length;
	        final ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + 8 + c_len);
	        buffer.putInt(c_len);//message length
	        int checkSum = CheckCRC32.crc32(c_bytes);
	        // compare crc32
//	        if (req.getId() != -1) {
//	            if (checkSum != req.getId()) {
//	                throw new MessageException(
//	                        "Checksum failure,message may be corrupted when transfering on networking.");
//	            }
//	        }
	        buffer.putInt(checkSum);
	        buffer.putLong(1001L);//tag 预留
	        buffer.put(c_bytes);//message data
	        buffer.flip();
	        return buffer;
	    }
	 
	 public  final boolean unpackMessages(final byte[] data, final int offset,ClientConfig cc,List<Message> list)
	            throws Exception {
		    int length=data.length;
		    int length_org = data.length;
		    int myoffset=0;
		    int count =0;
         boolean flag=true;
         while(length>0){
             try {
                 final ByteBuffer buf = ByteBuffer.wrap(data, myoffset, HEADER_LEN);
                 final int msgLen = buf.getInt();
                 final int checksum = buf.getInt();
                 int msgOffset = myoffset + HEADER_LEN;
//		        vailidateMessage(msgOffset, msgLen, checksum, data);
                 if (msgOffset + msgLen + 8 <= length_org) {
                     final byte[] dest = new byte[msgLen];
                     System.arraycopy(data, msgOffset, dest, 0, msgLen);
                     logger.warn("message:" + new String(dest));
                     convert(dest, list);
                     length -= HEADER_LEN + msgLen;
                     myoffset += HEADER_LEN + msgLen;
                     count++;
                 } else {
                     logger.warn("message not completed");
                     length = -1;
                 }
             }catch(NegativeArraySizeException e){
                 e.printStackTrace();
                 length=-1;
                 flag=false;
             }
		    }
		    cc.setOffset(myoffset+offset);
		    System.out.println("---------------"+count);
         return flag;
	  }

    /**
     * subclass must be imples
     * @param bytes
     */
      public abstract  void convert(byte[] bytes,List<Message> list);

	  public static final void vailidateMessage(final int offset, final int msgLen, final int checksum, final byte[] data)
	            throws Exception {
	        if (checksum != CheckCRC32.crc32(data, offset, msgLen)) {
	            throw new Exception("Invalid message");
	        }
	  }
}
