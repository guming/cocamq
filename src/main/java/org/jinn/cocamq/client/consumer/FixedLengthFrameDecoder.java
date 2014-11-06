package org.jinn.cocamq.client.consumer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
//import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Created by gumingcn on 2014/11/5.
 */
public class FixedLengthFrameDecoder  extends FrameDecoder {

    private final int frameLength;
    private final boolean allocateFullBuffer;

    /**
     * Calls {@link #FixedLengthFrameDecoder(int, boolean)} with {@code false}
     */
    public FixedLengthFrameDecoder(int frameLength) {
        this(frameLength, false);
    }

    /**
     * Creates a new instance.
     *
     * @param frameLength
     *        the length of the frame
     * @param allocateFullBuffer
     *        {@code true} if the cumulative {@link org.jboss.netty.buffer.ChannelBuffer} should use the
     *        {@link #frameLength} as its initial size
     */
    public FixedLengthFrameDecoder(int frameLength, boolean allocateFullBuffer) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException(
                    "frameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
        this.allocateFullBuffer = allocateFullBuffer;
        System.out.println(this.allocateFullBuffer+","+this.frameLength);
    }

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (buffer.readableBytes() < frameLength) {
            return null;
        } else {
            ChannelBuffer frame = extractFrame(buffer, buffer.readerIndex(), frameLength);
            buffer.skipBytes(frameLength);
            return frame;
        }
    }

    @Override
    protected ChannelBuffer newCumulationBuffer(ChannelHandlerContext ctx, int minimumCapacity) {
        System.out.println(frameLength);
        ChannelBufferFactory factory = ctx.getChannel().getConfig().getBufferFactory();
        if (allocateFullBuffer) {

            return factory.getBuffer(frameLength);
        }
        return super.newCumulationBuffer(ctx, minimumCapacity);
    }
}
