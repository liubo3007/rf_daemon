package com.kc.walle.station.engine.daemon.replenish.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kc.walle.common.domain.erp.Box;
import com.kc.walle.common.domain.erp.BoxDetail;
import com.kc.walle.common.domain.erp.GoodsInBill;
import com.kc.walle.common.domain.erp.GoodsInBillDetail;
import com.kc.walle.common.domain.erp.ReplenishmentOrder;
import com.kc.walle.common.domain.erp.ReplenishmentOrderDetail;
import com.kc.walle.common.domain.erp.Sku;
import com.kc.walle.common.domain.erp.vo.SkuBatchVO;
import com.kc.walle.common.enumeration.BucketArea;
import com.kc.walle.common.enumeration.GoodsInBillState;
import com.kc.walle.common.enumeration.ReplenishMode;
import com.kc.walle.common.enumeration.SourceBillType;
import com.kc.walle.common.infrastructure.common.OrderState;
import com.kc.walle.common.infrastructure.util.NumberUtils;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.service.impl.BaseService;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveSkuDto;
import com.kc.walle.station.engine.daemon.replenish.service.IBusinessProcessor;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuBatchRenderVo;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuRenderVo;

/**
 * 箱子业务类型处理器
 * @author LIUBO
 * 2018年9月7日
 */
@Service("BusinessType.Box")
public class BoxBusinessProcessorImpl extends BaseService  implements IBusinessProcessor {

	@Autowired
	private BillBusinessProcessorImpl billBusinessProcessorImpl;
	
	@Override
	public Result<ScanedSkuRenderVo> createScanedSkuRenderInfo(String boxNumber,Integer customerID,Sku sku) {
		logger.debug("createScanedSkuRenderInfo");
		Result<ScanedSkuRenderVo> result = new Result<ScanedSkuRenderVo>();
		Box box = this.serviceBeanFactory.getBoxDas().selectByNumberAndCustomerWithDetails(boxNumber, customerID);
		if(box == null){
			result.setError("",String.format("扫描的商品(%s)未匹配到LPN", sku.getSkuNumber()));
			return result;
		}
		List<BoxDetail> details = box.getDetails();
		if(details == null || details.isEmpty()){
			result.setError("",String.format(String.format("扫描的商品(%s)未匹配到LPN明细", sku.getSkuNumber())));
			return result;
		}
		Map<String,BoxDetail> skuBatchDetailMap = new HashMap<String, BoxDetail>();
		List<BoxDetail> matchUnDoneDetails = new ArrayList<BoxDetail>();
		List<ScanedSkuBatchRenderVo> scanedSkuBatchs = new ArrayList<ScanedSkuBatchRenderVo>();
		Integer allQuantity = 0;
		for(BoxDetail detail : details){
			if(StringUtils.equalsIgnoreCase(detail.getSkuID(), sku.getSkuID()) && NumberUtils.intValue(detail.getQuantity()) > 0){
				//明细中同一个商品要么全部带批次，要么全部不带批次
				if(StringUtils.isNotBlank(detail.getBatchNumber())){
					skuBatchDetailMap.put(detail.getBatchNumber(), detail);
				}
				matchUnDoneDetails.add(detail);
			}
		}
		if(matchUnDoneDetails.isEmpty()){
			result.setError("",String.format("扫描的商品(%s)不在LPN(%s)中或已完成上架",sku.getSkuNumber(), boxNumber));
			return result;
		}
		for(BoxDetail detail : matchUnDoneDetails){
			allQuantity += NumberUtils.intValue(detail.getQuantity());
		}
		if(!skuBatchDetailMap.isEmpty()){
			Map<String, SkuBatchVO> skuBatchMap = this.serviceBeanFactory.getSkuBatchDas().selectByBatchNumberInBatchAsMap(new ArrayList<String>(skuBatchDetailMap.keySet()));
			for (Entry<String,BoxDetail> entry : skuBatchDetailMap.entrySet()) {
				String batchNumber = entry.getKey();
				BoxDetail detail = entry.getValue();
				ScanedSkuBatchRenderVo scanBatch = new ScanedSkuBatchRenderVo();
				scanBatch.setBatchNumber(batchNumber);
				scanBatch.setBatchCode(skuBatchMap.get(batchNumber) == null ? null : skuBatchMap.get(batchNumber).getBatchCode());
				scanBatch.setQuantity(NumberUtils.intValue(detail.getQuantity()));
				scanedSkuBatchs.add(scanBatch);
			}
		}
		ScanedSkuRenderVo scanedSkuVo = new ScanedSkuRenderVo();
		scanedSkuVo.setSkuid(sku.getSkuID());
		scanedSkuVo.setSkuName(sku.getName());
		scanedSkuVo.setSkuNumber(sku.getSkuNumber());
		scanedSkuVo.setQuantity(allQuantity);
		scanedSkuVo.setSkuBatch(scanedSkuBatchs);
		result.setData(scanedSkuVo);
		return result;
	}

