package org.jinn.cocamq.broker;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jinn.cocamq.storage.MessageStorage;

public class SimpledPipelineFactory implements ChannelPipelineFactory {

	private DefaultChannelGroup channelGroup;

	private MessageStorage msgStorage;

	public SimpledPipelineFactory(DefaultChannelGroup channelGroup,
			MessageStorage msgStorage) {
		super();
		this.channelGroup = channelGroup;
		this.msgStorage = msgStorage;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline pipeline = Channels.pipeline();
        AutoFlusher bufferedWriter=new AutoFlusher();
//        pipeline.addFirst("buffer", bufferedWriter);
//		pipeline.addLast("execution",
//				new ExecutionHandler(Executors.newCachedThreadPool()));//recv error,buffer lost,single server
		pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(
				1024 * 64, Delimiters.lineDelimiter()));
		pipeline.addLast("executor", new ExecutionHandler(
				new OrderedMemoryAwareThreadPoolExecutor(8, 1048576, 1048576)));
		pipeline.addLast("handler", new MessageBrokerHandler(msgStorage,
				channelGroup));
		return pipeline;
	}

}
