package org.jinn.cocamq.protocol.message;

/**
 * Created by gumingcn on 14-9-4.
 */
public interface Message {

    public String getBody();//body:msg data

    public String getMessage();//header+topic+body

    public String getTopic();//topic

    public String getHeader();//header:msgid

    public String getPacket();//header+body
}
