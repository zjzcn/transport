package transport.protocol;

import transport.buffer.ChannelBuffer;


public interface Encoder {

	ChannelBuffer encode(Response response);
}
