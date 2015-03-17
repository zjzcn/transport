package transport.protocol.support;

import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Utils - Spring
 * 
 * @author zjzcn Team
 * @version 3.0
 */
@Component("springContext")
@Lazy(false)
public final class SpringContext implements ApplicationContextAware, DisposableBean {

	/** applicationContext */
	private static ApplicationContext applicationContext;

	/**
	 * 不可实例化
	 */
	private SpringContext() {
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContext.applicationContext = applicationContext;
	}

	public void destroy() throws Exception {
		applicationContext = null;
	}

	/**
	 * 获取applicationContext
	 * 
	 * @return applicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 获取实例
	 * 
	 * @param name
	 *            Bean名称
	 * @return 实例
	 */
	public static Object getBean(String name) {
		Assert.hasText(name);
		return applicationContext.getBean(name);
	}

	/**
	 * 获取实例
	 * 
	 * @param name
	 *            Bean名称
	 * @param type
	 *            Bean类型
	 * @return 实例
	 */
	public static <T> T getBean(String name, Class<T> type) {
		Assert.hasText(name);
		Assert.notNull(type);
		return applicationContext.getBean(name, type);
	}

	/**
	 * 获取实例
	 * 
	 * @param type
	 *            Bean类型
	 * @return 实例
	 */
	public static <T> Map<String, T> getBeansOfType(Class<T> type) {
		Assert.notNull(type);
		return applicationContext.getBeansOfType(type);
	}

	public static void publishEvent(ApplicationEvent event){ 
		Assert.notNull(event);
		applicationContext.publishEvent(event);
	}
}