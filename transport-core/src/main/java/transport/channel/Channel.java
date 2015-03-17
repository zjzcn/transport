package transport.channel;

import java.net.InetSocketAddress;

public interface Channel {

	   /**
     * get remote address.
     * 
     * @return remote address.
     */
    InetSocketAddress getRemoteAddress();

    /**
     * is connected.
     * 
     * @return connected
     */
    boolean isConnected();

    /**
     * contains attribute.
     * 
     * @param key key.
     * @return has or has not.
     */
    boolean containsAttribute(String key);

    /**
     * get attribute.
     * 
     * @param key key.
     * @return value.
     */
    Object getAttribute(String key);

    /**
     * set attribute.
     * 
     * @param key key.
     * @param value value.
     */
    void setAttribute(String key,Object value);
    
    /**
     * remove attribute.
     * 
     * @param key key.
     */
    void removeAttribute(String key);

    /**
     * clear attribute.
     */
    void clearAttribute();
    /**
     * get local address.
     * 
     * @return local address.
     */
    InetSocketAddress getLocalAddress();
    
    /**
     * send message.
     * 
     * @param message
     * @throws RemotingException
     */
    void send(Object message) throws ChannelException;

    /**
     * send message.
     * 
     * @param message
     * @param sent 是否已发送完成
     */
    void send(Object message, boolean sent) throws ChannelException;

    /**
     * close the channel.
     */
    void close();
    
    /**
     * Graceful close the channel.
     */
    void close(int timeout);
    
    /**
     * is closed.
     * 
     * @return closed
     */
    boolean isClosed();
}
