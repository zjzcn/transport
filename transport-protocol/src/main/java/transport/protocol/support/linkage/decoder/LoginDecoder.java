package transport.protocol.support.linkage.decoder;

import org.springframework.stereotype.Component;

import transport.buffer.ChannelBuffer;
import transport.protocol.Decoder;
import transport.protocol.Request;
import transport.protocol.annotation.DecoderMapping;
import transport.protocol.support.linkage.MessageId;
import transport.protocol.support.linkage.request.GpsRequest;
import transport.util.HexUtils;


@DecoderMapping(MessageId.LOGIN_REQ)
@Component
public class LoginDecoder implements Decoder{
	
	@Override
	public Request decode(ChannelBuffer buffer) {
		byte[] b = new byte[buffer.readableBytes()];
		buffer.readBytes(b);
		System.out.println("body:"+HexUtils.byte2hex(b));
		GpsRequest req = new GpsRequest();
		return req;
	}
	
}