package transport.channel.support.netty;

import java.nio.ByteBuffer;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import transport.buffer.ChannelBuffer;
import transport.buffer.ChannelBuffers;
import transport.buffer.DynamicChannelBuffer;
import transport.channel.ChannelFilter;
import transport.util.Assert;
import transport.util.HexUtils;

final class NettyFilter extends SimpleChannelHandler{
	
    private final ChannelFilter filter;
    
    private int bufferSize = 1024;
    private ChannelBuffer buffer = ChannelBuffers.EMPTY_BUFFER;
    
    public NettyFilter(ChannelFilter filter) {
        Assert.notNull(filter, "filter == null");
        this.filter = filter;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
        NettyChannel channel = NettyChannel.getChannel(ctx.getChannel());
        Object in = event.getMessage();
        Object msg;
        if (!(in instanceof org.jboss.netty.buffer.ChannelBuffer)) {
        	msg = filter.messageReceived(channel, in);
        	Channels.fireMessageReceived(ctx, msg, event.getRemoteAddress());
            return;
        }

        org.jboss.netty.buffer.ChannelBuffer inBuf = (org.jboss.netty.buffer.ChannelBuffer) in;
        int readable = inBuf.readableBytes();
        if (readable <= 0) {
            return;
        }
        //TODO:debug
    	byte[] b1 = new byte[inBuf.readableBytes()];
    	inBuf.getBytes(0,b1);
    	System.out.println(HexUtils.byte2hex(b1));
        
        ChannelBuffer frame;
        if (buffer.readable()) {
            if (buffer instanceof DynamicChannelBuffer) {
                buffer.writeBytes(inBuf.toByteBuffer());
                frame = buffer;
            } else {
                int size = buffer.readableBytes() + inBuf.readableBytes();
                frame = ChannelBuffers.dynamicBuffer(size>bufferSize ? size : bufferSize);
                frame.writeBytes(buffer, buffer.readableBytes());
                frame.writeBytes(inBuf.toByteBuffer());
            }
        } else {
            frame = ChannelBuffers.wrappedBuffer(inBuf.toByteBuffer());
        }

        try {
        	while (frame.readable()){
        		 //TODO:debug
        		byte[] b = new byte[frame.readableBytes()];
        		frame.getBytes(0,b);
        		System.out.println(HexUtils.byte2hex(b));
        		
        		frame.markReaderIndex();
        		msg = filter.messageReceived(channel, frame);
        		if (msg == ChannelFilter.FilterResult.REPLAY_INPUT) {
        			frame.resetReaderIndex();
        			break;
        		} else {
        			if (msg != null) {
        				Channels.fireMessageReceived(ctx, msg, event.getRemoteAddress());
        			}
        		}
        	} 
        } finally {
        	if (frame.readable()) {
        		frame.discardReadBytes();
        		buffer = frame;
        	} else {
        		buffer = ChannelBuffers.EMPTY_BUFFER;
        	}
        	NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
        }
    }
    
    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent evt) throws Exception {
        MessageEvent e = (MessageEvent) evt;
        Object originalMessage = e.getMessage();
        NettyChannel channel = NettyChannel.getChannel(ctx.getChannel());
        Object handledMessage = filter.filterWrite(channel, originalMessage);
        if (originalMessage == handledMessage) {
            ctx.sendDownstream(evt);
        } else if (handledMessage != null) {
        	if(handledMessage instanceof ChannelBuffer){
        		ByteBuffer buf = ((ChannelBuffer)handledMessage).toByteBuffer();
        		org.jboss.netty.buffer.ChannelBuffer nettyBuf = org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer(buf);
        		
        		Channels.write(ctx, e.getFuture(), nettyBuf, e.getRemoteAddress());
        	} else {
        		Channels.write(ctx, e.getFuture(), handledMessage, e.getRemoteAddress());
        	}
        }
    }
}
