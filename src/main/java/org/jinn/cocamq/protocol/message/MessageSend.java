package org.jinn.cocamq.protocol.message;

import java.io.Serializable;

/**
 * Created by gumingcn on 14-9-3.
 */
public class MessageSend implements Message,Serializable{

    private int  id;
    private String body;
    private String topic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MessageSend(byte[] bytes) {
        String content = new String(bytes);
        int pos = content.indexOf("#");
        if (pos == -1)
        {
            return;
        }
        id = Integer.valueOf(content.substring(0, pos));
        String body_str = content.substring(pos + 1);
        pos = body_str.indexOf("#");
        topic = body_str.substring(0, pos);
        body = body_str.substring(pos+1);
    }

    public MessageSend(int id, String body, String topic) {
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
        String temp= id +"#"+topic+"#"+body;
        return temp;
    }

    @Override
    public String getHeader() {
        return id+"";
    }

    @Override
    public String getPacket() {
        String temp= id +"#"+body;
        return temp;
    }
}
