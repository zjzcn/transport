package transport.protocol;

import transport.buffer.ChannelBuffer;



public interface Decoder {

	Request decode(ChannelBuffer buffer);
}
