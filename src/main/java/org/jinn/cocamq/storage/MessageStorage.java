package org.jinn.cocamq.storage;

import java.nio.channels.WritableByteChannel;

import org.jinn.cocamq.protocol.message.Message;


public interface MessageStorage {
	
	public void appendMessage(Message msg);
	
//	public Message getMessageById(int msgId);
	
	public void fetchTopicsMessages(final WritableByteChannel socketChanel,long offset,final long range, String topics);
	
}
