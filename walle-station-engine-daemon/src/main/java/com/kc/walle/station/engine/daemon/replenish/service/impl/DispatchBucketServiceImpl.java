package com.kc.walle.station.engine.daemon.replenish.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.kc.walle.common.domain.core.Bucket;
import com.kc.walle.common.domain.core.BucketSlotType;
import com.kc.walle.common.domain.core.BucketType;
import com.kc.walle.common.domain.core.Station;
import com.kc.walle.common.domain.core.StationWaypoint;
import com.kc.walle.common.domain.erp.Customer;
import com.kc.walle.common.domain.erp.DirectShelveBill;
import com.kc.walle.common.enumeration.DirectshelveBillState;
import com.kc.walle.common.infrastructure.job.ReturnBucketToPickingAreaResponseCode;
import com.kc.walle.common.infrastructure.message.bucket.ReturnBucketByRFRequestMessage;
import com.kc.walle.common.infrastructure.message.bucket.ReturnBucketByRFResponseMessage;
import com.kc.walle.common.infrastructure.message.order.DirectShelveBillFullfillRequestMessage;
import com.kc.walle.common.infrastructure.message.order.DirectShelveBillFullfillResopnseMessage;
import com.kc.walle.common.infrastructure.station.StationType;
import com.kc.walle.common.infrastructure.util.IDGenerator;
import com.kc.walle.station.engine.daemon.common.ErrorCode;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.service.impl.BaseService;
import com.kc.walle.station.engine.daemon.replenish.dto.DispatchBucketRequestDto;
import com.kc.walle.station.engine.daemon.replenish.service.IDispatchBucketService;
import com.kc.walle.station.engine.daemon.replenish.vo.BucketSlotTypeVo;
import com.kc.walle.station.engine.daemon.replenish.vo.BucketTypeVo;

@Service
public class DispatchBucketServiceImpl extends BaseService implements IDispatchBucketService {
	
	@Override
	public Result<List<BucketTypeVo>> getAllBucketTypes() {
		logger.debug("getAllBucketTypes()");
		Result<List<BucketTypeVo>> result = new Result<List<BucketTypeVo>>();
		List<BucketType> bucketTypes = this.serviceBeanFactory.getBucketTypeDas().selectAll();
		if(bucketTypes == null || bucketTypes.isEmpty()){
			result.setError(ErrorCode.BUCKET_TYPE_EMPTY.getCode(), ErrorCode.BUCKET_TYPE_EMPTY.getMessage());
			return result ;
		}
		List<BucketTypeVo> bucketTypeVos = new ArrayList<BucketTypeVo>();
		for(BucketType type : bucketTypes){
			BucketTypeVo typeVo = new BucketTypeVo();
			typeVo.setId(type.getBucketTypeID());
			typeVo.setName(type.getName());
			bucketTypeVos.add(typeVo);
		}
		result.setData(bucketTypeVos);
		logger.debug("getAllBucketTypes() done");
		return result;
	}

	@Override
	public Result<Void> checkStartDispatchScanStation(String stationID) {
		logger.debug("checkStartDispatchScanStation()");
		Result<Void> result = new Result<Void>();
		result = this.checkOfflineReplenishStation(stationID);
		if(!result.isSuccess()){
			return result;
		}
		List<DirectShelveBill> shelveBills = this.serviceBeanFactory.getDirectShelveBillDas().selectByStateStationID(DirectshelveBillState.DOING.name(), stationID);
		if(shelveBills != null && !shelveBills.isEmpty()){
			result.setError(ErrorCode.STATION_DISPATCHED_BUCKETS.getCode(), String.format(ErrorCode.STATION_DISPATCHED_BUCKETS.getMessage(), stationID));
			return result;
		}
		logger.debug("checkStartDispatchScanStation() done");
		return result;
	}
	
	@Override
	public Result<Void> checkFinishDispatchScanStation(String stationID) {
		logger.debug("checkFinishDispatchScanStation()");
		Result<Void> result = new Result<Void>();
		result = this.checkOfflineReplenishStation(stationID);
		if(!result.isSuccess()){
			return result;
		}
		List<DirectShelveBill> shelveBills = this.serviceBeanFactory.getDirectShelveBillDas().selectByStateStationID(DirectshelveBillState.DOING.name(), stationID);
		if(shelveBills == null || shelveBills.isEmpty()){
			result.setError(ErrorCode.STATION_NO_DISPATCH_BUCKETS.getCode(), String.format(ErrorCode.STATION_NO_DISPATCH_BUCKETS.getMessage(), stationID));
			return result;
		}
		logger.debug("checkFinishDispatchScanStation() done");
		return result;
	}

