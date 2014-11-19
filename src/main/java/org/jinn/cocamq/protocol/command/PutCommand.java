package org.jinn.cocamq.protocol.command;

import org.jinn.cocamq.protocol.message.Message;

import java.io.Serializable;

/**
 * Created by gumingcn on 14-9-5.
 */
public class PutCommand implements Command,Serializable {

    private static String command="set";
    private Message body;

    public PutCommand(Message msg) {
        this.body=msg;
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
        String content=body.getMessage();
        return command+" "+content.length()+" "+content+"\r\n";
    }

    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return command;
    }
}
