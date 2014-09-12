package org.jinn.cocamq.storage;

import java.nio.channels.WritableByteChannel;

import org.jinn.cocamq.entity.Message;


public interface MessageStorage {
	
	public void appendMessage(Message msg);
	
//	public Message getMessageById(int msgId);
	
	public void fetchMessagesBeforeByTopic(final WritableByteChannel socketChanel,long offset,final long range, String topics);
	
}