	@Override
	public Result<List<BucketSlotTypeVo>> getAllBucketSlotTypes() {
		logger.debug("getAllBucketSlotTypes()");
		Result<List<BucketSlotTypeVo>> result = new Result<List<BucketSlotTypeVo>>();
		List<BucketSlotType> bucketSlotTypes = this.serviceBeanFactory.getBucketSlotTypeDas().selectAll();
		if(bucketSlotTypes == null || bucketSlotTypes.isEmpty()){
			result.setError(ErrorCode.BUCKET_SLOT_TYPE_EMPTY.getCode(), ErrorCode.BUCKET_SLOT_TYPE_EMPTY.getMessage());
			return result;
		}
		List<BucketSlotTypeVo> bucketSlotTypeVos = new ArrayList<BucketSlotTypeVo>();
		for(BucketSlotType type : bucketSlotTypes){
			BucketSlotTypeVo typeVo = new BucketSlotTypeVo();
			typeVo.setId(type.getBucketSlotTypeID());
			typeVo.setName(type.getName());
			bucketSlotTypeVos.add(typeVo);
		}
		result.setData(bucketSlotTypeVos);
		logger.debug("getAllBucketSlotTypes()");
		return result;
	}

	@Override
	public Result<Void> startDispatchBucket(DispatchBucketRequestDto dispatchBucketRequest) {
		logger.debug("startDispatchBucket()");
		Result<Void> result = new Result<Void>();
		try {
			if(dispatchBucketRequest == null){
				result.setError(ErrorCode.DISPATCH_BUCKET_PARAM_EMPTY.getCode(), ErrorCode.DISPATCH_BUCKET_PARAM_EMPTY.getMessage());
				return result;
			}
			if(dispatchBucketRequest.getCustomerid() == null){
				result.setError(ErrorCode.DISPATCH_BUCKET_CUSTOMER_EMPTY.getCode(), ErrorCode.DISPATCH_BUCKET_CUSTOMER_EMPTY.getMessage());
				return result;
			}
			if(StringUtils.isEmpty(dispatchBucketRequest.getOfflineStationCode())){
				result.setError(ErrorCode.DISPATCH_BUCKET_STATION_EMPTY.getCode(), ErrorCode.DISPATCH_BUCKET_STATION_EMPTY.getMessage());
				return result;
			}
			//对离线站调度时，加锁，防止重复调度
			synchronized (internerPool.intern(dispatchBucketRequest.getOfflineStationCode())) {
				logger.debug(String.format("startDispatchBucket 对工作站(%s)加锁处理调度", dispatchBucketRequest.getOfflineStationCode()));
				List<DirectShelveBill> shelveBills = this.serviceBeanFactory.getDirectShelveBillDas().selectByStateStationID(DirectshelveBillState.DOING.name(), dispatchBucketRequest.getOfflineStationCode());
				if(shelveBills != null && !shelveBills.isEmpty()){
					result.setError(ErrorCode.STATION_DISPATCHED_BUCKETS.getCode(), String.format(ErrorCode.STATION_DISPATCHED_BUCKETS.getMessage(), dispatchBucketRequest.getOfflineStationCode()));
					return result;
				}
				DirectShelveBill bill = new DirectShelveBill();
				Customer customer = this.serviceBeanFactory.getCustomerDas().selectOne(dispatchBucketRequest.getCustomerid());
				String billID = IDGenerator.nextDirectShelveBillID(customer.getCustomerCode());
				bill.setBillID(billID);
				bill.setCustomerID(customer.getCustomerID());
				bill.setState(DirectshelveBillState.DOING.name());
				bill.setStationID(dispatchBucketRequest.getOfflineStationCode());
				List<Integer> bucketType = dispatchBucketRequest.getBucketType();
				if(bucketType != null && !bucketType.isEmpty()){
					String strBucketType = String.format("%s", StringUtils.join(bucketType.iterator(), ","));
					bill.setBucketTypeID(strBucketType);
				}
				List<Integer> bucketSlotType = dispatchBucketRequest.getBucketSlotType();
				if(bucketSlotType != null && !bucketSlotType.isEmpty()){
					String strBucketSlotType = String.format("%s", StringUtils.join(bucketSlotType.iterator(), ","));
					bill.setPreferredBucketSlotTypes(strBucketSlotType);
				}
				if(dispatchBucketRequest.getMinRate() != null && dispatchBucketRequest.getMinRate() > 0){
					bill.setMinEmptyRate(dispatchBucketRequest.getMinRate()/100);
				}
				if(dispatchBucketRequest.getMaxRate() != null && dispatchBucketRequest.getMaxRate() > 0){
					bill.setMaxEmptyRate(dispatchBucketRequest.getMaxRate()/100);
				}
				bill.setCreatedUser(dispatchBucketRequest.getUsername());
				boolean hasBuckets = directShelveGetAvailableBucketFaceCount(bill);
				if(!hasBuckets){
					result.setError(ErrorCode.DISPATCH_BUCKET_NO_BUCKETS.getCode(), ErrorCode.DISPATCH_BUCKET_NO_BUCKETS.getMessage());
					return result;
				}
				this.serviceBeanFactory.getDirectShelveBillDas().insertOne(bill);
				logger.debug(String.format("startDispatchBucket 对工作站(%s)加锁处理调度结束", dispatchBucketRequest.getOfflineStationCode()));
			}
		} catch (Exception e) {
			logger.error("startDispatchBucket error:",e);
			result.setError(ErrorCode.DISPATCH_BUCKET_ERROR.getCode(), e.getMessage());
			return result;
		}
		logger.debug("startDispatchBucket() done");
		return result;
	}

