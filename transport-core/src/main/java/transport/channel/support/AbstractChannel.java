package transport.channel.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import transport.channel.Channel;
import transport.channel.ChannelException;

public abstract class AbstractChannel implements Channel {

    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    
	@Override
    public void send(Object message) throws ChannelException {
        send(message, false);
    }

	@Override
    public boolean containsAttribute(String key) {
        return attributes.containsKey(key);
    }
    
	@Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

	@Override
    public void setAttribute(String key, Object value) {
        if (value == null) { // The null value unallowed in the ConcurrentHashMap.
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

	@Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
	
	@Override
    public void clearAttribute() {
        attributes.clear();
    }
}
