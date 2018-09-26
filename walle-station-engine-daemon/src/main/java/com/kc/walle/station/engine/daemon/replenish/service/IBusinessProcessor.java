package com.kc.walle.station.engine.daemon.replenish.service;

import com.kc.walle.common.domain.erp.Sku;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveRequestDto;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuRenderVo;

public interface IBusinessProcessor {
	/**
	 * 创建传给前端的扫描的商品具体渲染信息
	 * @return
	 */
	public Result<ScanedSkuRenderVo> createScanedSkuRenderInfo(String billNumber,Integer customerID,Sku sku);
	
	/**
	 * 校验扫描的单据或者箱号
	 * @param billOrBox
	 * @param customerID
	 * @return
	 */
	public String checkScanBillOrBox(String billOrBox, Integer customerID);
	
	/**
	 * 上架操作
	 * @param shelveRequest
	 * @return
	 */
	public String doShelve(ShelveRequestDto shelveRequest);
}
