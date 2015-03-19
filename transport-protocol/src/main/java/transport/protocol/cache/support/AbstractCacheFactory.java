package transport.protocol.cache.support;

import transport.protocol.cache.Cache;
import transport.protocol.cache.CacheFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractCacheFactory implements CacheFactory {
    
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();

    public Cache getCache(String name) {
        Cache cache = caches.get(name);
        if (cache == null) {
        	cache = caches.put(name, createCache(name));
        }
        return cache;
    }

    protected abstract Cache createCache(String name);

}
