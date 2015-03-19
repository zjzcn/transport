package transport.channel.support.mina;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import transport.channel.Channel;
import transport.channel.ChannelException;
import transport.channel.ChannelHandler;
import transport.channel.support.AbstractChannel;
import transport.util.Assert;

public class MinaChannel extends AbstractChannel {
    private static final Logger logger = LoggerFactory.getLogger(MinaChannel.class);

    private static final String CHANNEL_KEY = MinaChannel.class.getName() + ".CHANNEL";

    private final IoSession     session;

    private MinaChannel(IoSession session){
    	Assert.notNull(session, "mina session == null");

    	this.session = session;
    }

    static MinaChannel getChannel(IoSession session) {
    	Assert.notNull(session, "mina session == null");

    	MinaChannel ret = (MinaChannel) session.getAttribute(CHANNEL_KEY);
    	if (ret == null) {
    		ret = new MinaChannel(session);
    		if (session.isConnected()) {
    			session.setAttribute(CHANNEL_KEY, ret);
    		}
    	}
    	return ret;
    }

    static void removeChannelIfDisconnected(IoSession session) {
    	if (session != null && ! session.isConnected()) {
    		session.removeAttribute(CHANNEL_KEY);
    	}
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
    	return (InetSocketAddress) session.getLocalAddress();
    }

    @Override
    public boolean isConnected() {
    	return session.isConnected();
    }

	@Override
	public InetSocketAddress getLocalAddress() {
		return (InetSocketAddress) session.getLocalAddress();
	}

	@Override
	public void send(Object message, boolean sent) throws ChannelException {
	        boolean success = true;
	        int timeout = 0;
	        try {
	            WriteFuture future = session.write(message);
	            if (sent) {
	                success = future.awaitUninterruptibly(1000);
	            }
	        } catch (Throwable e) {
	            throw new ChannelException(this, "Failed to send message " + message + " to " + getRemoteAddress() + ", cause: " + e.getMessage(), e);
	        }
	        
	        if(!success) {
	            throw new ChannelException(this, "Failed to send message " + message + " to " + getRemoteAddress()
	                    + "in timeout(" + timeout + "ms) limit");
	        }
	}

	@Override
	public void close() {
	        try {
	            removeChannelIfDisconnected(session);
	        } catch (Exception e) {
	            logger.warn(e.getMessage(), e);
	        }
	        try {
	            if (logger.isInfoEnabled()) {
	                logger.info("CLose mina channel " + session);
	            }
	            session.close(false);
	        } catch (Exception e) {
	            logger.warn(e.getMessage(), e);
	        }
	}

	@Override
	public void close(int timeout) {
		close();
	}
}
