package org.jinn.cocamq.client.consumer;

import org.jinn.cocamq.protocol.message.Message;

import java.util.List;

/**
 * Created by gumiingcn on 2014/11/5.
 */
public interface MessageProcessor {
    public void processMessages(List<Message> msgList);
}
