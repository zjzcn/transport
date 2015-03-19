package transport.channel.support;

import transport.channel.*;

public abstract class AbstractEndpoint implements Endpoint {

	private FilterChain filterChain = new DefaultFilterChain();
	
	private ChannelHandler handler;

    private ChannelConfig channelConfig = new DefaultChannelConfig();

    private volatile boolean closed;

	@Override
	public void setChannelHandler(ChannelHandler handler){
		this.handler = handler;
	}
	
	@Override
	public ChannelHandler getChannelHandler(){
		return handler;
	}
	
	@Override
	public FilterChain getFilterChain(){
		return filterChain;
	}
	
	@Override
	public void setFilterChain(FilterChain filterChain){
		this.filterChain = filterChain;
	}

    @Override
    public ChannelConfig getChannelConfig(){
        return channelConfig;
    }

    @Override
    public void send(Object message) {
        send(message, false);
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
