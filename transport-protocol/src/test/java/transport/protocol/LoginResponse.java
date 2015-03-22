package transport.protocol;

public class LoginResponse extends Response {
	
	private int content;

	public LoginResponse(){
		setMsgId(MessageId.LOGIN_RSP);
	}

	public int getContent() {
		return content;
	}

	public void setContent(int content) {
		this.content = content;
	}
}
