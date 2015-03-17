package transport.channel;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import transport.util.Assert;

public class FilterChain {

	private final Map<String, ChannelFilter> name2filter = new ConcurrentHashMap<String, ChannelFilter>();
	
	private ChannelFilter headFilter;
	
	private ChannelFilter tailFilter;
	
	public FilterChain(){
		headFilter = new ChannelFilterAdapter();
		tailFilter = new ChannelFilterAdapter();
		
		headFilter.setNextFilter(tailFilter);
		tailFilter.setPrevFilter(headFilter);
	}
	
	public ChannelFilter getFilter(String name){
		return name2filter.get(name);
	}
	
	public void addFirst(String name, ChannelFilter filter){
		checkAddable(name);
		register(headFilter, name, filter);
	}
	
	public void addLast(String name, ChannelFilter filter){
		checkAddable(name);
		register(tailFilter.getPrevFilter(), name, filter);
	}
	
	public Map<String, ChannelFilter> getFilterMap(){
		return name2filter;
	}
	
	public Iterator<ChannelFilter> iterator(){
		return new Iterator<ChannelFilter>() {
			private ChannelFilter cursor = headFilter;
			
			@Override
			public boolean hasNext() {
				return cursor == tailFilter || cursor.getNextFilter() != tailFilter;
			}

			@Override
			public ChannelFilter next() {
				return cursor = cursor.getNextFilter();
			}

			@Override
			public void remove() {
				cursor.getPrevFilter().setNextFilter(cursor.getNextFilter());
				cursor.getNextFilter().setPrevFilter(cursor.getPrevFilter());
				cursor = cursor.getNextFilter();
			}
		};
	}
	
    private void checkAddable(String name) {
    	Assert.isTrue(!name2filter.containsKey(name), "Other filter is using the same name '" + name + "'");
    }
    
    private void register(ChannelFilter prevFilter, String name, ChannelFilter filter) {
        filter.setPrevFilter(prevFilter);
        filter.setNextFilter(prevFilter.getNextFilter());
        prevFilter.getNextFilter().setPrevFilter(filter);
        prevFilter.setNextFilter(filter);
        
        filter.setName(name);
        name2filter.put(name, filter);
    }
    
}