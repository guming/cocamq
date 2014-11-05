package org.jinn.cocamq.broker;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jinn.cocamq.protocol.message.Message;
import org.jinn.cocamq.protocol.message.MessageSend;
import org.jinn.cocamq.storage.MessageStorage;

public class MessageBrokerHandler extends SimpleChannelUpstreamHandler {
	
	private MessageStorage msgStorage;
	
	private final static Logger logger = Logger.getLogger(MessageBrokerHandler.class);
	DefaultChannelGroup channelGroup;
	
	public MessageBrokerHandler(MessageStorage msgStorage,DefaultChannelGroup channelGroup) {
		super();
		this.msgStorage = msgStorage;
		this.channelGroup=channelGroup;
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		super.channelOpen(ctx, e);
		channelGroup.add(ctx.getChannel());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		String temp="";
		try {
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();
//			logger.info("ctx id:"+ctx.getChannel().getId()+",ctx address:"+ctx.getChannel().getRemoteAddress());
			temp = buf.toString(Charset.defaultCharset());
			RequestMessage rpm = new RequestMessage(temp);
			String cmd=rpm.getCmd();
            if (cmd != null && cmd.equals("login")) {
				sendResponse(ctx);
			}else if(cmd.equals("set")){
                Message msg=new MessageSend(rpm.getBody());
                msgStorage.appendMessage(msg);
			}
			else if(cmd.equals("get")){
				SimpleWritableByteChannel swb=new SimpleWritableByteChannel();
				swb.channel=e.getChannel();
                msgStorage.fetchTopicsMessages(swb, rpm.getOffset(), rpm.getFetch_size(), "comment");
				// Channels.fireMessageReceived(e.getChannel(),buf);
			}else{
				logger.warn("do nothing:"+e.getRemoteAddress());
			}
		} catch (Exception ex) {
//			logger.error("messageReceived error:" + e.getRemoteAddress(),
//					ex);
			logger.error("messageReceived error,the msg is:" +temp);
			throw ex;
		}
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		logger.info("connected : " + e.getChannel().getRemoteAddress());
		super.channelConnected(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelClosed(ctx, e);
		logger.info("channelClosed : " + e.getChannel().getRemoteAddress());
		channelGroup.remove(ctx.getChannel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		logger.error(e.getCause().getMessage(), e.getCause());
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

	private void sendResponse(ChannelHandlerContext ctx) {
		Channel channel = ctx.getChannel();
		ResponseMessage response = new ResponseMessage();
		response.setMessage("u have been connected:"
				+ System.currentTimeMillis());
		response.setCmd("reply");
		channel.write(response.toString()); // send reply
	}
}
