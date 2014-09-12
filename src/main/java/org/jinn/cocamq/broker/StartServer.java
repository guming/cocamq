package org.jinn.cocamq.broker;

import org.jinn.cocamq.storage.MessageStorage;
import org.jinn.cocamq.storage.MsgFileStorage;

public class StartServer {
	public static void main(String[] args) {
		MessageStorage ms=new MsgFileStorage("comment");
		MessageBroker mb = new MessageBroker(ms);
		mb.start();
	}
}
