package org.jinn.cocamq.broker;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.queue.BufferedWriteHandler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gumingcn on 2014/11/19.
 */
public class AutoFlusher extends BufferedWriteHandler {
    private final AtomicLong bufferSize = new AtomicLong();

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        super.writeRequested(ctx,e);

        ChannelBuffer data = (ChannelBuffer) e.getMessage();
        long newBufferSize = bufferSize.addAndGet(data.readableBytes());

        // Flush the queue if it gets larger than 8KiB.
        if (newBufferSize > 8192) {
            flush();
            bufferSize.set(0);
        }
    }
}
