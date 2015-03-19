package transport.protocol.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transport.channel.Channel;
import transport.channel.ChannelException;
import transport.channel.ChannelHandlerAdapter;
import transport.protocol.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MessageChannelHandler extends ChannelHandlerAdapter{

    private final static Logger logger = LoggerFactory.getLogger(MessageChannelHandler.class);

	private ProtocolContext context;

	public MessageChannelHandler(ProtocolContext context){
		this.context = context;
	}
	
	@Override
    public void messageReceived(Channel channel, Object message) throws ChannelException {
		Request request = (Request)message;
        MethodInvoker invoker = context.getHandlerMethod(request.getMsgId(), request.getVersion());

		Response response = (Response)invoker.invoke(request);

		channel.send(response);
	}

    private Response invokeHandler(MethodInvoker holder, Request request) throws InvocationTargetException, IllegalAccessException {
        Object target = holder.getTarget();
        Method method = holder.getMethod();
        Response response = (Response)method.invoke(target, request);
        logger.debug("Executed handler method invokeHandler:" + target.getClass().getName() + "." + method.getName());

        return response;
    }
}