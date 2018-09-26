package com.kc.walle.station.engine.daemon.replenish.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kc.walle.common.domain.core.Bucket;
import com.kc.walle.common.domain.core.Station;
import com.kc.walle.common.domain.core.StationWaypoint;
import com.kc.walle.common.domain.erp.DirectShelveBill;
import com.kc.walle.common.domain.erp.Sku;
import com.kc.walle.common.enumeration.BusinessType;
import com.kc.walle.common.enumeration.DirectshelveBillState;
import com.kc.walle.common.infrastructure.station.StationType;
import com.kc.walle.station.engine.daemon.StationEngineDaemonProperties;
import com.kc.walle.station.engine.daemon.common.ErrorCode;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.service.impl.BaseService;
import com.kc.walle.station.engine.daemon.replenish.dto.ScanBillOrBoxRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ScanSkuRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveScanBucketSlotRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveSkuDto;
import com.kc.walle.station.engine.daemon.replenish.service.IBusinessProcessor;
import com.kc.walle.station.engine.daemon.replenish.service.IReplenishService;
import com.kc.walle.station.engine.daemon.replenish.vo.GoodsInBillVo;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuRenderVo;

@Service
public class ReplenishServiceImpl extends BaseService implements IReplenishService {
	
	@Autowired
	private BillBusinessProcessorImpl billBusinessProcessor;
	@Autowired
	private BoxBusinessProcessorImpl boxBusinessProcessor;
	@Autowired
	private StationEngineDaemonProperties stationEngineDaemonProperties;

	@Override
	public Result<List<GoodsInBillVo>> selectWaitingShelveBills(Integer customerID) {
		logger.debug("selectWaitingShelveBills()");
		Result<List<GoodsInBillVo>> result = billBusinessProcessor.selectWaitingShelveBills(customerID);
		logger.debug("selectWaitingShelveBills() done");
		return result;
	}
	
	@Override
	public Result<Integer> scanStation(String stationID) {
		logger.debug("scanStation()");
		Result<Integer> result= new Result<Integer>();
		Result<Station> checkResult = this.checkOfflineStation(stationID);
		if(!checkResult.isSuccess()){
			result.setError(checkResult.getErrorCode(), checkResult.getErrorMsg());
			return result;
		}
		Station station = checkResult.getData();
		if(!StringUtils.equalsIgnoreCase(station.getType(), StationType.OFFLINEREPLENISH.name())){
			result.setError(ErrorCode.SCAN_STATION_NOT_OFFLINE_REPLENISH_STATION.getCode(), String.format(ErrorCode.SCAN_STATION_NOT_OFFLINE_REPLENISH_STATION.getMessage(), stationID));
			return result;
		}
		List<DirectShelveBill> shelveBills = this.serviceBeanFactory.getDirectShelveBillDas().selectByStateStationID(DirectshelveBillState.DOING.name(), stationID);
		if(shelveBills == null || shelveBills.isEmpty()){
			result.setError(ErrorCode.STATION_NO_DISPATCH_BUCKETS.getCode(), String.format(ErrorCode.STATION_NO_DISPATCH_BUCKETS.getMessage(), stationID));
			return result;
		}
		result.setData(shelveBills.get(0).getCustomerID());
		logger.debug("scanStation() done");
		return result;
	}
	

	@Override
	public Result<String> scanBillOrBox(ScanBillOrBoxRequestDto scanBillOrBoxRequestDto) {
		logger.debug("scanBillOrBox()");
		Result<String> result = new Result<String>();
		if(StringUtils.isBlank(scanBillOrBoxRequestDto.getBillOrBox())){
			result.setError(ErrorCode.SCAN_BILL_BOX_SCANCODE_EMPTY.getCode(), ErrorCode.SCAN_BILL_BOX_SCANCODE_EMPTY.getMessage());
			return result;
		}
		if(scanBillOrBoxRequestDto.getCustomerID() == null){
			result.setError(ErrorCode.SCAN_BILL_BOX_CUSTOMERID_EMPTY.getCode(), ErrorCode.SCAN_BILL_BOX_CUSTOMERID_EMPTY.getMessage());
			return result;
		}
		//约定：前端传过来的type分为：Bill，Box，空
		//目前只有选择时，才会有值为Bill，其它方式都是空
		if(StringUtils.equalsIgnoreCase(BusinessType.Bill.name(), scanBillOrBoxRequestDto.getType())){
			String checkResult = billBusinessProcessor.checkScanBillOrBox(scanBillOrBoxRequestDto.getBillOrBox(), scanBillOrBoxRequestDto.getCustomerID());
			if(StringUtils.isNotBlank(checkResult)){
				result.setError(ErrorCode.SCAN_BILL_BOX_ERROR.getCode(), String.format("%s,请重试",checkResult));
				return result;
			}
			result.setData(BusinessType.Bill.name());
			return result;
		}else{
			//对于扫描，匹配顺序：先单据再箱子
			String checkBillResult = billBusinessProcessor.checkScanBillOrBox(scanBillOrBoxRequestDto.getBillOrBox(), scanBillOrBoxRequestDto.getCustomerID());
			if(StringUtils.isBlank(checkBillResult)){
				result.setData(BusinessType.Bill.name());
				return result;
			}
			String checkBoxResult = boxBusinessProcessor.checkScanBillOrBox(scanBillOrBoxRequestDto.getBillOrBox(), scanBillOrBoxRequestDto.getCustomerID());
			if(StringUtils.isBlank(checkBoxResult)){
				result.setData(BusinessType.Box.name());
				return result;
			}
			result.setError(ErrorCode.SCAN_BILL_BOX_ERROR.getCode(), String.format("未匹配到任何可用上架单/LPN,详情:%s;%s",checkBillResult,checkBoxResult));
			return result;
		}
	}
	
