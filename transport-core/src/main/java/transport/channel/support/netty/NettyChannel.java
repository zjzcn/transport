package transport.channel.support.netty;

import org.jboss.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.channel.ChannelException;
import transport.channel.support.AbstractChannel;
import transport.util.Assert;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NettyChannel extends AbstractChannel {

    private static final Logger logger = LoggerFactory.getLogger(NettyChannel.class);

    private static final ConcurrentMap<org.jboss.netty.channel.Channel, NettyChannel> channelMap = new ConcurrentHashMap<org.jboss.netty.channel.Channel, NettyChannel>();

    private final org.jboss.netty.channel.Channel channel;

    private SocketAddress remoteAddress;

    private NettyChannel(org.jboss.netty.channel.Channel channel){
    	Assert.notNull(channel, "netty channel == null");
     
        this.channel = channel;
    }

    static NettyChannel getChannel(org.jboss.netty.channel.Channel ch) {
    	Assert.notNull(ch, "netty channel == null");

        if (ch == null) {
            return null;
        }
        NettyChannel ret = channelMap.get(ch);
        if (ret == null) {
            NettyChannel nc = new NettyChannel(ch);
            ret = channelMap.putIfAbsent(ch, nc);
            if (ret == null) {
                ret = nc;
            }
        }
        return ret;
    }

    static void removeChannelIfDisconnected(org.jboss.netty.channel.Channel ch) {
        if (ch != null && ! ch.isConnected()) {
            channelMap.remove(ch);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        if(channel.getRemoteAddress() == null){
            return (InetSocketAddress)remoteAddress;
        }
        return (InetSocketAddress) channel.getRemoteAddress();
    }

    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public void send(Object message, boolean sent) throws ChannelException {
        boolean success = true;
        int timeout = 0;
        try {
            System.out.println(getRemoteAddress());
            ChannelFuture future = channel.write(message, getRemoteAddress());
            if (sent) {
//                timeout = getUrl().getPositiveParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
                success = future.await(1000);
            }
            Throwable cause = future.getCause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            throw new ChannelException(this, "Failed to send message " + message + " to " + getRemoteAddress() + ", cause: " + e.getMessage(), e);
        }
        
        if(! success) {
            throw new ChannelException(this, "Failed to send message " + message + " to " + getRemoteAddress()
                    + "in timeout(" + timeout + "ms) limit");
        }
    }


	@Override
	public void close(int timeout) {
		close();
	}

	@Override
    public void close() {
        try {
            removeChannelIfDisconnected(channel);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            clearAttribute();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Close netty channel " + channel);
            }
            channel.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NettyChannel other = (NettyChannel) obj;
        if (channel == null) {
            if (other.channel != null) return false;
        } else if (!channel.equals(other.channel)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "NettyChannel [channel=" + channel + "]";
    }

    //Netty使用UDP时：Channel.getRemoteAddress()返回null，而Event可以返回客户端地址，Channel.isConnected()返回false；
    // 所以在不指定地址时，发送时无法找到客户端地址（NotYetConnectedException）。
    void setRemoteAddress(SocketAddress remoteAddress){
        this.remoteAddress = remoteAddress;
    }
}