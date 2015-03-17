package transport.channel.support;

import transport.channel.ChannelHandler;
import transport.channel.Endpoint;
import transport.channel.FilterChain;

public abstract class AbstractEndpoint implements Endpoint {

	private FilterChain filterChain = new FilterChain();
	
	private ChannelHandler handler;
	
	@Override
	public void setHandler(ChannelHandler handler){
		this.handler = handler;
	}
	
	@Override
	public ChannelHandler getHandler(){
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
}
