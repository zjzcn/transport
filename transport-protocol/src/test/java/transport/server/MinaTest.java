package transport.server;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2014/7/23.
 */
public class MinaTest {
    public static void main(String[] args) throws Exception {
        SocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setReuseAddress( true );
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

        // Bind
        acceptor.setHandler(new EchoServerHandler());
        acceptor.bind(new InetSocketAddress(8089));

        System.out.println("Listening on port " + 8089);
    }

    private static class EchoServerHandler extends IoHandlerAdapter {
        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            // Write the received data back to remote peer
            session.write(((IoBuffer) message).duplicate());
        }
    }
}
