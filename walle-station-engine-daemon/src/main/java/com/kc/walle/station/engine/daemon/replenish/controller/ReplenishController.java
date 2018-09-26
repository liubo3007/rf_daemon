package com.kc.walle.station.engine.daemon.replenish.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.replenish.dto.ScanBillOrBoxRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ScanSkuRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveScanBucketSlotRequestDto;
import com.kc.walle.station.engine.daemon.replenish.service.IReplenishService;
import com.kc.walle.station.engine.daemon.replenish.vo.GoodsInBillVo;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuRenderVo;

@RestController
@RequestMapping("/replenish")
public class ReplenishController {

private final Logger logger = LoggerFactory.getLogger(ReplenishController.class);
	
	@Autowired
	private IReplenishService replenishService;
	
	@RequestMapping(value = "/selectbills", method = { RequestMethod.GET })
	@ResponseBody
	public Result<List<GoodsInBillVo>> selectBills(@RequestParam Integer customerID) {
		logger.debug(String.format("selectbills customerID:%s", customerID));
		Result<List<GoodsInBillVo>> result = replenishService.selectWaitingShelveBills(customerID);
		logger.debug("selectbills() done");
		return result;
	}
	
	@RequestMapping(value = "/scanstation", method = { RequestMethod.GET })
	@ResponseBody
	public Result<Integer> scanstation(@RequestParam String stationID) {
		logger.debug("scanstation");
		Result<Integer> result = replenishService.scanStation(stationID);
		logger.debug("scanstation() done");
		return result;
	}
	
	@RequestMapping(value = "/scanbillorbox", method = { RequestMethod.POST })
	@ResponseBody
	public Result<String> scanBillOrBox(@RequestBody ScanBillOrBoxRequestDto scanBillOrBoxRequestDto) {
		logger.debug("scanBillOrBox");
		Result<String> result = replenishService.scanBillOrBox(scanBillOrBoxRequestDto);
		logger.debug("scanBillOrBox() done");
		return result;
	}
	
	@RequestMapping(value = "/scansku", method = { RequestMethod.POST })
	@ResponseBody
	public Result<ScanedSkuRenderVo> scansku(@RequestBody ScanSkuRequestDto scanSkuRequestDto) {
		logger.debug("scanBillOrBox");
		Result<ScanedSkuRenderVo> result = replenishService.scanSku(scanSkuRequestDto);
		logger.debug("scanBillOrBox() done");
		return result;
	}
	
	@RequestMapping(value = "/scanbucketslot", method = { RequestMethod.POST })
	@ResponseBody
	public Result<Void> scanBucketSlot(@RequestBody ShelveScanBucketSlotRequestDto scanBucketSlotRequest) {
		logger.debug("scanBucketSlot");
		Result<Void> result = replenishService.scanBucketSlot(scanBucketSlotRequest);
		logger.debug("scanBucketSlot() done");
		return result;
	}
	
	@RequestMapping(value = "/shelve", method = { RequestMethod.POST })
	@ResponseBody
	public Result<Void> doShelve(@RequestBody ShelveRequestDto shelveRequest) {
		logger.debug("doShelve");
		Result<Void> result = replenishService.doShelve(shelveRequest);
		logger.debug("doShelve() done");
		return result;
	}
}
