package transport.cache;

public interface Cache {

    void put(Object key, Object value);

    Object get(Object key);

}
