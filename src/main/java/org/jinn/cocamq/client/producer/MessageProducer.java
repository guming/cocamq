package org.jinn.cocamq.client.producer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jinn.cocamq.protocol.command.PutCommand;
import org.jinn.cocamq.client.ClientConfig;
import org.jinn.cocamq.protocol.message.Message;

public class MessageProducer {
	
	private final static Logger logger = Logger.getLogger(MessageProducer.class);
	
	final ChannelFactory factory = new NioClientSocketChannelFactory(
			Executors.newCachedThreadPool(),
			Executors.newCachedThreadPool());
	
	final ClientBootstrap bootstrap = new ClientBootstrap(factory);
	ProductorZookeeper pz;
	ClientConfig cc=new ClientConfig();
	public Channel channel;
	public ChannelFuture future;
	private static class ClientHandler extends SimpleChannelUpstreamHandler {
//		final ClientBootstrap bootstrap;
//	    private final Timer timer;
//	    
//		public ClientHandler(ClientBootstrap bootstrap, Timer timer) {
//			super();
//			this.bootstrap = bootstrap;
//			this.timer = timer;
//		}

		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {
			// TODO Auto-generated method stub
			super.channelClosed(ctx, e);
		}

		@Override
		public void messageReceived(final ChannelHandlerContext ctx,
				final MessageEvent e) throws Exception {
			logger.info(e.getMessage());
			e.getChannel().close();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
				throws Exception {
			// TODO Auto-generated method stub
			super.exceptionCaught(ctx, e);
			e.getCause().printStackTrace();
			e.getChannel().close();
		}
		
	}

	public void start() {
		pz=new ProductorZookeeper("/root");
		pz.start("comment");
		String master="";
		try {
			cc=pz.getMasterBroker("comment");
			master=cc.getNodeValue();
			logger.info("connected to master:"+master);
			logger.info("connected to host:"+cc.getHost()+",port:"+cc.getPort());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChannelPipeline pipeline = bootstrap.getPipeline();
	    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
	                1024*64, Delimiters.lineDelimiter()));
		pipeline.addLast("handler", new ClientHandler());
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("connectTimeoutMillis",3*1000);
		future = bootstrap.connect(new InetSocketAddress(
				cc.getHost(), cc.getPort()));
		channel = future.awaitUninterruptibly().getChannel();
		logger.info("connected to server successed");
	}

	public void sendMessage(Message msg) {
        PutCommand pc=new PutCommand(msg);
		if(channel.isOpen()||channel.isConnected()){
			channel.write(
					ChannelBuffers.wrappedBuffer(pc.makeCommand()))
					;
		}
		else {
			logger.warn("the channel is closed:"+channel.getRemoteAddress());
		}
	}

	public void stop() {
		channel.getCloseFuture().awaitUninterruptibly(3*1000);
		bootstrap.releaseExternalResources();
		logger.info("stop the client successed");
	}

	public static void main(String[] args) {
//		MessageProductor mp = new MessageProductor();
//		mp.start();
//		mp.sendMessage();
//		mp.stop();
	}
}
