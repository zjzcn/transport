package transport.channel.support.netty;

import java.nio.ByteBuffer;

import org.jboss.netty.channel.*;

import transport.buffer.ChannelBuffer;
import transport.buffer.ChannelBuffers;
import transport.buffer.DynamicChannelBuffer;
import transport.channel.ChannelFilter;
import transport.channel.CodecFilter;
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
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        NettyChannel channel = NettyChannel.getChannel(ctx.getChannel());
        filter.channelConnected(channel);

        super.channelConnected(ctx, e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        NettyChannel channel = NettyChannel.getChannel(ctx.getChannel());
        filter.channelDisconnected(channel);

        super.channelDisconnected(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent evt){
        NettyChannel channel = NettyChannel.getChannel(ctx.getChannel());
        //Netty使用UDP时：Channel.getRemoteAddress()返回null，而Event可以返回客户端地址，Channel.isConnected()返回false；
        // 所以在不指定地址时，发送时无法找到客户端地址（NotYetConnectedException）。
        channel.setRemoteAddress(evt.getRemoteAddress());
        Object in = evt.getMessage();
        Object msg;
        if (!(in instanceof org.jboss.netty.buffer.ChannelBuffer)) {
        	msg = filter.messageReceived(channel, in);
        	Channels.fireMessageReceived(ctx, msg, evt.getRemoteAddress());
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
        		if (msg == CodecFilter.CodecResult.REPLAY_INPUT) {
        			frame.resetReaderIndex();
        			break;
        		} else {
        			if (msg != null) {
        				Channels.fireMessageReceived(ctx, msg, evt.getRemoteAddress());
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
        Object originalMessage = evt.getMessage();
        NettyChannel channel = NettyChannel.getChannel(ctx.getChannel());
        Object handledMessage = filter.filterWrite(channel, originalMessage);
        if (originalMessage == handledMessage) {
            ctx.sendDownstream(evt);
        } else if (handledMessage != null) {
        	if(handledMessage instanceof ChannelBuffer){
        		ByteBuffer buf = ((ChannelBuffer)handledMessage).toByteBuffer();
        		org.jboss.netty.buffer.ChannelBuffer nettyBuf = org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer(buf);
        		
        		Channels.write(ctx, evt.getFuture(), nettyBuf, evt.getRemoteAddress());
        	} else {
        		Channels.write(ctx, evt.getFuture(), handledMessage, evt.getRemoteAddress());
        	}
        }
    }
}
