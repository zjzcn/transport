package transport.protocol.support.linkage;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import transport.Param;
import transport.channel.ChannelHandler;
import transport.channel.FilterChain;
import transport.channel.Server;
import transport.context.BeanFactory;
import transport.protocol.Decoder;
import transport.protocol.Encoder;
import transport.protocol.Handler;
import transport.protocol.annotation.DecoderMapping;
import transport.protocol.annotation.EncoderMapping;
import transport.protocol.annotation.HandlerMapping;
import transport.protocol.support.SpringContext;

public class LinkageProtocol {
	
	private Map<String, Encoder> encoders = new ConcurrentHashMap<String, Encoder>();
	private Map<String, Decoder> decoders = new ConcurrentHashMap<String, Decoder>();
	private Map<String, Handler> handlers = new ConcurrentHashMap<String, Handler>();
	
	public FilterChain createFilterChain() {
		FilterChain chain = new FilterChain();
		chain.addLast("codec", new LinkageCodecFilter(encoders, decoders));
		chain.addLast("test", new TestCodecFilter(encoders, decoders));
		return chain;
	}

	public ChannelHandler createChannelHandler() {
		return new LinkageChannelHandler(handlers);
	}

	public void initMapping(){
		Map<String, Encoder> encoderMap = SpringContext.getBeansOfType(Encoder.class);
		Collection<Encoder> e = encoderMap.values();
		for(Encoder encoder : e){
			System.out.println(encoder);
			Class<?> clazz = encoder.getClass();
			if(clazz.isAnnotationPresent(EncoderMapping.class)){
				EncoderMapping ann = clazz.getAnnotation(EncoderMapping.class);
				encoders.put(ann.value(), encoder);
			} else {
				throw new IllegalStateException("Encoder has not @EncoderMapping annotation");
			}

		}

		Map<String, Decoder> decoderMap = SpringContext.getBeansOfType(Decoder.class);
		Collection<Decoder> d = decoderMap.values();
		for(Decoder decoder : d){
			Class<?> clazz = decoder.getClass();
			System.out.println(decoder);
			if(clazz.isAnnotationPresent(DecoderMapping.class)){
				DecoderMapping ann = clazz.getAnnotation(DecoderMapping.class);
				decoders.put(ann.value(), decoder);
			} else {
				throw new IllegalStateException("Decoder has not @DecoderMapping annotation");
			}
		}

		Map<String, Handler> handlerMap = SpringContext.getBeansOfType(Handler.class);
		Collection<Handler> h = handlerMap.values();
		for(Handler handler : h){
			Class<?> clazz = handler.getClass();
			System.out.println(handler);
			if(clazz.isAnnotationPresent(HandlerMapping.class)){
				HandlerMapping ann = clazz.getAnnotation(HandlerMapping.class);
				handlers.put(ann.value(), handler);
			} else {
				throw new IllegalStateException("Decoder has not @DecoderMapping annotation");
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/*.xml");
		
		LinkageProtocol s = new LinkageProtocol();
		s.initMapping();
		
		Server server = BeanFactory.getInstence(Server.class).getBean("mina");
		server.setFilterChain(s.createFilterChain());
		server.setHandler(s.createChannelHandler());
		Param param = new Param();
		param.port = 9090;
		server.bind(param);
		
		System.in.read();
	}
}
