package com.kc.walle.station.engine.daemon.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.kc.walle.station.engine.daemon.IStationEngineDaemonRpcServer;

@Configuration
@ComponentScan(basePackages = {"com.kc.walle.station.engine.daemon"})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class,DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
public class SpringBootRpcServer implements IStationEngineDaemonRpcServer {
	private static final Logger logger = LoggerFactory.getLogger(SpringBootRpcServer.class);

	private ApplicationContext sbAppContxt;

	@Override
	public void start() {
		logger.debug("start()");

		try {
			sbAppContxt = SpringApplication.run(SpringBootRpcServer.class);
		} catch (Exception e) {
			logger.error("Exception happens in start()", e);
			throw e;
		}

		logger.debug("start()done");
	}

	@Override
	public void stop() {
		logger.debug("stop()");

		try {
			if (sbAppContxt != null) {
				SpringApplication.exit(sbAppContxt);
				sbAppContxt = null;
			}
		} catch (Exception e) {
			logger.error("Exception happens in stop()", e);
			throw e;
		}

		logger.debug("stop() done");
	}
}
