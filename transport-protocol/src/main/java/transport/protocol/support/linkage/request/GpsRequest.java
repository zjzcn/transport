package transport.protocol.support.linkage.request;

import transport.protocol.Request;
import transport.protocol.support.linkage.MessageId;

public class GpsRequest extends Request {
	
	private int content;
	
	public GpsRequest(){
		setId(MessageId.GPS_REQ);
	}

	public int getContent() {
		return content;
	}

	public void setContent(int content) {
		this.content = content;
	}
}
