package transport.channel.support;

import transport.channel.Channel;
import transport.channel.ChannelConfig;
import transport.channel.Server;
import transport.util.Assert;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultChannelConfig implements ChannelConfig{

    private Map<String, Object> options = new ConcurrentHashMap<String, Object>();

    @Override
    public Object getOption(String key, Object defaultValue){
        Assert.notNull(key);

        if(options.containsKey(key)){
            return options.get(key);
        }
        return defaultValue;
    }

    @Override
    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public void setOption(String key, Object value){
        Assert.notNull(key);
        Assert.notNull(value);

        options.put(key, value);
    }
}
