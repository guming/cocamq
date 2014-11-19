package org.jinn.cocamq.protocol.command;

/**
 * Created by gumingcn on 2014/11/14.
 */
public class DataCommand implements Command {
    @Override
    public byte[] makeCommand() {
        return new byte[0];
    }

    @Override
    public String getCommand() {
        return null;
    }
}
