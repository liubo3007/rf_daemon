package com.kc.walle.station.engine.daemon.replenish.service;

import java.util.List;

import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.replenish.dto.DispatchBucketRequestDto;
import com.kc.walle.station.engine.daemon.replenish.vo.BucketSlotTypeVo;
import com.kc.walle.station.engine.daemon.replenish.vo.BucketTypeVo;

public interface IDispatchBucketService {
	
	/**
	 * ��ȡ���������б�
	 * @return
	 */
	Result<List<BucketTypeVo>> getAllBucketTypes();
	
	/**
	 * ��ȡ��λ�����б�
	 * @return
	 */
	Result<List<BucketSlotTypeVo>> getAllBucketSlotTypes();
	
	/**
	 * ��ʼ����ʱУ��ɨ������߲���վ
	 * @param stationID
	 * @return
	 */
	Result<Void> checkStartDispatchScanStation(String stationID);
	
	/**
	 * ��������ʱУ��ɨ������߲���վ
	 * @param stationID
	 * @return
	 */
	Result<Void> checkFinishDispatchScanStation(String stationID);
	
	/**
	 * ��ʼ���Ȼ���
	 * @param dispatchBucketRequest
	 * @return
	 */
	Result<Void> startDispatchBucket(DispatchBucketRequestDto dispatchBucketRequest);
	
	/**
	 * �������Ȼ���
	 * @param stationID
	 * @param operator
	 * @return
	 */
	Result<Void> finishDispatchBucket(String stationID,String operator);
	
	/**
	 * ���ݻ�λ�ͷŶ�Ӧ����
	 * @param bucketSlotID
	 * @return
	 */
	Result<Void> releaseBucketByBucketSlotID(String bucketSlotID);
	
}
