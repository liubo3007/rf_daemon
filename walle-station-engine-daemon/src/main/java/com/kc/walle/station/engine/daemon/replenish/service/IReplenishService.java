package com.kc.walle.station.engine.daemon.replenish.service;

import java.util.List;

import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.replenish.dto.ScanBillOrBoxRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ScanSkuRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveScanBucketSlotRequestDto;
import com.kc.walle.station.engine.daemon.replenish.vo.GoodsInBillVo;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuRenderVo;

public interface IReplenishService {
	
	/**
	 * ���ش��ϼܵĵ���
	 * @param customerID
	 * @return
	 */
	Result<List<GoodsInBillVo>> selectWaitingShelveBills(Integer customerID);
	
	/**
	 * ��֤����վ
	 * @param bucketSlotID
	 * @return
	 */
	Result<Integer> scanStation(String stationID);
	
	/**
	 * ����ɨ��ĵ��ݻ�������
	 * @param scanBillOrBoxRequestDto
	 * @return
	 */
	Result<String> scanBillOrBox(ScanBillOrBoxRequestDto scanBillOrBoxRequestDto);
	
	/**
	 * ����ɨ�����Ʒ
	 * @param scanSkuRequestDto
	 * @return
	 */
	Result<ScanedSkuRenderVo> scanSku(ScanSkuRequestDto scanSkuRequestDto);
	
	/**
	 * ��֤��λ��
	 * @param bucketSlotID
	 * @return
	 */
	Result<Void> scanBucketSlot(ShelveScanBucketSlotRequestDto scanBucketSlotRequest);
	
	/**
	 * �ϼܲ���
	 * @param shelveRequest
	 * @return
	 */
	Result<Void> doShelve(ShelveRequestDto shelveRequest);
}
