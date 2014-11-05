package org.jinn.cocamq.protocol.message;

import java.io.Serializable;

/**
 * Created by gumingcn on 2014/11/5.
 */
public class MessageRecieved implements Message,Serializable{

    private int  id;
    private String body;
    private String topic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MessageRecieved(byte[] bytes) {
        String message=new String(bytes);
        int pos = message.indexOf("#");
        if (pos == -1)
        {
            return;
        }
        id = Integer.valueOf(message.substring(0, pos));
        body = message.substring(pos + 1);
    }

    public MessageRecieved(int id, String body, String topic) {
        this.id = id;
        this.body = body;
        this.topic = topic;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        String temp= id +"#"+body;
        return temp;
    }

    @Override
    public String getHeader() {
        return id+"";
    }

    @Override
    public String getPacket() {
       return getMessage();
    }
}
