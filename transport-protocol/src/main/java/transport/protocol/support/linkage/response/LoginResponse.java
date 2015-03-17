package transport.protocol.support.linkage.response;

import transport.protocol.Response;
import transport.protocol.support.linkage.MessageId;

public class LoginResponse extends Response {
	
	private int content;

	public LoginResponse(){
		setId(MessageId.GPS_RSP);
	}

	public int getContent() {
		return content;
	}

	public void setContent(int content) {
		this.content = content;
	}
}
