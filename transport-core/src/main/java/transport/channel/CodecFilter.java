package transport.channel;


public abstract class CodecFilter extends ChannelFilterAdapter{

	@Override
	public Object messageReceived(Channel channel, Object message) throws ChannelException {
		return decode(channel, message);
	}
	
	@Override
	public Object filterWrite(Channel channel, Object message) throws ChannelException {
		return encode(channel, message);
	}
	
	protected abstract Object decode(Channel channel, Object message);
	
	protected abstract Object encode(Channel channel, Object message);
	
}
