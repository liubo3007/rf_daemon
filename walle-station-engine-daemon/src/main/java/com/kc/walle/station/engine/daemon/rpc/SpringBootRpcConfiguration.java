package com.kc.walle.station.engine.daemon.rpc;

import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.kc.walle.station.engine.daemon.StationEngineDaemonProperties;
import com.kc.walle.station.engine.daemon.StationEngineDaemonSpringContext;

/**
 * Das∑˛ŒÒ∂À≈‰÷√¿‡
 * @author kim.cheng
 *
 */
@Configuration
@Lazy
public class SpringBootRpcConfiguration {

	private int port;

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		StationEngineDaemonProperties daemonProperties = StationEngineDaemonSpringContext.getContext().getBean(StationEngineDaemonProperties.class);
		this.port = 50101;
		return new TomcatEmbeddedServletContainerFactory(this.port);
	}
//	
//	@Bean
//	public DataSource getDataSource(){
//		return SpringBeanFactory.getBean(DataSource.class);
//	}
}