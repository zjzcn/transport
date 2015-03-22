package transport.protocol;

public class LoginRequest extends Request {
	
	private int content;
	
	public LoginRequest(){
		super.setMsgId(MessageId.LOGIN_REQ);
	}

    public LoginRequest(Message msg){
        this();
        super.setVersion(msg.getVersion());
        super.setBody(msg.getBody());
    }

	public int getContent() {
		return content;
	}

	public void setContent(int content) {
		this.content = content;
	}
}
