package transport.channel.support;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;

import transport.channel.Channel;
import transport.channel.Server;

public abstract class AbstractServer extends AbstractEndpoint implements Server {

	@Override
	public InetSocketAddress getLocalAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send(Object message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(Object message, boolean sent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(int timeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Channel> getChannels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Channel getChannel(InetSocketAddress remoteAddress) {
		// TODO Auto-generated method stub
		return null;
	}

}
