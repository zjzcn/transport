package transport.channel;

import java.net.InetSocketAddress;
import java.util.Map;

public interface Endpoint {

	void setFilterChain(FilterChain filterChain);
	
	FilterChain getFilterChain();
	
	void setChannelHandler(ChannelHandler handler);
	
    ChannelHandler getChannelHandler();

    InetSocketAddress getLocalAddress();
    
    void send(Object message);

    void send(Object message, boolean sent);

    void close();
    
    void close(int timeout);
    
    boolean isClosed();

    ChannelConfig getChannelConfig();

}