package org.jinn.cocamq.client;

import java.util.List;

import org.jinn.cocamq.entity.MessageJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageProcessor {
    private final static Logger logger = LoggerFactory
            .getLogger(MessageProductor.class);
	private static final MessageProcessor instance=new MessageProcessor();
	private MessageProcessor(){
		
	}
	public static  MessageProcessor getInstance(){
		return instance;
	}
	public void processMessages(List<MessageJson> msgList){
			logger.info("list size:"+msgList.size());
			//do something else
	}

}
