package org.jinn.cocamq.entity;

import java.io.Serializable;

/**
 * Created by gumingcn on 14-9-3.
 */
public class MessageBytes implements Message,Serializable{

    private int  id;
    private String data;
    private String topic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public MessageBytes(byte[] bytes) {
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
        data = body_str.substring(pos+1);


    }

    public MessageBytes(int id, String data, String topic) {
        this.id = id;
        this.data = data;
        this.topic = topic;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTypic(String typic) {
        this.topic = typic;
    }

    public String getContent() {
        String temp= id +"#"+topic+"#"+data;
        return temp;
    }
}
