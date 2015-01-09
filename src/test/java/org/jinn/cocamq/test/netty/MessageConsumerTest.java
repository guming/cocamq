package org.jinn.cocamq.test.netty;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.jinn.cocamq.client.consumer.MessageConsumer;
import org.jinn.cocamq.client.consumer.SimpleMessageProcessor;
import org.jinn.cocamq.util.CommonExcutor;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import com.google.common.base.Stopwatch;

public class MessageConsumerTest {

	private final static Logger logger = Logger
			.getLogger(MessageConsumerTest.class);
    int fetch=32*1024;
	@Test
	public void testConnet2Broker() {

		MessageConsumer mp = new MessageConsumer();
		mp.start();
		MessageConsumer mp1 = new MessageConsumer();
		mp1.start();
		MessageConsumer mp2 = new MessageConsumer();
		mp2.start();
		MessageConsumer mp3 = new MessageConsumer();
		mp3.start();
		try {
			Thread.sleep(1000 * 10);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Test
	public void testFetchMessages() {

		MessageConsumer mp = new MessageConsumer("comment", SimpleMessageProcessor.getInstance());
		mp.start();
		while(true){
//            try {
                Stopwatch stopwatch = new Stopwatch();
                stopwatch.start();
    //            mp.getCz().updateFetchOffset("comment",0);
                System.out.println(mp==null);
                System.out.println(mp.getCc()==null);
                int offset=mp.getCc().getOffset();
                System.out.println("the offset is :" + offset);
                mp.fetchMessage(offset, fetch);
                stopwatch.stop();
                logger.info("testFetchMessages finished:" + stopwatch);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    // TODO: handle exception
                }
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } finally {
//            }
		}
//		mp.stop();
	}

	@Test
	public void testFetchMessagesThreads() {
		final CountDownLatch cdl = new CountDownLatch(30);
		for (int j = 0; j < 30; j++) {
			CommonExcutor.getExec().execute(new Runnable() {
				public void run() {
					logger.info("testFetchMessagesThreads threads:");
					MessageConsumer mp = new MessageConsumer();
					mp.start();
					Stopwatch stopwatch = new Stopwatch();
					stopwatch.start();
					try {
						mp.fetchMessage(0, 1024);
						cdl.countDown();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
					}
					stopwatch.stop();
					logger.info("testFetchMessagesThreads finished:"
							+ stopwatch);
					mp.stop();
				}
			});
		}
		try {
			cdl.await();
			Thread.sleep(1000 * 10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++)
			JUnitCore.runClasses(MessageConsumerTest.class);
	}

}
