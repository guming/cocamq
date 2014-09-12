package org.jinn.cocamq.test.zk;

import org.jinn.cocamq.client.ConsumerZookeeper;
import org.junit.Test;

public class CunsumerZookeeperTest {
	@Test
	public void testRegistCunsumer() {
		ConsumerZookeeper pzk=new ConsumerZookeeper("/root");
		try {
			pzk.registerTopicInZk("3001", "comment");
			pzk.start("comment");
			System.out.println(pzk.getMasterBroker("comment").getNodeValue());
			pzk.start("comment");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true){
			try {
				Thread.sleep(15*1000);
				System.out.println(pzk.getMasterBroker("comment"));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