	@Override
	public String checkScanBillOrBox(String billOrBox, Integer customerID) {
		logger.debug("checkScanBox()");
		String errorMsg = "";
		Box box = this.serviceBeanFactory.getBoxDas().selectByNumberAndCustomerWithDetails(billOrBox, customerID);
		if(box == null){
			return String.format("LPN号(%s),客户(%s)未搜索到对应上架箱", billOrBox,customerID);
		}
		List<BoxDetail> details = box.getDetails();
		if(details == null || details.isEmpty()){
			return String.format("LPN号(%s),客户(%s)没有待上架明细", billOrBox,customerID);
		}
		List<String> states = new ArrayList<String>();
		states.add(GoodsInBillState.WAITING_RECEIVED.name());
		states.add(GoodsInBillState.SHELVEING_DOING.name());
		List<GoodsInBill> bindBills = this.serviceBeanFactory.getGoodsInBillDas().selectByBoxNosStatesCustomerID(Arrays.asList(billOrBox), states, customerID);
		if(bindBills == null || bindBills.isEmpty()){
			return String.format("LPN号(%s),客户(%s)还未绑定上架单，请稍后重试", billOrBox,customerID);
		}
		if(bindBills.size() > 1){
			return String.format("LPN号(%s),客户(%s)绑定了多个上架单", billOrBox,customerID);
		}
		GoodsInBill bindBill = bindBills.get(0);
		for(BoxDetail detail : details){
			if (!StringUtils.equals(detail.getSourceBillID(), bindBill.getGoodsInBillID())) {
				return String.format("LPN号(%s),客户(%s)还未成功绑定上架单(%s)，请稍后重试", billOrBox,customerID,bindBill.getGoodsInBillNumber());
			}
		}
		logger.debug("checkScanBox() done");
		return errorMsg;
	}


