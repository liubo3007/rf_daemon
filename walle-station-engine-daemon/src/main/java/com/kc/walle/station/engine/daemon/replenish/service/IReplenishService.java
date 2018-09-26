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
	 * 加载待上架的单据
	 * @param customerID
	 * @return
	 */
	Result<List<GoodsInBillVo>> selectWaitingShelveBills(Integer customerID);
	
	/**
	 * 验证离线站
	 * @param bucketSlotID
	 * @return
	 */
	Result<Integer> scanStation(String stationID);
	
	/**
	 * 接收扫描的单据或者箱子
	 * @param scanBillOrBoxRequestDto
	 * @return
	 */
	Result<String> scanBillOrBox(ScanBillOrBoxRequestDto scanBillOrBoxRequestDto);
	
	/**
	 * 接收扫描的商品
	 * @param scanSkuRequestDto
	 * @return
	 */
	Result<ScanedSkuRenderVo> scanSku(ScanSkuRequestDto scanSkuRequestDto);
	
	/**
	 * 验证货位号
	 * @param bucketSlotID
	 * @return
	 */
	Result<Void> scanBucketSlot(ShelveScanBucketSlotRequestDto scanBucketSlotRequest);
	
	/**
	 * 上架操作
	 * @param shelveRequest
	 * @return
	 */
	Result<Void> doShelve(ShelveRequestDto shelveRequest);
}
