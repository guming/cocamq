package org.jinn.cocamq.test.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import org.jinn.cocamq.commons.CheckCRC32;
import org.jinn.cocamq.commons.ClientConfig;
import org.jinn.cocamq.commons.CommonExcutor;
import org.jinn.cocamq.commons.MessagePack;
import org.jinn.cocamq.entity.Message;
import org.jinn.cocamq.entity.MessageBytes;
import org.jinn.cocamq.client.entity.MessageJson;
import org.jinn.cocamq.storage.MsgFileStorage;
import org.junit.Test;

public class MsgFileStorageTest {
	@Test
	public void testAppendMessage() {
		MsgFileStorage mfs=new MsgFileStorage("comment");
        long start =System.currentTimeMillis();
		  try {
              for (int i = 0; i < 40000; i++) {
                  String temp2="{\"action\":\"edit\",\"redis_key_hash\":\"1\",\"DB_key_hash\":\"\"," +
                          "\"time\":\"1406168332.35081900\",\"source\":\"web\",\"mars_cid\":\"\"," +
                          "\"session_id\":\"\",\"info\":{\"cart_id\":\"6185\",\"user_id\":\""+i+"\",\"brand_id\":\"7511\"," +
                          "\"num\":2,\"warehouse\":\"as大劫案快解放但就是放得开束ash侃大山" +
                          "ash看动画东方航空上帝会富士康解释都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                          "ash看动画东方航空上帝会富士康解释都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                          "都很费劲第三方还是开货到付款导师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                          "ash看动画东方航空上帝会富士康解释都很费劲第三方还师考核发解释都开发还是看到横峰街道很费劲黑道教父黑道教" +
                          "父花雕鸡开户行静安寺咁大噶就是个法华经爱就是大是大非带结发华东师范\",\"merchandise_id\":\"1001950\",\"channel\":\"te\"," +
                          "\"cart_record_id\":\"8765\",\"size_id\":\"2756943\"}}";
                  byte[] temp=temp2.getBytes();
                  int checkSum = CheckCRC32.crc32(temp);
                      Message msg = new MessageBytes(checkSum,temp2,"comment");
                      mfs.appendMessage(msg);
                    temp=null;//help gc
		       }
		      } catch (Exception e) {
		              // TODO Auto-generated catch block
		              e.printStackTrace();
		      }finally{
                 System.out.println(System.currentTimeMillis()-start);
              }
	}
	@Test
	public void testAppendMessageThreads() {
		final MsgFileStorage mfs=new MsgFileStorage("comment");
        long start =System.currentTimeMillis();
		  try {
              for (int i = 0; i < 40000; i++) {
            	  CommonExcutor.getExec().execute(
            			new Runnable(){
						@Override
						public void run() {
//							  Message msg = new MessageJson();
//								msg.setTopic("comment");
//								msg.setType(1);
//								msg.setMemo("msg test" + UUID.randomUUID().toString());
//								msg.setIp(11);
//								msg.setUid(45);
//								msg.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
//		                      msg.setId(1000);
//		                      mfs.appendMessage(msg);
						}
		              }
            	);
              }
		      } catch (Exception e) {
		              // TODO Auto-generated catch block
		              e.printStackTrace();
		      }finally{
		      }
			try {
                System.out.println(System.currentTimeMillis()-start);
                Thread.sleep(10000);
			} catch (Exception e) {
				// TODO: handle exception
			}
	}
	@Test
	public void testGetMessagesBeforeByTopic() throws Exception{
		final MsgFileStorage mfs=new MsgFileStorage("comment");
		
        FileChannel channel=null;
		try {
			channel = new RandomAccessFile(new File("/opt/data/mq/test.txt"), "rw").getChannel();
			mfs.fetchMessagesBeforeByTopic(channel, 0,1024, "comment");
			ByteBuffer bb=ByteBuffer.allocate(1024);
			channel.position(0);
			channel.read(bb);
			List<MessageJson> listMsg= MessagePack.unpackMessages(bb.array(), 0,new ClientConfig());
			System.out.println("listMsg size:"+listMsg.size());
//			channel = new RandomAccessFile(new File("/opt/data/mq/test2.txt"), "rw").getChannel();
//			mfs.fetchMessagesBeforeByTopic(channel, 1025*1024,1024, "comment");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(null!=channel){
				channel.force(true);
				channel.close();
			}
		}
	}
}
