package org.jinn.cocamq.broker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.jboss.netty.buffer.DirectChannelBufferFactory;
import org.jboss.netty.channel.Channel;

public class SimpleWritableByteChannel implements WritableByteChannel {
	Channel channel;
	DirectChannelBufferFactory df=new DirectChannelBufferFactory();
	@Override
	public boolean isOpen() {
		return channel.isOpen();
		// TODO Auto-generated method stub
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		channel.close();
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		// TODO Auto-generated method stub
		channel.write(df.getBuffer(src));
		return src.capacity();
	}

}
