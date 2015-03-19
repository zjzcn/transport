package transport.channel;

import java.net.InetSocketAddress;

public class TransportException extends RuntimeException {

    public TransportException(String message){
        super(message);
    }

    public TransportException(Throwable cause){
        super(cause);
    }

    public TransportException(String message, Throwable cause){
        super(message, cause);
    }
}