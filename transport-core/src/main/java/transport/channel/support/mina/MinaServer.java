package transport.channel.support.mina;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.channel.Channel;
import transport.channel.ChannelFilter;
import transport.channel.TransportException;
import transport.channel.support.AbstractServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MinaServer extends AbstractServer {
    
    private static final Logger logger = LoggerFactory.getLogger(MinaServer.class);

    private IoAcceptor acceptor;
    
	@Override
    protected void doOpen(SocketAddress localAddress, String tcpOrUdp, final boolean log){
        //tcp or udp
        if("tcp".equals(tcpOrUdp)){
			acceptor = new NioSocketAcceptor();
        } else if("udp".equals(tcpOrUdp)){
			acceptor =  new NioDatagramAcceptor();
		} else {
			throw new IllegalArgumentException("Option tcpOrUdp value must be 'tcp' or 'udp'!");
		}
        //logging
        if(log){
            acceptor.getFilterChain().addLast("logging",  new LoggingFilter());
        }
        //filters
        Iterator<ChannelFilter> it = super.getFilterChain().iterator();
        while(it.hasNext()){
        	ChannelFilter filter = it.next();
        	acceptor.getFilterChain().addLast(filter.getName(), new MinaFilter(filter));
        }
        //handler
        acceptor.setHandler(new MinaHandler(getChannelHandler()));
        // bind
        try {
            acceptor.bind(localAddress);
        } catch (IOException e) {
            throw new TransportException(e);
        }
	}

    @Override
    protected void doClose() {
        try {
            if (acceptor != null) {
                acceptor.unbind(getLocalAddress());
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        Collection<IoSession> sessions = acceptor.getManagedSessions().values();
        for (IoSession session : sessions) {
            if (session.getRemoteAddress().equals(remoteAddress)) {
                return MinaChannel.getChannel(session);
            }
        }
        return null;
    }

    public Collection<Channel> getChannels() {
        Collection<IoSession> sessions = acceptor.getManagedSessions().values();
        Collection<Channel> channels = new HashSet<Channel>();
        for (IoSession session : sessions) {
            if (session.isConnected()) {
                channels.add(MinaChannel.getChannel(session));
            }
        }
        return channels;
    }
}
