package com.kc.walle.station.engine.daemon.common.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.kc.walle.common.domain.core.Bucket;
import com.kc.walle.common.domain.core.BucketSlot;
import com.kc.walle.common.domain.core.Station;
import com.kc.walle.common.domain.core.Waypoint;
import com.kc.walle.common.infrastructure.waypoint.WayPointType;
import com.kc.walle.station.engine.daemon.common.ErrorCode;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.ServiceBeanFactory;

public class BaseService {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected ServiceBeanFactory serviceBeanFactory;
	
	@Autowired
	protected BeanFactory beanFactory;
	
	protected Interner<String> internerPool = Interners.newWeakInterner();
	
	protected Result<Station> checkOfflineStation(String stationID){
		Result<Station> result = new Result<Station>();
		if(StringUtils.isEmpty(stationID)){
			result.setError(ErrorCode.SCAN_STATION_PARAM_EMPTY.getCode(), ErrorCode.SCAN_STATION_PARAM_EMPTY.getMessage());
			return result;
		}
		Station station = this.serviceBeanFactory.getStationDas().selectOne(stationID);
		if(station == null){
			result.setError(ErrorCode.SCAN_STATION_NOT_EXIST.getCode(), String.format(ErrorCode.SCAN_STATION_NOT_EXIST.getMessage(), stationID));
			return result;
		}
		if(station.getEnabled() != null && !station.getEnabled()){
			result.setError(ErrorCode.SCAN_STATION_UN_ENABLED.getCode(), String.format(ErrorCode.SCAN_STATION_UN_ENABLED.getMessage(), stationID));
			return result;
		}
		result.setData(station);
		return result;
	}
	
	protected Result<Bucket> checkOfflineBucketSlot(String bucketSlotID){
		Result<Bucket> result = new Result<Bucket>();
		if(StringUtils.isBlank(bucketSlotID)){
			result.setError(ErrorCode.BUCKET_SLOT_PARAM_EMPTY.getCode(), ErrorCode.BUCKET_SLOT_PARAM_EMPTY.getMessage());
			return result;
		}
		BucketSlot bucketSlot = serviceBeanFactory.getBucketSlotDas().selectOne(bucketSlotID);
		if(bucketSlot == null){
			result.setError(ErrorCode.BUCKET_SLOT_NOT_EXIST.getCode(), String.format(ErrorCode.BUCKET_SLOT_NOT_EXIST.getMessage(), bucketSlotID));
			return result;
		}
		Bucket bucket = this.serviceBeanFactory.getBucketDas().selectOneDO(bucketSlot.getBucketID());
		if(bucket == null){
			result.setError(ErrorCode.BUCKET_NOT_BUCKET_EXIST.getCode(), String.format(ErrorCode.BUCKET_NOT_BUCKET_EXIST.getMessage(), bucketSlotID));
			return result;
		}
		Waypoint waypoint = this.serviceBeanFactory.getWaypointDas().selectOne(bucket.getWaypointID()+"");
		if(waypoint == null){
			result.setError(ErrorCode.BUCKET_SLOT_WAYPOINT_NOT_EXIST.getCode(), String.format(ErrorCode.BUCKET_SLOT_WAYPOINT_NOT_EXIST.getMessage(), bucketSlotID));
			return result;
		}
		if(!StringUtils.equalsIgnoreCase(waypoint.getWayPointType(), WayPointType.BUCKET_DETACHABLE_WORKING.name())){
			result.setError(ErrorCode.BUCKET_SLOT_NOT_OFFLINE.getCode(), String.format(ErrorCode.BUCKET_SLOT_NOT_OFFLINE.getMessage(), bucketSlotID));
			return result;
		}
		result.setData(bucket);
		return result;
	}
}
