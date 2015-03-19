package transport.protocol.cache.support.threadlocal;

import transport.protocol.cache.Cache;
import transport.protocol.cache.support.AbstractCacheFactory;

public class ThreadLocalCacheFactory extends AbstractCacheFactory {

    protected Cache createCache(String name) {
        return new ThreadLocalCache(name);
    }

}
