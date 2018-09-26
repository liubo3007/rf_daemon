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
import com.kc.walle.station.engine.daemon.replenish.dto.DispatchBucketRequestDto;
import com.kc.walle.station.engine.daemon.replenish.service.IDispatchBucketService;
import com.kc.walle.station.engine.daemon.replenish.vo.BucketSlotTypeVo;
import com.kc.walle.station.engine.daemon.replenish.vo.BucketTypeVo;

@RestController
@RequestMapping("/dispatch")
public class DispatchBucketController {

private final Logger logger = LoggerFactory.getLogger(DispatchBucketController.class);
	
	@Autowired
	private IDispatchBucketService dispatchBucketService;
	
	@RequestMapping(value = "/getbuckettype", method = { RequestMethod.GET })
	@ResponseBody
	public Result<List<BucketTypeVo>> listBucketType() {
		logger.debug("listBucketType()");
		Result<List<BucketTypeVo>> result = dispatchBucketService.getAllBucketTypes();
		logger.debug("listBucketType() done");
		return result;
	}
	
	@RequestMapping(value = "/getbucketslottype", method = { RequestMethod.GET })
	@ResponseBody
	public Result<List<BucketSlotTypeVo>> listBucketSlotType() {
		logger.debug("listBucketSlotType()");
		Result<List<BucketSlotTypeVo>> result = dispatchBucketService.getAllBucketSlotTypes();
		logger.debug("listBucketSlotType() done");
		return result;
	}
	
	
	@RequestMapping(value = "/start/sanstation", method = { RequestMethod.GET })
	@ResponseBody
	public Result<Void> startDispatchScanStation(@RequestParam String stationID) {
		logger.debug(String.format("startDispatchScanStation stationID:%s", stationID));
		Result<Void> result = dispatchBucketService.checkStartDispatchScanStation(stationID);
		logger.debug("startDispatchScanStation() done");
		return result;
	}
	
	@RequestMapping(value = "/finish/sanstation", method = { RequestMethod.GET })
	@ResponseBody
	public Result<Void> finishDispatchScanStation(@RequestParam String stationID) {
		logger.debug(String.format("finishDispatchScanStation stationID:%s", stationID));
		Result<Void> result = dispatchBucketService.checkFinishDispatchScanStation(stationID);
		logger.debug("finishDispatchScanStation() done");
		return result;
	}
	
	@RequestMapping(value = "/startdispatch", method = { RequestMethod.POST })
	@ResponseBody
	public Result<Void> startDispatch(@RequestBody DispatchBucketRequestDto dispatchBucketRequest) {
		logger.debug("startDispatch");
		Result<Void> result = dispatchBucketService.startDispatchBucket(dispatchBucketRequest);
		logger.debug("startDispatch() done");
		return result;
	}
	
	@RequestMapping(value = "/finishdispatch", method = { RequestMethod.GET })
	@ResponseBody
	public Result<Void> finishDispatch(@RequestParam String stationID,@RequestParam String username) {
		logger.debug(String.format("finishDispatch stationID:%s;operator:%s", stationID,username));
		Result<Void> result = dispatchBucketService.finishDispatchBucket(stationID,username);
		logger.debug("finishDispatch() done");
		return result;
	}
	
	@RequestMapping(value = "/releasebucket", method = { RequestMethod.GET })
	@ResponseBody
	public Result<Void> releaseBucket(@RequestParam String bucketSlotID) {
		logger.debug(String.format("releaseBucket bucketSlotID:%s", bucketSlotID));
		Result<Void> result = dispatchBucketService.releaseBucketByBucketSlotID(bucketSlotID);
		logger.debug("releaseBucket() done");
		return result;
	}
}
