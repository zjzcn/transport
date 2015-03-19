package transport.protocol;

import transport.buffer.ChannelBuffer;


public class Message {

	private String msgId;

    private String version;

    private ChannelBuffer body;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ChannelBuffer getBody() {
        return body;
    }

    public void setBody(ChannelBuffer body) {
        this.body = body;
    }
}
