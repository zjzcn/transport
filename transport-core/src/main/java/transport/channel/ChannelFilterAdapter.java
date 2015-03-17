package transport.channel;

import transport.channel.support.AbstractChannelFilter;


public class ChannelFilterAdapter extends AbstractChannelFilter {

	@Override
	public void channelConnected(Channel channel) throws ChannelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelDisconnected(Channel channel) throws ChannelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageSent(Channel channel, Object message) throws ChannelException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object messageReceived(Channel channel, Object message)throws ChannelException {
		return message;
	}

	@Override
	public void exceptionCaught(Channel channel, Throwable exception)
			throws ChannelException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object filterWrite(Channel channel, Object message) throws ChannelException {
		return message;
	}


}
