package com.kc.walle.station.engine.daemon.replenish.service;

import java.util.List;

import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.replenish.dto.DispatchBucketRequestDto;
import com.kc.walle.station.engine.daemon.replenish.vo.BucketSlotTypeVo;
import com.kc.walle.station.engine.daemon.replenish.vo.BucketTypeVo;

public interface IDispatchBucketService {
	
	/**
	 * 获取货架类型列表
	 * @return
	 */
	Result<List<BucketTypeVo>> getAllBucketTypes();
	
	/**
	 * 获取货位类型列表
	 * @return
	 */
	Result<List<BucketSlotTypeVo>> getAllBucketSlotTypes();
	
	/**
	 * 开始调度时校验扫描的离线补货站
	 * @param stationID
	 * @return
	 */
	Result<Void> checkStartDispatchScanStation(String stationID);
	
	/**
	 * 结束调度时校验扫描的离线补货站
	 * @param stationID
	 * @return
	 */
	Result<Void> checkFinishDispatchScanStation(String stationID);
	
	/**
	 * 开始调度货架
	 * @param dispatchBucketRequest
	 * @return
	 */
	Result<Void> startDispatchBucket(DispatchBucketRequestDto dispatchBucketRequest);
	
	/**
	 * 结束调度货架
	 * @param stationID
	 * @param operator
	 * @return
	 */
	Result<Void> finishDispatchBucket(String stationID,String operator);
	
	/**
	 * 根据货位释放对应货架
	 * @param bucketSlotID
	 * @return
	 */
	Result<Void> releaseBucketByBucketSlotID(String bucketSlotID);
	
}
