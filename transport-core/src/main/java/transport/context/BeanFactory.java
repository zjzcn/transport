package transport.context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import transport.channel.Server;
import transport.util.Assert;

public class BeanFactory<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);
	
	private static final String BEANS_DIRECTORY = "META-INF/beans/";
	
	private static final ConcurrentMap<Class<?>, BeanFactory<?>> BEAN_FACTORYS = new ConcurrentHashMap<Class<?>, BeanFactory<?>>();
	
	//////////////////////////////////
	private String defaultName;
	
	private ConcurrentMap<String, Class<?>> cachedImplClasss = new ConcurrentHashMap<String, Class<?>>();
	
	private ConcurrentMap<String, Object> cachedInstances = new ConcurrentHashMap<String, Object>();
	
	private BeanFactory(Class<T> interfaceClass){
		loadBeans(BEANS_DIRECTORY, interfaceClass);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> BeanFactory<T> getInstence(Class<T> interfaceClass){
		Assert.notNull(interfaceClass, "Bean interfaceClass == null");
        Assert.isTrue(interfaceClass.isInterface(), "Bean interfaceClass(" + interfaceClass + ") is not interface!");
        
        BeanFactory<T> factory = (BeanFactory<T>)BEAN_FACTORYS.get(interfaceClass);
        if (factory == null) {
        	BEAN_FACTORYS.putIfAbsent(interfaceClass, new BeanFactory<T>(interfaceClass));
        	factory = (BeanFactory<T>)BEAN_FACTORYS.get(interfaceClass);
        }
        return factory;
	}
	
	@SuppressWarnings("unchecked")
	public T getBean(String name){
		Assert.hasText(name, "Bean name must have vlaue");
		String[] strs = name.split("\\.");
		T t = (T)cachedInstances.get(name);
		try {
			if(t == null){
				cachedInstances.putIfAbsent(name, cachedImplClasss.get(strs[0]).newInstance());
				t = (T)cachedInstances.get(name);
			}
			return t;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public T getDefaultBean(){
		return getBean(defaultName);
	}
	
	private void loadBeans(String dir, Class<?> interfaceClass) {
		String fileName = dir + interfaceClass.getName();
		try {
			Enumeration<java.net.URL> urls;
			ClassLoader classLoader = BeanFactory.class.getClassLoader();
			if (classLoader != null) {
				urls = classLoader.getResources(fileName);
			} else {
				urls = ClassLoader.getSystemResources(fileName);
			}
			if (urls != null) {
				while (urls.hasMoreElements()) {
					java.net.URL url = urls.nextElement();
					String interfaceName = url.getPath();
					BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
					try {
						String line = null;
						while ((line = reader.readLine()) != null) {
							final int ci = line.indexOf('#');
							if (ci >= 0) line = line.substring(0, ci);
							line = line.trim();
							if (line.length() > 0) {
								try {
									String name=null, className=null;
									int i = line.indexOf('=');
									if (i > 0) {
										name = line.substring(0, i).trim();
										className = line.substring(i + 1).trim();
									}
									if (className.length() > 0) {
										if(defaultName == null){
											defaultName = name;
										}
										cachedImplClasss.put(name, Class.forName(className));
									}
								} catch (Throwable t) {
									throw new IllegalStateException("Failed to load extension class(interface: " + interfaceName + ", class line: " + line + ") in " + url + ", cause: " + t.getMessage(), t);
								}
							}
						} // end of while read lines
					} catch (Throwable t) {
						logger.error("Exception when load extension class(interface: " + interfaceName + ", class file: " + url + ") in " + url, t);
					} finally {
						reader.close();
					}
				} // end of while urls
			}
		} catch (Throwable t) {
			logger.error("Exception when load extension class(interface: " + interfaceClass.getName() + ", description file: " + fileName + ").", t);
		}
	}
	
	public static void main(String[] args) {
		Server s = BeanFactory.getInstence(Server.class).getBean("mina.udp.9090");
		System.out.println(s);
	}
}
