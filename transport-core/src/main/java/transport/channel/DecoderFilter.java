package transport.channel;



public abstract class DecoderFilter extends ChannelFilterAdapter{

	@Override
	public Object messageReceived(Channel channel, Object message) throws ChannelException {
		return decode(channel, message);
	}

	protected abstract Object decode(Channel channel, Object message);
	
}
