package org.jinn.cocamq.broker;

import org.jinn.cocamq.storage.DataFileStorage;
import org.jinn.cocamq.storage.MessageStorage;

public class BrokerBootstrap {
    /**
     * simple bootstrap
     * @param args
     */
	public static void main(String[] args) {
		MessageStorage ms=new DataFileStorage("comment");
		MessageBroker mb = new MessageBroker(ms);
		mb.start();
	}
}