	@Override
	public Result<Void> finishDispatchBucket(String stationID, String operator) {
		logger.debug("finishDispatchBucket()");
		Result<Void> result = new Result<Void>();
		if(StringUtils.isBlank(stationID)){
			result.setError(ErrorCode.FINISH_DISPATCH_BUCKET_STATION_EMPTY.getCode(), ErrorCode.FINISH_DISPATCH_BUCKET_STATION_EMPTY.getMessage());
			return result;
		}
		//结束调度时，加锁，防止重复结束调度
		synchronized (internerPool.intern(stationID)) {
			logger.debug(String.format("finishDispatchBucket 对工作站(%s)加锁处理调度",stationID));
			List<DirectShelveBill> shelveBills = this.serviceBeanFactory.getDirectShelveBillDas().selectByStateStationID(DirectshelveBillState.DOING.name(), stationID);
			if(shelveBills == null || shelveBills.isEmpty()){
				result.setError(ErrorCode.STATION_NO_DISPATCH_BUCKETS.getCode(), String.format(ErrorCode.STATION_NO_DISPATCH_BUCKETS.getMessage(), stationID));
				return result;
			}
			DirectShelveBill bill = shelveBills.get(0);
			bill.setState(DirectshelveBillState.DONE.name());
			bill.setLastUpdatedUser(operator);
			this.serviceBeanFactory.getDirectShelveBillDas().updateState(bill);
			logger.debug(String.format("finishDispatchBucket 对工作站(%s)加锁处理调度结束",stationID));
		}
		logger.debug("finishDispatchBucket() done");
		return result;
	}

	@Override
	public Result<Void> releaseBucketByBucketSlotID(String bucketSlotID) {
		logger.debug("releaseBucketByBucketSlotID()");
		Result<Void> result = new Result<Void>();
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
		Result<Void> checkStationResult = this.checkOfflineReplenishStation(stationWaypoints.get(0).getStationID());
		if(!checkStationResult.isSuccess()){
			result.setError(checkStationResult.getErrorCode(), checkStationResult.getErrorMsg());
			return result;
		}
		String returnMessage = requestReleaseBucket(bucket.getBucketID(), bucket.getWaypointID(),stationWaypoints.get(0).getStationID());
		if(StringUtils.isNotBlank(returnMessage)){
			result.setError(ErrorCode.RELEASE_BUCKET_REQUEST_ERROR.getCode(), returnMessage);
			return result;
		}
		logger.debug("releaseBucketByBucketSlotID() done");
		return result;
	}

	private String requestReleaseBucket(String bucketID,Integer waypointID,String stationID) {
		logger.info(String.format("requestReleaseBucket bucketID:%s;waypointID:%s;stationID:%s", bucketID,waypointID,stationID));
		String result = null;
		try {
			ReturnBucketByRFRequestMessage reqMsg = new ReturnBucketByRFRequestMessage();
			reqMsg.setBucketID(bucketID);
			reqMsg.setWaypointID(waypointID);
			reqMsg.setParentID(stationID);
			ObjectMessage obj_response_msg = serviceBeanFactory.getBucketLeaveRequester().request(reqMsg, ReturnBucketByRFRequestMessage.class.getName(), true, 60000);
			logger.info("requestReleaseBucket obj_response_msg:"+obj_response_msg);
			if(obj_response_msg == null){
				return ReturnBucketToPickingAreaResponseCode.TIME_OUT.getName();
			}
			ReturnBucketByRFResponseMessage res_msg = null;
			res_msg = (ReturnBucketByRFResponseMessage)obj_response_msg.getObject();
			if(res_msg.getCode() != null && !ReturnBucketToPickingAreaResponseCode.SUCC.equals(res_msg.getCode())){
				result = res_msg.getCode().getName();
			}
		} catch (Exception e) {
			logger.error("Exception happens in requestReleaseBucket()", e);
			return e.getMessage();
		}
		logger.debug("requestReleaseBucket() done");
		return result;
	}
	
	private Result<Void> checkOfflineReplenishStation(String stationID){
		Result<Void> result = new Result<Void>();
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
		return result;
	}
	
	public boolean directShelveGetAvailableBucketFaceCount(DirectShelveBill bill) {
		logger.debug("directShelveGetAvailableBucketFaceCount()");
		try {
			
			DirectShelveBillFullfillRequestMessage message = new DirectShelveBillFullfillRequestMessage();
			message.setDirectShelveBill(bill);
			ObjectMessage objMsg =  serviceBeanFactory.getJobReporter().request(message, DirectShelveBillFullfillRequestMessage.class.getName(),60000);
			
			if (objMsg == null) {
				throw new RuntimeException("连接manager服务器超时，请联系管理员");
			}else{
				DirectShelveBillFullfillResopnseMessage response = (DirectShelveBillFullfillResopnseMessage) objMsg.getObject();
				logger.debug(String.format("校验是否有可用货架:%s", response.isHasBucket()));
				return response.isHasBucket();
			}
		} catch (Exception e) {
			logger.error("Exception happens in directShelveGetAvailableBucketFaceCount()", e);
		}
		return false;
	}

}
