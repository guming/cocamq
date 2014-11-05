package org.jinn.cocamq.client.consumer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FixedLengthFrameDecoder;
import org.jinn.cocamq.client.producer.MessageProductor;
import org.jinn.cocamq.client.ClientConfig;
import org.jinn.cocamq.protocol.message.Message;
import org.jinn.cocamq.protocol.message.MessagePack;
import org.jinn.cocamq.protocol.command.GetCommand;
import org.jinn.cocamq.protocol.message.MessageRecieved;
import org.jinn.cocamq.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageConsumer {
	
	private final static Logger logger = LoggerFactory
			.getLogger(MessageProductor.class);
	
	String topic;
	
	ConsumerZookeeper cz;
	
	ClientConfig cc=new ClientConfig();
	
//	DirectBuffer db;
	
	final ChannelFactory factory = new NioClientSocketChannelFactory(
			Executors.newSingleThreadExecutor(),
			Executors.newSingleThreadExecutor(), 1);
	
	final ClientBootstrap bootstrap = new ClientBootstrap(factory);
	
	public Channel channel;

    public MessageProcessor messageProcessor;

    MessagePack messagePack=new MessagePack() {
        @Override
        public void convert(byte[] bytes,List<Message> list) {
            try {
                Message msg=new MessageRecieved(bytes);
                list.add(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

	private class ClientHandler extends SimpleChannelHandler {
		@Override
		public void messageReceived(final ChannelHandlerContext ctx,
				final MessageEvent e) throws Exception {
//            cz.updateFetchOffset(topic, 0);
			ChannelBuffer temp=(ChannelBuffer)e.getMessage();
            ChannelBuffer t=temp.copy();
            System.out.println("message received size:"+temp.array().length);
            List<Message> listMsg=new ArrayList<Message>();
            messagePack.unpackMessages(t.array(), cc.getOffset(), cc, listMsg);
            cz.updateFetchOffset(topic, cc.getOffset());
            logger.info("cc getOffset:" + cc.getOffset());
            messageProcessor.processMessages(listMsg);//process logic
		}
	}
	public MessageConsumer() {
		this.topic=PropertiesUtil.getValue("consumer.topics");
		// TODO Auto-generated constructor stub
	}
	public MessageConsumer(String topic) {
		this.topic=topic;
		// TODO Auto-generated constructor stub
	}
    public MessageConsumer(String topic,MessageProcessor mp) {
        this.topic=topic;
        this.messageProcessor = mp;
        // TODO Auto-generated constructor stub
    }
	public void start() {
		cz=new ConsumerZookeeper("/root");
		cz.start(topic);
		String master="";
		try {
			cc=cz.getMasterBroker(topic);
			master=cc.getNodeValue();
			cc.setOffset(cz.readFetchOffset(topic));
			logger.info("connected to master:"+master);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChannelPipeline pipeline = bootstrap.getPipeline();
        pipeline.addLast("framer", new FixedLengthFrameDecoder(1024*4));
        pipeline.addLast("handler", new ClientHandler());
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(
				cc.getHost(), cc.getPort()));
		channel = future.awaitUninterruptibly().getChannel();
		logger.info("connected to server successed");
	}
	public void start(ClientConfig cClient) {
		ChannelPipeline pipeline = bootstrap.getPipeline();
        pipeline.addLast("framer", new FixedLengthFrameDecoder(1024*4));
        pipeline.addLast("handler", new ClientHandler());
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(
				cClient.getHost(), cClient.getPort()));
		channel = future.awaitUninterruptibly().getChannel();
		logger.info("connected to broker successed:"+cClient.getNodeValue());
	}
	public void fetchMessage(final int offset,final int length) {
		GetCommand gcommand=new GetCommand("get",offset,length);
		channel.write(
				ChannelBuffers.wrappedBuffer(gcommand.makeCommand()));
	}

	public void stop() {
		channel.getCloseFuture().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
		logger.info("stop the client successed");
	}
	
	public ClientConfig getCc() {
		return cc;
	}
	public static void main(String[] args) {
		MessageConsumer mp = new MessageConsumer("comment");
		mp.start();
		mp.fetchMessage(mp.getCc().getOffset(),1024*2);
		mp.stop();
	}
}
