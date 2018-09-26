package com.kc.walle.station.engine.daemon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.kc.walle.common.infrastructure.ann.NoProguard;
import com.kc.walle.common.infrastructure.config.LoggerUtils;

@NoProguard
public class StationEngineDaemonMain {
	private static final Logger logger = LoggerFactory.getLogger(StationEngineDaemonMain.class);
	
	static {
		LoggerUtils.init();
	}
	
	@NoProguard
	public static void main(String[] args) throws Exception {
		logger.info("StationEngineDaemonMain Start");
		
		ApplicationContext serverCtx = StationEngineDaemonSpringContext.getContext();
		
		// Start the RCP Server
		IStationEngineDaemonRpcServer rpcServer = serverCtx.getBean(IStationEngineDaemonRpcServer.class);
		rpcServer.start();
		
		// Keep the application running
		while(true) {
			Thread.sleep(Integer.MAX_VALUE) ;
		}
	}
}
