package transport.protocol.support.linkage.request;

import transport.protocol.Request;
import transport.protocol.support.linkage.MessageId;

public class LoginRequest extends Request {
	
	private int content;
	
	public LoginRequest(){
		setId(MessageId.GPS_REQ);
	}

	public int getContent() {
		return content;
	}

	public void setContent(int content) {
		this.content = content;
	}
}