	@Override
	public String doShelve(ShelveRequestDto shelveRequest) {
		logger.debug("doShelve()");
		String errorMsg = "";
		//按箱号加锁处理
		synchronized (internerPool.intern(shelveRequest.getBillOrBox())) {
			Box box = this.serviceBeanFactory.getBoxDas().selectByNumberAndCustomerWithDetails(shelveRequest.getBillOrBox(), shelveRequest.getCustomerID());
			if(box == null){
				return String.format("LPN号(%s),客户(%s)未搜索到对应上架箱", shelveRequest.getCustomerID());
			}
			List<BoxDetail> details = box.getDetails();
			if(details == null || details.isEmpty()){
				return String.format("LPN号(%s),客户(%s)没有待上架明细", shelveRequest.getCustomerID());
			}
			Map<String,ShelveSkuDto> detailShelveSkuMap = new HashMap<String, ShelveSkuDto>();
			Map<String,BoxDetail> boxDetailMap = new HashMap<String, BoxDetail>();
			List<BoxDetail> updateQuantityDetails = new ArrayList<BoxDetail>();
			String cannotShelveSkus = "";
			for(ShelveSkuDto shelveSku : shelveRequest.getSkus()){
				boolean contain = false;
				String shelveSkuBatchLabel = String.format("%s_%s", shelveSku.getSkuid(),shelveSku.getSkuBatch());
				for(BoxDetail detail : details){
					String detailSkuBatchLabel = String.format("%s_%s", detail.getSkuID(),detail.getBatchNumber());
					if(StringUtils.equalsIgnoreCase(detailSkuBatchLabel, shelveSkuBatchLabel) && NumberUtils.intValue(detail.getQuantity()) > 0){
						detailShelveSkuMap.put(detail.getBoxDetailID(), shelveSku);
						boxDetailMap.put(detail.getBoxDetailID(), detail);
						contain = true;
						break;
					}
				}
				if(!contain){
					cannotShelveSkus += shelveSku.getSkuNumber()+",";
				}
			}
			ReplenishmentOrder order = new ReplenishmentOrder();
			order.setCustomerID(box.getCustomerID());
			order.setStationID(shelveRequest.getOfflineStation());
			order.setBucketArea(BucketArea.PICKING);
			order.setSourceBillType(SourceBillType.GOODS_IN);
			order.setSourceBillID(details.get(0).getSourceBillID());
			order.setState(OrderState.DONE.name());
			order.setReplenishMode(ReplenishMode.RF_SPARE_BY_LPN);
			order.setCreatedUser(shelveRequest.getUsername());
			order.setLastUpdatedUser(shelveRequest.getUsername());
			List<ReplenishmentOrderDetail> replenishDetails = new ArrayList<ReplenishmentOrderDetail>();
			for (Entry<String,ShelveSkuDto> entry : detailShelveSkuMap.entrySet()) {
				String boxDetailID = entry.getKey();
				ShelveSkuDto shelveSku = entry.getValue();
				BoxDetail boxDetail = boxDetailMap.get(boxDetailID);
				Integer remainQuantity = boxDetail.getQuantity();
				if(shelveSku.getSkuQuantity() <= 0){
					logger.debug(String.format("sku(%s) shelve quantity is 0", shelveSku.getSkuid()));
					continue;
				}
				Integer shelveQuantity = Math.min(remainQuantity, shelveSku.getSkuQuantity());
				ReplenishmentOrderDetail replenishDetail = new ReplenishmentOrderDetail();
				replenishDetail.setSkuID(shelveSku.getSkuid());
				replenishDetail.setBatchNumber(shelveSku.getSkuBatch());
				replenishDetail.setPackID(boxDetail.getPackID());
				replenishDetail.setQuantity(shelveQuantity);
				replenishDetail.setFulfillQuantity(shelveQuantity);
				replenishDetail.setAssistLocate(shelveRequest.getBucketSlot());
				replenishDetail.setSourceBillDetailID(boxDetail.getSourceBillDetailID());
				replenishDetail.setCreatedUser(shelveRequest.getUsername());
				replenishDetail.setLastUpdatedUser(shelveRequest.getUsername());
				replenishDetails.add(replenishDetail);
				
				BoxDetail updateBoxDetail = new BoxDetail();
				updateBoxDetail.setSourceBillID(order.getSourceBillID());
				updateBoxDetail.setSourceBillDetailID(replenishDetail.getSourceBillDetailID());
				updateBoxDetail.setBoxDetailID(boxDetail.getBoxDetailID());
				updateBoxDetail.setQuantity(shelveQuantity);
				updateQuantityDetails.add(updateBoxDetail);
			}
			if(!replenishDetails.isEmpty()){
				order.setOrderDetails(replenishDetails);
				this.serviceBeanFactory.getReplenishOrderDas().createPickingAreaStorageOrder(order);
			}
			
			doSkuShelveUpdateBoxInfo(box, updateQuantityDetails, shelveRequest.getUsername());
			if(StringUtils.isNotBlank(cannotShelveSkus)){
				errorMsg = String.format("以下商品已经上架完成了:%s", cannotShelveSkus);
			}
			
			//如果有单据信息，更新单据信息
			updateBillInfo(shelveRequest.getBillOrBox(), shelveRequest.getCustomerID(), shelveRequest.getUsername(), updateQuantityDetails);
		}
		logger.debug("doShelve() done");
		return errorMsg;
	}
	
