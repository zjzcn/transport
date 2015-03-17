package transport.channel.support.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
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
import transport.util.NamedThreadFactory;

public class NettyServer extends AbstractServer {
    
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Map<String, Channel>  channels; // <ip:port, channel>

    private ServerBootstrap                 bootstrap;

    private org.jboss.netty.channel.Channel channel;
    
	@Override
	public void bind(Param param) throws IOException{


        ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerBoss", false));
        ExecutorService worker = Executors.newCachedThreadPool(new NamedThreadFactory("NettyServerWorker", false));
        ChannelFactory channelFactory = new NioServerSocketChannelFactory(boss, worker);
        bootstrap = new ServerBootstrap(channelFactory);
        
        final NettyHandler nettyHandler = new NettyHandler(this.getHandler());
//        channels = nettyHandler.getChannels();
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
            	ChannelPipeline pipeline = Channels.pipeline();
            	InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
            	pipeline.addLast("logger", new LoggingHandler());
                Iterator<ChannelFilter> it = NettyServer.this.getFilterChain().iterator();
                while(it.hasNext()){
                	ChannelFilter filter = it.next();
                	pipeline.addLast(filter.getName(), new NettyFilter(filter));
                }
                pipeline.addLast("handler", nettyHandler);
                return pipeline;
            }
        });
        // bind
        channel = bootstrap.bind(new InetSocketAddress(9090));
        logger.info("Server has started! Listening on:"+channel.getLocalAddress());
	}

	public static void main(String[] args) throws Exception {
		Server server = new NettyServer();
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
		
		server.bind(null);
	}
}
