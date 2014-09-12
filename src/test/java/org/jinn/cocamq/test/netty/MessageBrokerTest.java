package org.jinn.cocamq.test.netty;

import org.jinn.cocamq.broker.MessageBroker;
import org.jinn.cocamq.storage.MessageStorage;
import org.jinn.cocamq.storage.MsgFileStorage;
import org.junit.Test;

public class MessageBrokerTest {
	@Test
	public void testBroker(){
		MessageStorage ms=new MsgFileStorage("comment");
		MessageBroker mb = new MessageBroker(ms);
		mb.start();
		try {
			Thread.sleep(1000*60*10);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
