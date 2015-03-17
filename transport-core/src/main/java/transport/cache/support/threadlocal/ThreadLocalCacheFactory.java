package transport.cache.support.threadlocal;

import transport.cache.Cache;
import transport.cache.support.AbstractCacheFactory;

public class ThreadLocalCacheFactory extends AbstractCacheFactory {

    protected Cache createCache(String name) {
        return new ThreadLocalCache(name);
    }

}
