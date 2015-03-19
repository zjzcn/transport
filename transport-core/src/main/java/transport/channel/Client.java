package transport.channel;

public interface Client extends Endpoint, Channel{

	void connect() throws Exception;
	
}
