package transport.channel;

import transport.channel.support.AbstractChannelFilter;


public class ChannelFilterAdapter extends AbstractChannelFilter {

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
	public Object messageReceived(Channel channel, Object message)throws ChannelException {
		return message;
	}

	@Override
	public Object filterWrite(Channel channel, Object message) throws ChannelException {
		return message;
	}

    @Override
    public void exceptionCaught(Channel channel, Throwable exception) throws ChannelException {

    }

}
