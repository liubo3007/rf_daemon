package com.kc.walle.station.engine.daemon;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.kc.walle.station.engine.StationEngineProperties;

@Component
@Lazy
public class StationEngineDaemonProperties {
	private String rpcServerIP ;
	private int rpcServerPort = 50101;
	private int socketIOServerPort ;
	private boolean checkSkuAndBucketAttribute;
	
	@PostConstruct
	public void init() throws Exception {
		StationEngineProperties stationEngineProperties = StationEngineDaemonSpringContext.getContext().getBean(StationEngineProperties.class);
		this.rpcServerIP = stationEngineProperties.getStationIP() ;
		//this.rpcServerPort = stationEngineProperties.getStationRpcPort() ;
		//this.socketIOServerPort = stationEngineProperties.getStationSocketIOPort() ;
		this.checkSkuAndBucketAttribute = stationEngineProperties.isCheckSkuAndBucketAttribute();
	}
	
	public String getRpcServerIP(){
		return rpcServerIP ;// TODO: Read from database later
	}
	
	public int getRpcServerPort() {
		return rpcServerPort; // TODO: Read from database later
	}
	
	public int getSocketIOServerPort() {
		return socketIOServerPort; // TODO: Read from database later
	}

	public boolean isCheckSkuAndBucketAttribute() {
		return checkSkuAndBucketAttribute;
	}
	
}
