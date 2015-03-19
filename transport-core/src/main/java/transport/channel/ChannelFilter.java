package transport.channel;

public interface ChannelFilter{
    
	void setName(String name);
	
	String getName();
	
	ChannelFilter getPrevFilter();
	
	void setPrevFilter(ChannelFilter filter);
	
	ChannelFilter getNextFilter();
	
	void setNextFilter(ChannelFilter filter);
	
	/**********************/
    void channelConnected(Channel channel) throws ChannelException;

    void channelDisconnected(Channel channel) throws ChannelException;

    void messageSent(Channel channel, Object message) throws ChannelException;

    Object messageReceived(Channel channel, Object message) throws ChannelException;

    void exceptionCaught(Channel channel, Throwable exception) throws ChannelException;

    Object filterWrite(Channel channel, Object message) throws ChannelException;
}
