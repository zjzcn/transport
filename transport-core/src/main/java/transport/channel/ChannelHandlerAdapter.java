package transport.channel;


public class ChannelHandlerAdapter implements ChannelHandler {

	@Override
	public void channelConnected(Channel channel) throws ChannelException {

	}

	@Override
	public void channelDisconnected(Channel channel) throws ChannelException {

	}

	@Override
	public void messageSent(Channel channel, Object message) throws ChannelException {

	}

	@Override
    public void messageReceived(Channel channel, Object message) throws ChannelException {

	}

	@Override
	public void exceptionCaught(Channel channel, Throwable exception) throws ChannelException {

	}

}