	public void doSkuShelveUpdateBoxInfo(Box box,List<BoxDetail> updateQuantityDetails,String operateUser){
		logger.debug("doSkuShelveUpdateBoxInfo()");
		List<BoxDetail> deleteDetails = new ArrayList<BoxDetail>();
		List<BoxDetail> updateDetails = new ArrayList<BoxDetail>();
		Map<String,Integer> detailQuantityMap = new HashMap<String, Integer>();
		for(BoxDetail detail : updateQuantityDetails){
			detailQuantityMap.put(detail.getBoxDetailID(),detail.getQuantity());
		}
		for(BoxDetail detail : box.getDetails()){
			if(detailQuantityMap.containsKey(detail.getBoxDetailID())){
				Integer remainQuantity = NumberUtils.intValue(detail.getQuantity()) - NumberUtils.intValue(detailQuantityMap.get(detail.getBoxDetailID()));
				if(NumberUtils.intValue(remainQuantity) > 0){
					detail.setQuantity(remainQuantity);
					detail.setLastUpdatedUser(operateUser);
					updateDetails.add(detail);
				}else{
					deleteDetails.add(detail);
				}
			}
		}
		if (deleteDetails.size() > 0) {
			for (BoxDetail boxDetail : deleteDetails) {
				this.serviceBeanFactory.getBoxDetailDas().deleteOne(boxDetail.getBoxDetailID());
			}
		}
		if (updateDetails.size() > 0) {
			for (BoxDetail boxDetail : updateDetails) {
				this.serviceBeanFactory.getBoxDetailDas().updateOne(boxDetail);
			}
		}
		logger.debug("doSkuShelveUpdateBoxInfo() done");
	}
	
	private void updateBillInfo(String boxNo,Integer customerID,String operator,List<BoxDetail> updateQuantityDetails){
		if(!updateQuantityDetails.isEmpty()){
			GoodsInBill bill = this.serviceBeanFactory.getGoodsInBillDas().selectOneWithDetailsOnly(updateQuantityDetails.get(0).getSourceBillID());
			if(bill != null && bill.getGoodsInBillDetails() != null && !bill.getGoodsInBillDetails().isEmpty()){
				Map<String,BoxDetail> boxDetailMap = new HashMap<String, BoxDetail>();
				List<GoodsInBillDetail> updateGoodsInBillDetails = new ArrayList<GoodsInBillDetail>();
				if(!updateQuantityDetails.isEmpty()){
					for(BoxDetail detail : updateQuantityDetails){
						boxDetailMap.put(detail.getSourceBillDetailID(), detail);
					}
				}
				for(GoodsInBillDetail detail : bill.getGoodsInBillDetails()){
					if(boxDetailMap.containsKey(detail.getGoodsInBillDetailID())){
						BoxDetail boxDetail = boxDetailMap.get(detail.getGoodsInBillDetailID());
						GoodsInBillDetail updateBillDetail = new GoodsInBillDetail();
						updateBillDetail.setGoodsInBillDetailID(detail.getGoodsInBillDetailID());
						updateBillDetail.setSkuID(detail.getSkuID());
						updateBillDetail.setBatchNumber(detail.getBatchNumber());
						updateBillDetail.setPackID(detail.getPackID());
						updateBillDetail.setReceivedQuantity(boxDetail.getQuantity());
						updateBillDetail.setPickAreaQuantity(boxDetail.getQuantity());
						updateBillDetail.setLastUpdatedUser(operator);
						updateGoodsInBillDetails.add(updateBillDetail);
					}
				}
				if(!updateGoodsInBillDetails.isEmpty()){
					billBusinessProcessorImpl.doSkuShelveUpdateBillInfo(bill, updateGoodsInBillDetails, boxNo, operator);
				}
			}
		}
	}
}
