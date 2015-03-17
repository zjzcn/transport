package transport.protocol.support.linkage;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import transport.buffer.ChannelBuffer;
import transport.channel.Channel;
import transport.channel.CodecFilter;
import transport.protocol.Decoder;
import transport.protocol.Encoder;
import transport.protocol.Packet;
import transport.protocol.Request;
import transport.protocol.Response;
import transport.util.HexUtils;

public class TestCodecFilter extends CodecFilter{
	
	private static final Logger logger = LoggerFactory.getLogger(TestCodecFilter.class);
	
	private Map<String, Encoder> encoders;

	private Map<String, Decoder> decoders;
	
	public TestCodecFilter(Map<String, Encoder> encoders, Map<String, Decoder> decoders){
		this.encoders = encoders;
		this.decoders = decoders;
	}
	
	@Override
	protected Object decode(Channel channel, Object message) {
		if(message instanceof ChannelBuffer) {
			ChannelBuffer buffer = (ChannelBuffer)message;
			buffer.markReaderIndex();
			int readLen = buffer.readableBytes();
			if(readLen < 4){
				return FilterResult.REPLAY_INPUT;
			}
			byte[] frameHead = new byte[2];
			buffer.readBytes(frameHead);
			if((frameHead[0]&0xFF)==0xAA && (frameHead[1]&0xFF)==0xBB){
				int len = buffer.readUnsignedShort();
				if(len > readLen){
					buffer.resetReaderIndex();
					return FilterResult.REPLAY_INPUT;
				}
				byte[] frameTail = new byte[2];
				buffer.getBytes(len-2, frameTail);
//				if((frameTail[0]&0xFF)!=0x99 && (frameTail[1]&0xFF)!=0xFF){
//					while(buffer.readable()){
//						buffer.markReaderIndex();
//						if((buffer.readByte()&0xFF) == 0xAA){
//							
//						}
//						if()
//					}
//					return ChannelFilter.FilterResult.REPLAY_INPUT;
//				}
				byte[] id = new byte[2];
				buffer.readBytes(id);
				String msgId = HexUtils.byte2hex(id);
				int sno = buffer.readUnsignedShort();
				System.out.println(sno);
				byte[] tmNoBytes = new byte[4];
				buffer.readBytes(tmNoBytes);
				String tmNo = HexUtils.byte2hex(tmNoBytes);
				System.out.println(tmNo);
				
				Decoder decoder = decoders.get(msgId);
				
				ChannelBuffer body = buffer.slice(buffer.readerIndex(), len-15);
				buffer.skipBytes(len-12);
				Request request = decoder.decode(body);
				return request;
			} else {
				logger.error("Frame start flag not 0xAABB");
			}
			
		}
		
		System.out.println("in:I am a test codec filter");
		return message;
	}

	@Override
	protected Object encode(Channel channel, Object message) {
		System.out.println("out:I am a test codec filter");
		return message;
	}
	
}