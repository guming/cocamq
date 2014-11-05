package org.jinn.cocamq.client.consumer;

import java.util.List;

import org.jinn.cocamq.client.producer.MessageProductor;
import org.jinn.cocamq.protocol.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageProcessor {
    private final static Logger logger = LoggerFactory
            .getLogger(MessageProductor.class);
	private static final MessageProcessor instance=new MessageProcessor();
	public MessageProcessor(){
		
	}
	public static  MessageProcessor getInstance(){
		return instance;
	}
	public void processMessages(List<Message> msgList){
			logger.info("list size:"+msgList.size());
			//do something else
	}
}
