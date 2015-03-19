package transport.protocol.support;

import org.springframework.context.ApplicationContext;
import transport.channel.*;
import transport.channel.support.DefaultFilterChain;
import transport.context.BeanFactory;
import transport.protocol.Bootstrap;
import transport.protocol.ProtocolContext;

import java.net.SocketAddress;

/**
 * Created by Administrator on 2014/6/19.
 */
public class ServerBootstrap implements Bootstrap {

    private ProtocolContext context;

    private String transport = "mina";

    private String tcpOrUdp = "tcp";

    private boolean log = true;

    public ServerBootstrap(String protocol, ApplicationContext ctx){
        context = new DefaultProtocolContext(protocol, ctx);
    }

    @Override
    public void startup(SocketAddress address) {
        Server server = BeanFactory.getInstance(Server.class).getBean(transport);
        server.setFilterChain(buildFilterChain());
        server.setChannelHandler(buildChannelHandler());
        ChannelConfig config = server.getChannelConfig();
        config.setOption(ChannelConfig.TCP_OR_UDP, tcpOrUdp);
        config.setOption(ChannelConfig.LOG, log);
        server.bind(address);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public String getTransport() {
        return transport;
    }

    @Override
    public void setTransport(String transport) {
        this.transport = transport;
    }

    @Override
    public String getTcpOrUdp() {
        return tcpOrUdp;
    }

    @Override
    public void setTcpOrUdp(String tcpOrUdp) {
        this.tcpOrUdp = tcpOrUdp;
    }

    @Override
    public boolean isLog() {
        return log;
    }

    @Override
    public void setLog(boolean log) {
        this.log = log;
    }


    private FilterChain buildFilterChain() {
        FilterChain chain = new DefaultFilterChain();
        chain.addLast("headCodec", new MessageHeadCodecFilter(context));
        chain.addLast("bodyCodec", new MessageBodyCodecFilter(context));
        return chain;
    }

    private ChannelHandler buildChannelHandler() {
        return new MessageChannelHandler(context);
    }
}
