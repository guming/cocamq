package org.jinn.cocamq.test.netty;

import org.apache.log4j.Logger;
import org.jinn.cocamq.client.MessageProductor;
import org.jinn.cocamq.commons.CheckCRC32;
import org.jinn.cocamq.commons.CommonExcutor;
import org.jinn.cocamq.entity.Message;
import org.jinn.cocamq.entity.MessageBytes;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import com.google.common.base.Stopwatch;
public class MessageProducerTest {
	
	private final static Logger logger = Logger.getLogger(MessageProducerTest.class);
	
	@Test
	public void testConnet2Broker(){
		MessageProductor mp = new MessageProductor();
		mp.start();
		MessageProductor mp1 = new MessageProductor();
		mp1.start();
		MessageProductor mp2 = new MessageProductor();
		mp2.start();
		MessageProductor mp3 = new MessageProductor();
		mp3.start();
		 try {
				Thread.sleep(1000*10);
			} catch (Exception e) {
				// TODO: handle exception
			}
	}
	@Test
	public void testSendMessage(){
		MessageProductor mp = new MessageProductor();
		mp.start();
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		 try {
             for (int i = 0; i < 40000; i++) {
                     mp.sendMessage(getMessage(i));
		       }
		      } catch (Exception e) {
		              // TODO Auto-generated catch block
		              e.printStackTrace();
		      }finally{
		      }
			stopwatch.stop();
			logger.info("testSendMessage finished:"+stopwatch);
		    mp.stop();

	}
	
	@Test
	public void testSendMessageThreads(){
		 for (int j = 0; j < 4; j++) {
		CommonExcutor.getExec3().execute(
		new Runnable(){
			public void run(){
				logger.info("testSendMessage threads:");
				final MessageProductor mp = new MessageProductor();
				mp.start();
				Stopwatch stopwatch = new Stopwatch();
				stopwatch.start();
				 try {
		             for (int i = 0; i < 10000; i++) {
		                     mp.sendMessage(getMessage(i));
				       }
				      } catch (Exception e) {
				              // TODO Auto-generated catch block
				              e.printStackTrace();
				      }finally{
				      }
					stopwatch.stop();
					logger.info("testSendMessage finished:"+stopwatch);
				mp.stop();
			}
		});
		 }
		 try {
				Thread.sleep(1000*60);
			} catch (Exception e) {
				// TODO: handle exception
			}
	}
	
	private Message getMessage(final int i){
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

//        String temp2="{\"action\":\"edit\",\"redis_key_hash\":\"1\",\"DB_key_hash\":\"\"," +
//                "\"time\":\"1406168332.35081900\",\"source\":\"web\",\"mars_cid\":\"\"," +
//                "\"session_id\":\"\",\"info\":{\"cart_id\":\"6185\",\"user_id\":\""+i+"\",\"brand_id\":\"7511\"," +
//                "\"num\":2,\"warehouse\":\"asd\",\"merchandise_id\":\"1001950\",\"channel\":\"te\"," +
//                "\"cart_record_id\":\"8765\",\"size_id\":\"2756943\"}}";
        byte[] temp2Bytes=temp2.getBytes();
        int checknum=CheckCRC32.crc32(temp2Bytes);
		Message msg = new MessageBytes(checknum,temp2,"comment");
		return msg;
	}
	 public static void main(String[] args) {
	        for (int i = 0; i < 10; i++)
	            JUnitCore.runClasses(MessageProducerTest.class);
	 }
}
