package transport.channel.support.mina;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import transport.Param;
import transport.buffer.ChannelBuffer;
import transport.buffer.ChannelBuffers;
import transport.channel.Channel;
import transport.channel.ChannelException;
import transport.channel.ChannelFilter;
import transport.channel.ChannelHandlerAdapter;
import transport.channel.DecoderFilter;
import transport.channel.EncoderFilter;
import transport.channel.Server;
import transport.channel.support.AbstractServer;

public class MinaServer extends AbstractServer {
    
    private static final Logger logger = LoggerFactory.getLogger(MinaServer.class);

    private IoAcceptor acceptor;
    
	@Override
	public void bind(Param param) throws IOException{
		if("tcp".equalsIgnoreCase(param.tcpOrUdp)){
			acceptor = new NioSocketAcceptor();
		} else if("udp".equalsIgnoreCase(param.tcpOrUdp)){
			acceptor =  new NioDatagramAcceptor();
		} else {
			throw new IllegalArgumentException("param.tcpOrUdp value must be 'tcp' or 'udp'!");
		}

		acceptor.getFilterChain().addLast("logging",  new LoggingFilter());   
        Iterator<ChannelFilter> it = super.getFilterChain().iterator();
        while(it.hasNext()){
        	ChannelFilter filter = it.next();
        	acceptor.getFilterChain().addLast(filter.getName(), new MinaFilter(filter));
        }

        acceptor.setHandler(new MinaHandler(getHandler()));
        acceptor.bind(new InetSocketAddress(param.port));
        logger.info("Server has started! Listening on:"+acceptor.getLocalAddress());
	}

	public static void main(String[] args) throws Exception {
		Server server = new MinaServer();
		server.getFilterChain().addLast("e", new DecoderFilter() {
			@Override
			protected Object decode(Channel channel, Object msg) {
				if(msg instanceof ChannelBuffer){
					ChannelBuffer buffer = (ChannelBuffer)msg;
					System.out.println("filter received");
					byte[] bytes = new byte[buffer.readableBytes()];
					buffer.readBytes(bytes);
					return new String(bytes);
				} 

				return msg;
			}
		});
		
		server.getFilterChain().addLast("f", new DecoderFilter() {
			@Override
			protected Object decode(Channel channel, Object msg) {
				if(msg instanceof ChannelBuffer){
					ChannelBuffer buffer = (ChannelBuffer)msg;
					System.out.println("filter received");
					byte[] bytes = new byte[buffer.readableBytes()];
					buffer.readBytes(bytes);
					return new String(bytes);
				}
				return msg+"kkkkkkk";
			}
		});
		

		server.getFilterChain().addLast("t", new EncoderFilter() {
			@Override
			protected Object encode(Channel channel, Object msg) {
				if(msg instanceof ChannelBuffer){
					ChannelBuffer buffer = (ChannelBuffer)msg;
					System.out.println("filter received");
					byte[] bytes = new byte[buffer.readableBytes()];
					buffer.readBytes(bytes);
					return new String(bytes);
				}
				return ChannelBuffers.wrappedBuffer((msg+" is a ack is a ack").getBytes());
			}
		});
		
		server.getFilterChain().addLast("g", new EncoderFilter() {
			@Override
			protected Object encode(Channel channel, Object msg) {
				if(msg instanceof ChannelBuffer){
					ChannelBuffer buffer = (ChannelBuffer)msg;
					System.out.println("filter received");
					byte[] bytes = new byte[buffer.readableBytes()];
					buffer.readBytes(bytes);
					return new String(bytes);
				}
				return msg+" is a ack";
			}
		});
		
		server.setHandler(new ChannelHandlerAdapter() {
			@Override
			public void messageReceived(Channel channel, Object message)
					throws ChannelException {
				System.out.println("handler received");
				System.out.println(message);
				channel.send("hehe", false);
			}
		});
		
//		server.bind(new InetSocketAddress(9090));
	}
}
