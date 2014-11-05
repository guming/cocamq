package org.jinn.cocamq.test.zk;

import org.jinn.cocamq.client.producer.ProductorZookeeper;
import org.jinn.cocamq.client.ClientConfig;
import org.junit.Test;

public class ProductorZookeeperTest {
	@Test
	public void testRegistProductor() {
		ProductorZookeeper pzk=new ProductorZookeeper("/root");
		try {
			pzk.registerTopicInZk("2001", "comment");
			pzk.start("comment");
			ClientConfig cc=pzk.getMasterBroker("comment");
			System.out.println("cc:"+cc.getNodeValue());
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
