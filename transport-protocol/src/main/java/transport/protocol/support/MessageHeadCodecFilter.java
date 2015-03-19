package transport.protocol.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.buffer.ChannelBuffer;
import transport.channel.Channel;
import transport.channel.CodecFilter;
import transport.protocol.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MessageHeadCodecFilter extends CodecFilter {

    private static final Logger logger = LoggerFactory.getLogger(MessageHeadCodecFilter.class);

    private ProtocolContext context;

    public MessageHeadCodecFilter(ProtocolContext context){
        this.context = context;
    }

    @Override
    protected Object decode(Channel channel, Object message) {
        if (!(message instanceof ChannelBuffer)) {
            throw new ProtocolException(this.getName() + "decode input must ChannelBuffer");
        }

        ChannelBuffer buffer = (ChannelBuffer) message;
        MethodInvoker invoker = context.getHeadDecoderMethod();

        Message ret = (Message)invoker.invoke(message);

        if(ret instanceof ReplayInputMessage){
            return CodecResult.REPLAY_INPUT;
        }
        return ret;
    }

    @Override
    protected Object encode(Channel channel, Object message) {
        if(!(message instanceof Message)){
            throw new ProtocolException(this.getName() + "encode input must Message");
        }
        Message msg = (Message) message;
        MethodInvoker invoker = context.getHeadEncoderMethod();
        ChannelBuffer buffer = (ChannelBuffer)invoker.invoke(msg);
        return buffer;
    }

}