package transport.protocol;


public interface Handler {
	
	Response handle(Request request);

}
