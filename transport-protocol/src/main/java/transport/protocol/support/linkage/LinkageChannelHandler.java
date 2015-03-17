package transport.protocol.support.linkage;

import java.util.Map;

import transport.channel.Channel;
import transport.channel.ChannelHandlerAdapter;
import transport.protocol.Handler;
import transport.protocol.Request;
import transport.protocol.Response;

public class LinkageChannelHandler extends ChannelHandlerAdapter{

	private Map<String, Handler> handlers;

	public LinkageChannelHandler(Map<String, Handler> handlers){
		this.handlers = handlers;
	}
	
	@Override
	public void messageReceived(Channel channel, Object message){
		Request request = (Request)message;
		Handler handler = handlers.get(request.getId());
		Response response = handler.handle(request);

		channel.send(response);
	}
}