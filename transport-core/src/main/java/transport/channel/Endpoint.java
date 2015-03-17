package transport.channel;

import java.net.InetSocketAddress;

public interface Endpoint {

	void setFilterChain(FilterChain filterChain);
	
	FilterChain getFilterChain();
	
	void setHandler(ChannelHandler handler);
	
    ChannelHandler getHandler();

    InetSocketAddress getLocalAddress();
    
    void send(Object message);

    void send(Object message, boolean sent);

    void close();
    
    void close(int timeout);
    
    boolean isClosed();

}