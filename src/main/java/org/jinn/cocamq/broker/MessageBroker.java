package org.jinn.cocamq.broker;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jinn.cocamq.protocol.message.Message;
import org.jinn.cocamq.storage.DataFileStorage;
import org.jinn.cocamq.storage.MessageStorage;
import org.jinn.cocamq.util.PropertiesUtil;

public class MessageBroker {

	private final static Logger logger = Logger.getLogger(MessageBroker.class);

	private MessageStorage msgStorage;

	private DefaultChannelGroup allChannels;

	private ServerSocketChannelFactory channelFactory;

//	private boolean binary = false;

	public static int port = 15001;
	
	public static int WORK_THREAD_COUNT=1;
	
	final BrokerZooKeeper bzk = new BrokerZooKeeper("/root");

	public MessageBroker() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MessageBroker(MessageStorage msgStorage) {
		this.msgStorage = msgStorage;
	}

	public void start() {
		logger.info("MessageServer starting.....");
//		initWorkThread();
		InetSocketAddress localAddress = new InetSocketAddress(port);

		channelFactory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		allChannels = new DefaultChannelGroup("messageChannelGroup");
		final ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);
//        bootstrap.setOption("child.bufferFactory", HeapChannelBufferFactory.getInstance(ByteOrder.LITTLE_ENDIAN));
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
        bootstrap.setOption("connectTimeoutMillis",3*1000);
        bootstrap.setOption("receiveBufferSize", 1024 * 128);//max buffersize
        bootstrap.setOption("sendBufferSize", 1024 * 128);//max buffersize
        bootstrap.setOption("child.reuseAddress", true);
        bootstrap.setOption("child.receiveBufferSize", 1024 * 128);//max buffersize
        bootstrap.setOption("child.sendBufferSize", 1024 * 128);//max buffersize
        bootstrap.setPipelineFactory(createPipelineFactory(allChannels,msgStorage));
		// ChannelPipelineFactory pipelineFactory;
		// if (binary){
		// //do nothing
		// }else{
		// pipelineFactory = createSimpledPipelineFactory(
		// allChannels);
		// }
		// bootstrap.setPipelineFactory(pipelineFactory);
		final Channel bind = bootstrap.bind(localAddress);

		String brokerId = PropertiesUtil.getValue("broker.id");
		boolean isMaster = Boolean.valueOf(PropertiesUtil
				.getValue("broker.master"));
		try {
			bzk.registerBrokerInZk(brokerId, isMaster);
            bzk.registerBrokerTopicInZk(brokerId, "comment", isMaster);
            bzk.start();
        } catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.error("broker " + brokerId + " register error", e1);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown();
				bind.close().awaitUninterruptibly();
				bootstrap.releaseExternalResources();
			}
		});
	}

	private SimpledPipelineFactory createPipelineFactory(DefaultChannelGroup channelGroup,
			MessageStorage msgStorage) {
		return new SimpledPipelineFactory(channelGroup,msgStorage);
	}

	public void initWorkThread(){
//		for (int i=0;i<WORK_THREAD_COUNT;i++) {
//			WorkThread wt=new WorkThread();
//			wt.setDaemon(true);
//			wt.start();
//		}
	}
	class WorkThread extends Thread{
		public void run(){
			while(true){
				try {
					Message msg=MessageQueue.getBqueue().poll(1, TimeUnit.MINUTES);
					if(msg==null)
						continue;
//					msgStorage.appendMessage(msg);
//					logger.info("msg append......");
				} catch (Exception e) {
					
				}
				if(MessageQueue.getBqueue().size()==0){
					try {
						Thread.sleep(50);
						logger.info("queue is empty!");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	public void shutdown(){
		logger.info("shutdown the broker.");
		try {
			bzk.unRegisterInZk("comment");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("server shutdown error",e);
		}
	}
	public static void main(String[] args) {
		MessageStorage ms=new DataFileStorage("comment");
		MessageBroker mb = new MessageBroker();
		mb.msgStorage=ms;
		mb.start();
	}
}
