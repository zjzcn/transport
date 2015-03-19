package transport.channel.support.netty;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.channel.Channel;
import transport.channel.ChannelFilter;
import transport.channel.support.AbstractServer;
import transport.util.NamedThreadFactory;
import transport.util.NetUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyServer extends AbstractServer{
    
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Map<String, Channel>  channels; // <ip:port, channel>

    private Bootstrap bootstrap;

    private org.jboss.netty.channel.Channel channel;
    
	@Override
    protected void doOpen(SocketAddress localAddress, String tcpOrUdp, final boolean log) {
        ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerWorker", false));
        //tcp or udp
        if("tcp".equals(tcpOrUdp)){
            ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerBoss", false));
            ChannelFactory channelFactory = new NioServerSocketChannelFactory(boss, worker);
            bootstrap = new ServerBootstrap(channelFactory);
        } else if("udp".equals(tcpOrUdp)){
            ChannelFactory channelFactory = new NioDatagramChannelFactory(worker);
            bootstrap = new ConnectionlessBootstrap(channelFactory);
        } else {
            throw new IllegalArgumentException("Option tcpOrUdp value must be 'tcp' or 'udp'!");
        }

        //netty pipeline
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
            	ChannelPipeline pipeline = Channels.pipeline();
                //logging
                if(log){
                    InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
                    pipeline.addLast("logger", new LoggingHandler());
                }
                //filters
                Iterator<ChannelFilter> it = NettyServer.this.getFilterChain().iterator();
                while(it.hasNext()){
                	ChannelFilter filter = it.next();
                	pipeline.addLast(filter.getName(), new NettyFilter(filter));
                }
                //handler
                NettyHandler nettyHandler = new NettyHandler(NettyServer.this.getChannelHandler());
                channels = nettyHandler.getChannels();
                pipeline.addLast("handler", nettyHandler);
                return pipeline;
            }
        });
        // bind
        if(bootstrap instanceof ServerBootstrap){
            channel = ((ServerBootstrap)bootstrap).bind(localAddress);
        } else if(bootstrap instanceof ConnectionlessBootstrap){
            channel = ((ConnectionlessBootstrap)bootstrap).bind(localAddress);
        }
	}

    @Override
    protected void doClose() {
        try {
            if (channel != null) {
                // unbind.
                channel.close();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            Collection<Channel> channels = getChannels();
            if (channels != null && channels.size() > 0) {
                for (Channel channel : channels) {
                    try {
                        channel.close();
                    } catch (Throwable e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            if (bootstrap != null) {
                // release external resource.
                bootstrap.releaseExternalResources();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            if (channels != null) {
                channels.clear();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public Collection<Channel> getChannels() {
        Collection<Channel> chs = new HashSet<Channel>();
        for (Channel channel : this.channels.values()) {
            if (channel.isConnected()) {
                chs.add(channel);
            } else {
                channels.remove(NetUtils.toAddressString(channel.getRemoteAddress()));
            }
        }
        return chs;
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        return channels.get(NetUtils.toAddressString(remoteAddress));
    }

}