	@Override
	public Result<ScanedSkuRenderVo> scanSku(ScanSkuRequestDto scanSkuRequestDto) {
		logger.debug("scanSku()");
		Result<ScanedSkuRenderVo> result = new Result<ScanedSkuRenderVo>();
		if(StringUtils.isBlank(scanSkuRequestDto.getSkuNumber())){
			result.setError(ErrorCode.SCAN_SKU_CODE_EMPTY.getCode(), ErrorCode.SCAN_SKU_CODE_EMPTY.getMessage());
			return result;
		}
		if(scanSkuRequestDto.getCustomerID() == null){
			result.setError(ErrorCode.SCAN_SKU_CUSTOMER_EMPTY.getCode(), ErrorCode.SCAN_SKU_CUSTOMER_EMPTY.getMessage());
			return result;
		}
		if(StringUtils.isBlank(scanSkuRequestDto.getBillOrBox())){
			result.setError(ErrorCode.SCAN_SKU_BILLBOX_EMPTY.getCode(), ErrorCode.SCAN_SKU_BILLBOX_EMPTY.getMessage());
			return result;
		}
		if(!StringUtils.equalsIgnoreCase(scanSkuRequestDto.getType(), BusinessType.Bill.name()) && !StringUtils.equalsIgnoreCase(scanSkuRequestDto.getType(), BusinessType.Box.name())){
			result.setError(ErrorCode.SCAN_SKU_BILLBOX_TYPE_ERROR.getCode(), ErrorCode.SCAN_SKU_BILLBOX_TYPE_ERROR.getMessage());
			return result;
		}
		List<Sku> skus = this.serviceBeanFactory.getSkuDas().selectByCustomerAndNumber(scanSkuRequestDto.getCustomerID(), scanSkuRequestDto.getSkuNumber());
		if(skus == null || skus.isEmpty()){
			result.setError(ErrorCode.SCAN_SKU_NOT_EXIST.getCode(), String.format(ErrorCode.SCAN_SKU_NOT_EXIST.getMessage(), scanSkuRequestDto.getSkuNumber()));
			return result;
		}
		Sku sku = skus.get(0);
		IBusinessProcessor processor = beanFactory.getBean(String.format("BusinessType.%s", scanSkuRequestDto.getType()), IBusinessProcessor.class);
		result = processor.createScanedSkuRenderInfo(scanSkuRequestDto.getBillOrBox(), scanSkuRequestDto.getCustomerID(), sku);
		if(!result.isSuccess()){
			result.setError(ErrorCode.SCAN_SKU_ERROR.getCode(), result.getErrorMsg());
		}
		logger.debug("scanSku() done");
		return result;
	}
	
