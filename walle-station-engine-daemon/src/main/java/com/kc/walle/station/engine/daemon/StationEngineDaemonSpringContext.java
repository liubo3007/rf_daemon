package com.kc.walle.station.engine.daemon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.kc.walle.common.infrastructure.common.SpringBeanFactory;

public class StationEngineDaemonSpringContext {
private static final Logger logger = LoggerFactory.getLogger(StationEngineDaemonSpringContext.class);
	
	private static ApplicationContext applicationContext;
	
	public static ApplicationContext getContext() {
		if (applicationContext == null) {
			applicationContext = SpringBeanFactory.getApplicationContext();
		}
		if (applicationContext == null) {
			synchronized(StationEngineDaemonSpringContext.class) {
				if (applicationContext == null) {
					logger.debug("init spring context");
					applicationContext = new ClassPathXmlApplicationContext("spring/WalleStationEngineDaemonSpringConfig.xml");
					logger.debug("init spring context done");
				}
			}
		}
		
		return applicationContext;
	}
}
