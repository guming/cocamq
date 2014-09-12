package org.jinn.cocamq.command;

import org.jinn.cocamq.entity.Message;

import java.io.Serializable;

/**
 * Created by gumingcn on 14-9-5.
 */
public class PutCommand implements Command,Serializable {

    private static String command="set";
    private Message body;

    public PutCommand(Message msg) {
//        String content=msg.getData().toString();
//        int bodylength=msg.getData().length;
//        byte[] prefixBytes=(command+"#"+msg.getId()+"#"+msg.getTopic()+"#"+bodylength+"#").getBytes();
        this.body=msg;
    }

    public PutCommand(String buf) {

        if(buf.indexOf("#")!=-1){

        }
    }

    @Override
    public byte[] makeCommand() {
        // TODO Auto-generated method stub
        return getCommandContent().getBytes();
    }

    @Override
    public String toString() {
        return "Command [command=" + command + "]";
    }

    public String getCommandContent(){
        String content=body.getContent();
        return command+"#"+content.length()+"#"+content+"\n";
    }

    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return command;
    }
}
