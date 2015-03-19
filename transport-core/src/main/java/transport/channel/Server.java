package transport.channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;

public interface Server extends Endpoint{

	void bind(SocketAddress localAddress);

    Collection<Channel> getChannels();

    Channel getChannel(InetSocketAddress remoteAddress);
}
