package org.jinn.cocamq.test.zk;

import org.jinn.cocamq.broker.BrokerZooKeeper;
import org.junit.Test;

public class BrokerZookeeperTest {
	@Test
	public void testRegistBroker() {
		BrokerZooKeeper bzk=new BrokerZooKeeper("/root");
		try {
			bzk.registerBrokerInZk("1001", true);
			bzk.registerBrokerTopicInZk("1001", "comment", true);
			bzk.registerBrokerInZk("1002", true);
			bzk.registerBrokerTopicInZk("1002", "comment", true);
			bzk.start();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Thread.sleep(10*1000);
			bzk.unRegisterInZk("comment");
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bzk.registerBrokerInZk("1001", true);
			bzk.registerBrokerTopicInZk("1001", "comment", true);
			Thread.sleep(5*60*1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
