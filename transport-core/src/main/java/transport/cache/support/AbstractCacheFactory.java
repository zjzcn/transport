package transport.cache.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import transport.cache.Cache;
import transport.cache.CacheFactory;

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
