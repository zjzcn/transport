package transport.channel;

import java.net.InetSocketAddress;
import java.util.Collection;

import transport.Param;

public interface Server extends Endpoint{

	void bind(Param param) throws Exception;
	
    boolean isBound();

    Collection<Channel> getChannels();

    Channel getChannel(InetSocketAddress remoteAddress);
}
