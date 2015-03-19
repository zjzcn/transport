package transport.channel;

import java.lang.reflect.InvocationTargetException;

public interface ChannelHandler {
	
    void channelConnected(Channel channel) throws ChannelException;

    void channelDisconnected(Channel channel) throws ChannelException;

    /**
     * on message sent.
     * 
     * @param channel channel.
     * @param message message.
     */
    void messageSent(Channel channel, Object message) throws ChannelException;

    /**
     * on message received.
     *  @param channel channel.
     * @param message message.
     */
    void messageReceived(Channel channel, Object message) throws ChannelException;

    /**
     * on exception caught.
     * 
     * @param channel channel.
     * @param exception exception.
     */
    void exceptionCaught(Channel channel, Throwable exception) throws ChannelException;
}
