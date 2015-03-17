package transport.channel;


public abstract class EncoderFilter extends ChannelFilterAdapter{

	@Override
	public Object filterWrite(Channel channel, Object message) throws ChannelException {
		return encode(channel, message);
	}
	
	protected abstract Object encode(Channel channel, Object message);
	
}
