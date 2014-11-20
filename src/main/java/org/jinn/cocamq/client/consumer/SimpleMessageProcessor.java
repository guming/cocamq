package org.jinn.cocamq.client.consumer;

import java.util.List;

import org.jinn.cocamq.client.producer.MessageProducer;
import org.jinn.cocamq.protocol.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMessageProcessor implements MessageProcessor{
    private final static Logger logger = LoggerFactory
            .getLogger(MessageProducer.class);
	private static final SimpleMessageProcessor instance=new SimpleMessageProcessor();
	public SimpleMessageProcessor(){
		
	}
	public static SimpleMessageProcessor getInstance(){
		return instance;
	}
	public void processMessages(List<Message> msgList){
			logger.info("list size:"+msgList.size());
			//do something else
	}
}
