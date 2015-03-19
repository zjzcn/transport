package transport.protocol.support;

import transport.protocol.Bootstrap;

import java.net.SocketAddress;

/**
 * Created by Administrator on 2014/6/19.
 */
public class ClientBootstrap implements Bootstrap {
    @Override
    public String getTransport() {
        return null;
    }

    @Override
    public void setTransport(String transport) {

    }

    @Override
    public String getTcpOrUdp() {
        return null;
    }

    @Override
    public void setTcpOrUdp(String tcpOrUdp) {

    }

    @Override
    public boolean isLog() {
        return false;
    }

    @Override
    public void setLog(boolean log) {

    }

    @Override
    public void startup(SocketAddress address) {

    }

    @Override
    public void shutdown() {

    }
}
