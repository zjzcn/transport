package transport.channel;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2014/6/21.
 */
public interface FilterChain {
    ChannelFilter getFilter(String name);

    void addFirst(String name, ChannelFilter filter);

    void addLast(String name, ChannelFilter filter);

    Map<String, ChannelFilter> getFilterMap();

    Iterator<ChannelFilter> iterator();
}
