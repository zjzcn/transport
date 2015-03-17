package transport.channel.support.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import transport.channel.ChannelHandler;
import transport.channel.support.mina.MinaChannel;
import transport.util.Assert;

final class NettyHandler extends SimpleChannelHandler {

    private final ChannelHandler handler;
    
    public NettyHandler(ChannelHandler handler) {
    	Assert.notNull(handler, "handler == null");
        this.handler = handler;
    }
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
    	System.out.println("------");
        NettyChannel channel = NettyChannel.getChannel(ctx.getChannel());
        try {
            handler.messageReceived(channel, e.getMessage());
        } finally {
        	NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
        }
    }
}
