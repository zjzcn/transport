package transport.protocol;

import org.springframework.stereotype.Component;
import transport.buffer.ChannelBuffer;
import transport.buffer.ChannelBuffers;
import transport.protocol.annotation.*;
import transport.util.HexUtils;


@Component
public class LoginHandler {
	@DecoderMapping(protocol = "linkage", msgId = MessageId.LOGIN_REQ, version = "1.0")
	public Request decode(Message msg) {
        LoginRequest req = new LoginRequest(msg);
        ChannelBuffer buffer = msg.getBody();
		byte[] b = new byte[buffer.readableBytes()];
		buffer.readBytes(b);
		System.out.println("body:"+HexUtils.byte2hex(b));
		return req;
	}

    @EncoderMapping(protocol = "linkage", msgId = MessageId.LOGIN_RSP, version = "1.0")
    public Message encode(Response rsp) {
        LoginResponse response = (LoginResponse)rsp;
        ChannelBuffer buffer = ChannelBuffers.directBuffer(1024);
        buffer.writeByte(response.getContent());
        rsp.setBody(buffer);
        return rsp;
    }

    @HandlerMapping(protocol = "linkage", msgId = MessageId.LOGIN_REQ, version = "1.0")
    public Response handle(Request request) {
        System.out.println(((LoginRequest)request).getContent());
        LoginResponse response = new LoginResponse();
        response.setContent(2);
        response.setVersion(request.getVersion());
        return response;
    }

    @MessageHeadDecoder(protocol = "linkage")
    public Message decodeHead(ChannelBuffer buffer) {
//        byte[] b = new byte[buffer.readableBytes()];
//        buffer.readBytes(b);
//        System.out.println("body:"+HexUtils.byte2hex(b));
//        LoginRequest req = new LoginRequest();
//        return req;
//
//        ChannelBuffer buffer = (ChannelBuffer)message;
        Message msg = new Message();
        buffer.markReaderIndex();
        int readLen = buffer.readableBytes();
        if (readLen < 4) {
            return new ReplayInputMessage();
        }
        byte[] frameHead = new byte[2];
        buffer.readBytes(frameHead);
        if ((frameHead[0] & 0xFF) == 0xAA && (frameHead[1] & 0xFF) == 0xBB) {
            int len = buffer.readUnsignedShort();
            if (len > readLen) {
                buffer.resetReaderIndex();
                return new ReplayInputMessage();
            }
            byte[] frameTail = new byte[2];
            buffer.getBytes(len - 2, frameTail);
            byte[] id = new byte[2];
            buffer.readBytes(id);
            String msgId = HexUtils.byte2hex(id);
            msg.setMsgId(msgId);
            msg.setVersion("1.0");
            int sno = buffer.readUnsignedShort();
            System.out.println(sno);
            byte[] tmNoBytes = new byte[4];
            buffer.readBytes(tmNoBytes);
            String tmNo = HexUtils.byte2hex(tmNoBytes);
            System.out.println(tmNo);

            ChannelBuffer body = buffer.slice(buffer.readerIndex(), len - 15);
            buffer.skipBytes(len - 12);
            msg.setBody(body);
        }
        return msg;
    }

    @MessageHeadEncoder(protocol = "linkage")
    public ChannelBuffer encodeHead(Message msg) {
        ChannelBuffer buffer = ChannelBuffers.directBuffer(1024);
        buffer.writeByte(Integer.valueOf(msg.getMsgId()));
        buffer.writeBytes(msg.getBody());
        return buffer;

    }
}