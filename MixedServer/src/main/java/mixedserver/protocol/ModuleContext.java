package mixedserver.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ModuleContext implements ApplicationContextAware {

	public static Logger logger = LoggerFactory.getLogger(ModuleContext.class);

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		logger.debug("setApplicationContext");

		applicationContext = context;
	}

	/**
	 * 获取 Bean
	 * 
	 * @param cls
	 * @return
	 */
	public static Object getBean(Class<?> cls) {
		if (applicationContext != null) {
			try {
				return applicationContext.getBean(cls);
			} catch (BeansException e) {
				return null;
			}
		} else {
			return null;
		}
	}
}
