package transport.channel.support.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import transport.channel.Channel;
import transport.channel.ChannelHandler;
import transport.util.Assert;
import transport.util.NetUtils;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@org.jboss.netty.channel.ChannelHandler.Sharable
final class NettyHandler extends SimpleChannelHandler {

    private final Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>(); // <ip:port, channel>

    private final ChannelHandler handler;
    
    public NettyHandler(ChannelHandler handler) {
    	Assert.notNull(handler, "handler == null");
        this.handler = handler;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        NettyChannel channel = NettyChannel.getChannel(ctx.getChannel());
        try {
            if (channel != null) {
                channels.put(NetUtils.toAddressString((InetSocketAddress) ctx.getChannel().getRemoteAddress()), channel);
            }
            handler.channelConnected(channel);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
    	System.out.println("------");
        NettyChannel channel = NettyChannel.getChannel(ctx.getChannel());
        try {
            handler.messageReceived(channel, e.getMessage());
        } finally {
        	NettyChannel.removeChannelIfDisconnected(ctx.getChannel());
        }
    }

    Map<String, Channel> getChannels(){
        return channels;
    }
}
