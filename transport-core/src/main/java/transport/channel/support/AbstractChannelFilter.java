package transport.channel.support;

import transport.channel.ChannelFilter;


public abstract class AbstractChannelFilter implements ChannelFilter {
	
	private String name;
	
	private ChannelFilter prevFilter;
	
	private ChannelFilter nextFilter;

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public ChannelFilter getPrevFilter() {
		return prevFilter;
	}

	@Override
	public void setPrevFilter(ChannelFilter filter) {
		this.prevFilter = filter;
	}

	@Override
	public ChannelFilter getNextFilter() {
		return nextFilter;
	}

	@Override
	public void setNextFilter(ChannelFilter filter) {
		this.nextFilter = filter;
	}
	
}
