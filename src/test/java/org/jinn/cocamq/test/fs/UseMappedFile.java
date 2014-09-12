package org.jinn.cocamq.test.fs;

import org.jinn.cocamq.entity.MessageBytes;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class UseMappedFile {
	
	     public static void main( String args[] ) throws Exception { 
//		     RandomAccessFile raf2 = new RandomAccessFile( "/Users/gumingcn/mqfile/temp2.mq", "rw" );
		     RandomAccessFile raf = new RandomAccessFile( "/Users/gumingcn/dev/mqfile/temp.mq", "rwd" );
		     FileChannel fc = raf.getChannel();
//		     FileChannel fc2 = raf2.getChannel(); 
		     MappedByteBuffer mappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, 1024*1024*100);
//		     fc.transferTo(0, fc.size(), raf2.getChannel());
////		     raf2.write(mappedByteBuffer.array());
//	//	     mappedByteBuffer.position((int)fc.size());
             String temp2="{\"action\":\"edit\",\"redis_key_hash\":\"1\",\"DB_key_hash\":\"\"," +
                     "\"time\":\"1406168332.35081900\",\"source\":\"web\",\"mars_cid\":\"\"," +
                     "\"session_id\":\"\",\"info\":{\"cart_id\":\"6185\",\"user_id\":\""+100+"\",\"brand_id\":\"7511\"," +
                     "\"num\":2,\"warehouse\":\"as大劫案快解放但就是放得开束ash侃大山" +
                     "ash看动画东方航空上帝会富士康解释都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                     "ash看动画东方航空上帝会富士康解释都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                     "都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                     "ash看动画东方航空上帝会富士康解释都很费劲第三方还师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                     "父花雕鸡开户行静安寺咁大噶就是个法华经爱就是大是大非带结发华东师范\",\"merchandise_id\":\"1001950\",\"channel\":\"te\"," +
                     "\"cart_record_id\":\"8765\",\"size_id\":\"2756943\"}}";
             System.out.println(temp2.getBytes().length);
             long start = System.currentTimeMillis();
             for (int i = 0; i < 40000; i++) {
//                 String temp="{\"action\":\"edit\",\"redis_key_hash\":\"1\",\"DB_key_hash\":\"\"," +
//                         "\"time\":\"1406168332.35081900\",\"source\":\"web\",\"mars_cid\":\"\"," +
//                         "\"session_id\":\"\",\"info\":{\"cart_id\":\"6185\",\"user_id\":\""+i+"\",\"brand_id\":\"7511\"," +
//                         "\"num\":2,\"warehouse\":\"VIP_NH\",\"merchandise_id\":\"1001950\",\"channel\":\"te\"," +
//                         "\"cart_record_id\":\"8765\",\"size_id\":\"2756943\"}}";

                 MessageBytes msg = new MessageBytes(i,temp2,"comment");

//                 ByteBuffer sd=MessagePack.packMessageBuffer(i, msg);

                 mappedByteBuffer.put(msg.getContent().getBytes());
             }
             mappedByteBuffer.force();
		     mappedByteBuffer.flip();
             System.out.println(System.currentTimeMillis()-start);
		 	byte[] bb=new byte[1024];
             mappedByteBuffer.get(bb,0,1024);
//		     raf.read(bb, 0, 1024);
		     System.out.println(new String(bb));
//		     fc.force(true);
//		     raf2.close();
//		     raf.close();
//		     Thread.sleep(1000*2);
	    } 

}
