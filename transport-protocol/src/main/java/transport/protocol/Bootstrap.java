package transport.protocol;


import transport.channel.ChannelHandler;
import transport.channel.FilterChain;

import java.net.SocketAddress;
import java.util.Map;

public interface Bootstrap {

    String getTransport();

    void setTransport(String transport);

    String getTcpOrUdp();

    void setTcpOrUdp(String tcpOrUdp);

    boolean isLog();

    void setLog(boolean log);

    void startup(SocketAddress address);

    void shutdown();

}
