package transport.channel.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.channel.Channel;
import transport.channel.ChannelConfig;
import transport.channel.Server;
import transport.util.ExecutorUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

public abstract class AbstractServer extends AbstractEndpoint implements Server {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);

    private InetSocketAddress localAddress;

    private InetSocketAddress bindAddress;

    ExecutorService executor;

    @Override
    public void bind(SocketAddress localAddress) {
        String tcpOrUdp = (String)getChannelConfig().getOption(ChannelConfig.TCP_OR_UDP, "tcp");
        boolean log = (Boolean)getChannelConfig().getOption(ChannelConfig.LOG, false);

        doOpen(localAddress, tcpOrUdp, log);

        this.localAddress = (InetSocketAddress)localAddress;
        logger.info("Server has started! Listening on:" + localAddress);
    }

	@Override
	public InetSocketAddress getLocalAddress() {
		return localAddress;
	}


	@Override
	public void send(Object message, boolean sent) {
        Collection<Channel> channels = getChannels();
        for (Channel channel : channels) {
            if (channel.isConnected()) {
                channel.send(message, sent);
            }
        }
	}

	@Override
	public void close() {
        if (logger.isInfoEnabled()) {
            logger.info("Close " + getClass().getSimpleName() + " bind " + getLocalAddress() + ", export " + getLocalAddress());
        }
        ExecutorUtil.shutdownNow(executor, 100);
        try {
            super.close();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            doClose();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
	}

	@Override
	public void close(int timeout) {
        ExecutorUtil.gracefulShutdown(executor ,timeout);
        close();
	}

    protected abstract void doOpen(SocketAddress localAddress, String tcpOrUdp, boolean log);

    protected abstract void doClose();

}
