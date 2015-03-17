package transport.protocol;

import transport.buffer.ChannelBuffer;


public abstract class Packet {

	private String id;
	
	private int length;
	
	private int sno;
	
	private String tno;

	private ChannelBuffer body;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getSno() {
		return sno;
	}

	public void setSno(int sno) {
		this.sno = sno;
	}

	public String getTno() {
		return tno;
	}

	public void setTno(String tno) {
		this.tno = tno;
	}

	public ChannelBuffer getBody() {
		return body;
	}

	public void setBody(ChannelBuffer body) {
		this.body = body;
	}
	
}
