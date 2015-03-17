package transport.protocol.support.linkage.encoder;

import org.springframework.stereotype.Component;

import transport.buffer.ChannelBuffer;
import transport.buffer.ChannelBuffers;
import transport.protocol.Encoder;
import transport.protocol.Response;
import transport.protocol.annotation.EncoderMapping;
import transport.protocol.support.linkage.MessageId;
import transport.protocol.support.linkage.response.GpsResponse;

@EncoderMapping(MessageId.GPS_RSP)
@Component
public class GpsEncoder implements Encoder{
	@Override
	public ChannelBuffer encode(Response rsp) {
		GpsResponse response = (GpsResponse)rsp;
		ChannelBuffer buffer = ChannelBuffers.directBuffer(1024);
		buffer.writeByte(Integer.valueOf(rsp.getId()));
		buffer.writeByte(response.getContent());
		return buffer;
	}
	
}