	@Override
	public Result<Void> scanBucketSlot(ShelveScanBucketSlotRequestDto scanBucketSlotRequest) {
		logger.debug("scanBucketSlot()");
		Result<Void> result = new Result<Void>();
		String bucketSlotID = scanBucketSlotRequest.getBucketSlot();
		//1. 校验货位
		Result<Bucket> checkResult = this.checkOfflineBucketSlot(bucketSlotID);
		if(!checkResult.isSuccess()){
			result.setError(checkResult.getErrorCode(), checkResult.getErrorMsg());
			return result;
		}
		Bucket bucket = checkResult.getData();
		Integer waypointID = bucket.getWaypointID();
		List<StationWaypoint> stationWaypoints = this.serviceBeanFactory.getStationWaypointDas().queryByWaypointID(waypointID);
		if(stationWaypoints == null || stationWaypoints.isEmpty()){
			result.setError(ErrorCode.BUCKET_SLOT_UNBIND_STATION.getCode(), String.format(ErrorCode.BUCKET_SLOT_UNBIND_STATION.getMessage(), bucketSlotID));
			return result;
		}
		if(stationWaypoints.size() > 1){
			result.setError(ErrorCode.BUCKET_SLOT_UNBIND_STATION_MORE_THAN_ONE.getCode(), String.format(ErrorCode.BUCKET_SLOT_UNBIND_STATION_MORE_THAN_ONE.getMessage(), bucketSlotID));
			return result;
		}
		Result<Station> checkStationResult = this.checkOfflineStation(stationWaypoints.get(0).getStationID());
		if(!checkStationResult.isSuccess()){
			result.setError(checkStationResult.getErrorCode(), checkStationResult.getErrorMsg());
			return result;
		}
		Station station = checkStationResult.getData();
		if(!StringUtils.equalsIgnoreCase(station.getType(), StationType.OFFLINEREPLENISH.name())){
			result.setError(ErrorCode.BUCKET_SLOT_UNBIND_STATION_NOT_OFFLINE_REPLENISH_STATION.getCode(), String.format(ErrorCode.BUCKET_SLOT_UNBIND_STATION_NOT_OFFLINE_REPLENISH_STATION.getMessage(),bucketSlotID, stationWaypoints.get(0).getStationID()));
			return result;
		}
		
		//2. 校验商品批次残次属性
		if(stationEngineDaemonProperties.isCheckSkuAndBucketAttribute()){
			List<ShelveSkuDto> skus = scanBucketSlotRequest.getSkus();
			for(ShelveSkuDto sku : skus){
				if(StringUtils.isNotBlank(sku.getBatchCode())){
					//默认认为批次号格式  *****-@@-##，其中第二项即为残次属性
					String[] attributesArr = sku.getBatchCode().split("-");
					if(attributesArr.length >= 2){
						String skuImperfect = sku.getBatchCode().split("-")[1];
						if(!StringUtils.equalsIgnoreCase(skuImperfect, bucket.getBucketType().getName())){
							result.setError(ErrorCode.SHELVE_SKU_BUCKET_ATTRIBUTE_INCONFORMITY.getCode(), String.format(ErrorCode.SHELVE_SKU_BUCKET_ATTRIBUTE_INCONFORMITY.getMessage(),sku.getSkuNumber(),sku.getBatchCode(),bucket.getBucketID()));
							return result;
						}
					}
				}
			}
		}
		logger.debug("scanBucketSlot() done");
		return result;
	}

	@Override
	public Result<Void> doShelve(ShelveRequestDto shelveRequest) {
		logger.debug("doShelve()");
		Result<Void> result = new Result<Void>();
		try {
			if(shelveRequest == null){
				result.setError(ErrorCode.SHELVE_PARAM_EMPTY.getCode(),ErrorCode.SHELVE_PARAM_EMPTY.getMessage());
				return result;
			}
			List<ShelveSkuDto> shelveSkus = shelveRequest.getSkus();
			if(shelveSkus == null || shelveSkus.isEmpty()){
				result.setError(ErrorCode.SHELVE_SKU_PARAM_EMPTY.getCode(),ErrorCode.SHELVE_SKU_PARAM_EMPTY.getMessage());
				return result;
			}
			if(StringUtils.isBlank(shelveRequest.getBucketSlot())){
				result.setError(ErrorCode.SHELVE_SKU_PARAM_EMPTY.getCode(),ErrorCode.SHELVE_SKU_PARAM_EMPTY.getMessage());
				return result;
			}
			
			if(StringUtils.isBlank(shelveRequest.getBillOrBox())){
				result.setError(ErrorCode.SHELVE_BILLBOX_PARAM_EMPTY.getCode(),ErrorCode.SHELVE_BILLBOX_PARAM_EMPTY.getMessage());
				return result;
			}
			if(!StringUtils.equalsIgnoreCase(shelveRequest.getType(), BusinessType.Bill.name()) && !StringUtils.equalsIgnoreCase(shelveRequest.getType(), BusinessType.Box.name())){
				result.setError(ErrorCode.SHELVE_BILLBOX_TYPE_PARAM_ERROR.getCode(), ErrorCode.SHELVE_BILLBOX_TYPE_PARAM_ERROR.getMessage());
				return result;
			}
			if(shelveRequest.getCustomerID() == null){
				result.setError(ErrorCode.SHELVE_CUSTOMERID_PARAM_ERROR.getCode(), ErrorCode.SHELVE_CUSTOMERID_PARAM_ERROR.getMessage());
				return result;
			}
			
			IBusinessProcessor processor = beanFactory.getBean(String.format("BusinessType.%s", shelveRequest.getType()), IBusinessProcessor.class);
			String processResult = processor.doShelve(shelveRequest);
			if(StringUtils.isNotBlank(processResult)){
				result.setError(ErrorCode.SHELVE_ERROR.getCode(), processResult);
			}
		} catch (Exception e) {
			logger.error("doShelve error:",e);
			result.setError(ErrorCode.SHELVE_ERROR.getCode(), e.getMessage());
		}
		logger.debug("doShelve() done");
		return result;
	}


}
