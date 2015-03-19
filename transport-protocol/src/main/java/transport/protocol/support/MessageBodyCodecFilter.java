package transport.protocol.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.channel.Channel;
import transport.channel.CodecFilter;
import transport.protocol.Message;
import transport.protocol.MethodInvoker;
import transport.protocol.ProtocolContext;
import transport.protocol.ProtocolException;

public class MessageBodyCodecFilter extends CodecFilter{
	
	private static final Logger logger = LoggerFactory.getLogger(MessageBodyCodecFilter.class);
	
	private ProtocolContext context;

	public MessageBodyCodecFilter(ProtocolContext context){
        this.context = context;
    }
	
	@Override
	protected Object decode(Channel channel, Object message) {
        if(!(message instanceof Message)){
            throw new ProtocolException("message must be instance of Message class");
        }

        Message msg = (Message)message;
        MethodInvoker invoker = context.getDecoderMethod(msg.getMsgId(), msg.getVersion());

        return invoker.invoke(msg);
	}

	@Override
	protected Object encode(Channel channel, Object message) {
        if(!(message instanceof Message)){
            throw new ProtocolException("message must be instance of Message class");
        }
        Message msg = (Message)message;
        MethodInvoker invoker = context.getEncoderMethod(msg.getMsgId(), msg.getVersion());

        return invoker.invoke(msg);
	}

}