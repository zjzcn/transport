package transport.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import transport.Param;
import transport.buffer.ChannelBuffer;
import transport.buffer.ChannelBuffers;
import transport.channel.Channel;
import transport.channel.ChannelFilter;
import transport.channel.ChannelHandler;
import transport.channel.ChannelHandlerAdapter;
import transport.channel.CodecFilter;
import transport.channel.FilterChain;
import transport.channel.Server;
import transport.context.BeanFactory;
import transport.protocol.annotation.DecoderMapping;
import transport.protocol.annotation.EncoderMapping;
import transport.protocol.annotation.HandlerMapping;
import transport.util.PackageUtils;


public class ProtocolManager {

	private static Map<String, Encoder> encoders = new ConcurrentHashMap<String, Encoder>();
	private static Map<String, Decoder> decoders = new ConcurrentHashMap<String, Decoder>();
	private static Map<String, Handler> handlers = new ConcurrentHashMap<String, Handler>();
	
	public static FilterChain initFilterChain(){
		FilterChain chain = new FilterChain();
		chain.addLast("codec", new ProtocolCodecFilter());
		return chain;
	}
	
	public static ChannelHandler initChannelHandler(){
		return new ProtocolHandler();
	}
	
	public static List<ChannelFilter> getChannelFilters(){
		List<ChannelFilter> filters = new ArrayList<ChannelFilter>();
		
		return filters;
	}
	
	public static ChannelHandler getChannelHandler(){
		return null;
	}
	
	public static void addEncoder(String id, Encoder encoder){
		encoders.put(id, encoder);
	}
	
	public static void addDecoder(String id, Decoder decoder){
		decoders.put(id, decoder);
	}
	
	public static void addHandler(String id, Handler handler){
		handlers.put(id, handler);
	}
	
	public static void scanMapping(String basePackage){
		try {
			List<String> classNames = PackageUtils.getClassNames(basePackage);
			for(String className : classNames){
				System.out.println(className);
				Class<?> clazz = Class.forName(className);
				if(clazz.isAnnotationPresent(EncoderMapping.class)){
					EncoderMapping ann = clazz.getAnnotation(EncoderMapping.class);
					encoders.put(ann.value(), (Encoder)clazz.newInstance());
				} else if(clazz.isAnnotationPresent(DecoderMapping.class)){
					DecoderMapping ann = clazz.getAnnotation(DecoderMapping.class);
					decoders.put(ann.value(), (Decoder)clazz.newInstance());	
				} else if(clazz.isAnnotationPresent(HandlerMapping.class)){
					HandlerMapping ann = clazz.getAnnotation(HandlerMapping.class);
					handlers.put(ann.value(), (Handler)clazz.newInstance());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class ProtocolCodecFilter extends CodecFilter{

		@Override
		protected Object decode(Channel channel, Object message) {
			if(message instanceof ChannelBuffer) {
				ChannelBuffer buffer = (ChannelBuffer)message;
				String id = String.valueOf(buffer.readByte());
				Decoder decoder = decoders.get(id);
				Request request = decoder.decode(buffer);
				request.setId(id);
				return request;
			}
			return null;
		}

		@Override
		protected Object encode(Channel channel, Object message) {
			MyResponse rsp = (MyResponse)message;
			String id = rsp.getId();
			Encoder encoder = encoders.get(id);
			ChannelBuffer buffer = encoder.encode(rsp);
			return buffer;
		}
		
	}
	
	private static class ProtocolHandler extends ChannelHandlerAdapter{
		
		@Override
		public void messageReceived(Channel channel, Object message){
			Request request = (Request)message;
			Handler handler = handlers.get(request.getId());
			Response response = handler.handle(request);
			
			channel.send(response);
		}
	}
	
	@DecoderMapping("1")
	public static class MyDecoder implements Decoder{
		@Override
		public Request decode(ChannelBuffer buffer) {
			int i = buffer.readByte();
			MyRequest req = new MyRequest();
			req.setContent(i);
			return req;
		}
		
	}
	
	@EncoderMapping("2")
	public static class MyEncoder implements Encoder{
		@Override
		public ChannelBuffer encode(Response rsp) {
			MyResponse response = (MyResponse)rsp;
			ChannelBuffer buffer = ChannelBuffers.directBuffer(1024);
			buffer.writeByte(Integer.valueOf(rsp.getId()));
			buffer.writeByte(response.getContent());
			return buffer;
		}
		
	}
	@HandlerMapping("1")
	public static class MyHandler implements Handler{
		
		@Override
		public Response handle(Request request) {
			System.out.println(((MyRequest)request).getContent());
			MyResponse response = new MyResponse();
			response.setId("2");
			response.setContent(2);
			return response;
		}
	}
	
	private static class MyRequest extends Request{
		private int content;

		public int getContent() {
			return content;
		}

		public void setContent(int content) {
			this.content = content;
		}
	}
	
	private static class MyResponse extends Response{
		private int content;

		public int getContent() {
			return content;
		}

		public void setContent(int content) {
			this.content = content;
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		scanMapping("transport");
//		decoders.put(1, new MyDecoder());
//		encoders.put(2, new MyEncoder());
//		handlers.put(1, new MyHandler());
//		Server server = new MinaServer();
		Server server = BeanFactory.getInstence(Server.class).getBean("netty");
		server.setFilterChain(initFilterChain());
		server.setHandler(initChannelHandler());
		Param param = new Param();
		param.port = 9090;
		server.bind(param);
		
		Server server1 = BeanFactory.getInstence(Server.class).getBean("mina");
		server1.setFilterChain(initFilterChain());
		server1.setHandler(initChannelHandler());
		param.tcpOrUdp = "udp";
		server1.bind(param);
	}
	
}
