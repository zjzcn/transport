package transport.protocol.support.linkage.handler;

import org.springframework.stereotype.Component;

import transport.protocol.Handler;
import transport.protocol.Request;
import transport.protocol.Response;
import transport.protocol.annotation.HandlerMapping;
import transport.protocol.support.linkage.MessageId;
import transport.protocol.support.linkage.request.GpsRequest;
import transport.protocol.support.linkage.response.GpsResponse;

@HandlerMapping(MessageId.GPS_REQ)
@Component
public class GpsHandler implements Handler{
	
	@Override
	public Response handle(Request request) {
		System.out.println(((GpsRequest)request).getContent());
		GpsResponse response = new GpsResponse();
		response.setContent(2);
		return response;
	}
}