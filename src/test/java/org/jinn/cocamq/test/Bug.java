 package org.jinn.cocamq.test;
 
 import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.DirectChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictor;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioSocketChannelConfig;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
 
 /**
  * Bug
  *
  * @author <a href=mailto:jushi@taobao.com>jushi</a>
  * @created 2010-8-17
  *
  */
 public class Bug {
 
     private static class ClientHandler extends SimpleChannelHandler {
         @Override
         public void messageReceived(final ChannelHandlerContext ctx,
                                     final MessageEvent e) throws Exception {
             ChannelBuffer buf = (ChannelBuffer) e.getMessage();
//             i (buf.readableBytes()) {
            	 System.out.print(new String(buf.array()));
//             }
             System.out.println();
             e.getChannel().close();
         }
     }
 
     private static class ServerHandler extends SimpleChannelHandler {
         @Override
         public void messageReceived(final ChannelHandlerContext ctx,
                                     final MessageEvent e) throws Exception {
             TimeUnit.SECONDS.sleep(1); // delay for buffer rewrite.
             e.getChannel().write(e.getMessage());
         }
 
         @Override
         public void channelConnected(ChannelHandlerContext ctx,
                                      ChannelStateEvent e) throws Exception {
             System.out.println("connected : " + e.getChannel());
             NioSocketChannelConfig config =
                 (NioSocketChannelConfig) e.getChannel().getConfig();
             config.setBufferFactory(new DirectChannelBufferFactory()); // zero-copy
             config.setReceiveBufferSizePredictor(new FixedReceiveBufferSizePredictor(10)); // fix buffer size requirement
         }
 
         @Override
         public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
             System.out.println("closed : " + e.getChannel());
         }
     }
 
     static void serve() {
         final ChannelFactory factory =
             new NioServerSocketChannelFactory(Executors.newSingleThreadExecutor(),
                                               Executors.newSingleThreadExecutor(),
                                               1);
         final ServerBootstrap bootstrap = new ServerBootstrap(factory);
 
         ChannelPipeline pipeline = bootstrap.getPipeline();
         pipeline.addLast("execution",
                          new ExecutionHandler(Executors.newCachedThreadPool())); // async message received
         pipeline.addLast("handler", new ServerHandler());
         pipeline.addLast("encoder",new StringEncoder());  
         pipeline.addLast("decoder",new StringDecoder());
         bootstrap.setOption("child.tcpNoDelay", true);
         bootstrap.setOption("child.keepAlive", true);
         final Channel bind = bootstrap.bind(new InetSocketAddress(8080));
 
         Runtime.getRuntime().addShutdownHook(new Thread() {
             @Override
             public void run() {
                 System.out.println("shutdown");
                 bind.close().awaitUninterruptibly();
                 bootstrap.releaseExternalResources();
             }
         });
     }
 
     static void connect() {
         final ChannelFactory factory =
            new NioClientSocketChannelFactory(Executors.newSingleThreadExecutor(),
                                              Executors.newSingleThreadExecutor(),
                                              1);

        final ClientBootstrap bootstrap = new ClientBootstrap(factory);

        ChannelPipeline pipeline = bootstrap.getPipeline();
        pipeline.addLast("handler", new ClientHandler());
        pipeline.addLast("encoder",new StringDecoder());  
        pipeline.addLast("decoder",new StringDecoder());
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        ChannelFuture future =
            bootstrap.connect(new InetSocketAddress("localhost", 8080));
        Channel channel = future.awaitUninterruptibly().getChannel();
        channel.write(ChannelBuffers.wrappedBuffer("++++++++++".getBytes())).awaitUninterruptibly();
        channel.write(ChannelBuffers.wrappedBuffer("----------".getBytes()))
               .awaitUninterruptibly();
        channel.write(ChannelBuffers.wrappedBuffer("==========".getBytes()))
               .awaitUninterruptibly();
        channel.getCloseFuture().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }

    public static void main(String[] args) {
        serve();
        connect();
        System.exit(0);
    }
}