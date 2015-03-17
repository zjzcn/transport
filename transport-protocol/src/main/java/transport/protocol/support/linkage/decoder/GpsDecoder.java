package transport.protocol.support.linkage.decoder;

import org.springframework.stereotype.Component;

import transport.buffer.ChannelBuffer;
import transport.protocol.Decoder;
import transport.protocol.Request;
import transport.protocol.annotation.DecoderMapping;
import transport.protocol.support.linkage.MessageId;
import transport.protocol.support.linkage.request.GpsRequest;


@DecoderMapping(MessageId.GPS_REQ)
@Component
public class GpsDecoder implements Decoder{
	
	@Override
	public Request decode(ChannelBuffer buffer) {
		int i = buffer.readByte();
		GpsRequest req = new GpsRequest();
		req.setContent(i);
		return req;
	}
	
}