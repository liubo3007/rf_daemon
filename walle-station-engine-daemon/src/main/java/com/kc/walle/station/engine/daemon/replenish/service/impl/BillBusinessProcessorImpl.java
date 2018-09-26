package com.kc.walle.station.engine.daemon.replenish.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kc.walle.common.domain.erp.Box;
import com.kc.walle.common.domain.erp.BoxDetail;
import com.kc.walle.common.domain.erp.GoodsInBill;
import com.kc.walle.common.domain.erp.GoodsInBillDetail;
import com.kc.walle.common.domain.erp.ReplenishQuota;
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
import com.kc.walle.common.infrastructure.webapp.query.QueryCondition;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.service.impl.BaseService;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveRequestDto;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveSkuDto;
import com.kc.walle.station.engine.daemon.replenish.service.IBusinessProcessor;
import com.kc.walle.station.engine.daemon.replenish.vo.GoodsInBillVo;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuBatchRenderVo;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuRenderVo;

/**
 * 单据业务类型处理器
 * @author LIUBO
 * 2018年9月7日
 */
@Service("BusinessType.Bill")
public class BillBusinessProcessorImpl extends BaseService  implements IBusinessProcessor{

	@Autowired
	private BoxBusinessProcessorImpl boxBusinessProcessorImpl;
	
	@Override
	public Result<ScanedSkuRenderVo> createScanedSkuRenderInfo(String billNumber,Integer customerID,Sku sku) {
		logger.debug("createScanedSkuRenderInfo");
		Result<ScanedSkuRenderVo> result = new Result<ScanedSkuRenderVo>();
		String skuNumber = sku.getSkuNumber();
		List<GoodsInBill> goodsInBills = this.serviceBeanFactory.getGoodsInBillDas().selectByCustomerIDAndBillNumberWithDetailsOnly(customerID,billNumber);
		if(goodsInBills == null || goodsInBills.isEmpty()){
			result.setError("", String.format("扫描的商品(%s)未匹配到对应上架单(%s)",skuNumber,billNumber));
			return result;
		}
		GoodsInBill bill = goodsInBills.get(0);
		List<GoodsInBillDetail> details = bill.getGoodsInBillDetails();
		if(details == null || details.isEmpty()){
			result.setError("",String.format("扫描的商品(%s)未匹配到对应上架单明细", skuNumber));
			return result;
		}
		Map<String,GoodsInBillDetail> skuBatchDetailMap = new HashMap<String, GoodsInBillDetail>();
		List<GoodsInBillDetail> matchUnDoneDetails = new ArrayList<GoodsInBillDetail>();
		List<ScanedSkuBatchRenderVo> scanedSkuBatchs = new ArrayList<ScanedSkuBatchRenderVo>();
		Integer allQuantity = 0;
		Map<String,Integer> remainQuantityMap = this.calcSkuRemainQuantity(bill);
		boolean contain = false;
		for(GoodsInBillDetail detail : details){
			String skuIDBatchPack = String.format("%s_%s_%s", detail.getSkuID(),detail.getBatchNumber(),detail.getPackID());
			if(StringUtils.equalsIgnoreCase(detail.getSkuID(),sku.getSkuID())){
				contain = true;
				if(NumberUtils.intValue(remainQuantityMap.get(skuIDBatchPack)) > 0){
					//明细中同一个商品要么全部带批次，要么全部不带批次
					if(StringUtils.isNotBlank(detail.getBatchNumber())){
						skuBatchDetailMap.put(detail.getBatchNumber(), detail);
					}
					matchUnDoneDetails.add(detail);
				}
			}
		}
		if(!contain){
			result.setError("",String.format("扫描的商品(%s)不在上架单(%s)中", skuNumber,billNumber));
			return result;
		}
		if(matchUnDoneDetails.isEmpty()){
			result.setError("",String.format("扫描的商品(%s)在上架单(%s)中已完成上架", sku.getSkuNumber(),bill.getGoodsInBillNumber()));
			return result;
		}
		for(GoodsInBillDetail detail : matchUnDoneDetails){
			String skuIDBatchPack = String.format("%s_%s_%s", detail.getSkuID(),detail.getBatchNumber(),detail.getPackID());
			allQuantity += remainQuantityMap.get(skuIDBatchPack);
		}
		if(!skuBatchDetailMap.isEmpty()){
			Map<String, SkuBatchVO> skuBatchMap = this.serviceBeanFactory.getSkuBatchDas().selectByBatchNumberInBatchAsMap(new ArrayList<String>(skuBatchDetailMap.keySet()));
			for (Entry<String,GoodsInBillDetail> entry : skuBatchDetailMap.entrySet()) {
				String batchNumber = entry.getKey();
				GoodsInBillDetail detail = entry.getValue();
				String skuIDBatchPack = String.format("%s_%s_%s", detail.getSkuID(),detail.getBatchNumber(),detail.getPackID());
				ScanedSkuBatchRenderVo scanBatch = new ScanedSkuBatchRenderVo();
				scanBatch.setBatchNumber(batchNumber);
				scanBatch.setBatchCode(skuBatchMap.get(batchNumber) == null ? null : skuBatchMap.get(batchNumber).getBatchCode());
				scanBatch.setQuantity(remainQuantityMap.get(skuIDBatchPack));
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
		logger.debug("createScanedSkuRenderInfo done");
		return result;
	}
	
	@Override
	public String checkScanBillOrBox(String billOrBox, Integer customerID) {
		logger.debug("checkScanBillOrBox()");
		String errorMsg = "";
		List<GoodsInBill> goodsInBills = this.serviceBeanFactory.getGoodsInBillDas().selectByCustomerIDAndBillNumberWithDetailsOnly(customerID,billOrBox);
		if(goodsInBills == null || goodsInBills.isEmpty()){
			return String.format("单号(%s),客户(%s)未搜索到对应上架单信息", billOrBox,customerID);
		}
		GoodsInBill bill = goodsInBills.get(0);
		if((!StringUtils.equalsIgnoreCase(bill.getState(), GoodsInBillState.WAITING_RECEIVED.name())) && (!StringUtils.equalsIgnoreCase(bill.getState(), GoodsInBillState.SHELVEING_DOING.name()))){
			return String.format("单号(%s),客户(%s)对应上架单状态已不是可上架状态", billOrBox,customerID);
		}
		if(!isOnlyContainOneBox(bill)){
			return String.format("单号(%s),客户(%s)包含多个箱子", billOrBox,customerID);
		}
		if(this.isBillFinish(bill)){
			return String.format("单号(%s),客户(%s)对应上架单已上架完成", billOrBox,customerID);
		}
		logger.debug("checkScanBillOrBox() done");
		return errorMsg;
	}


	public Result<List<GoodsInBillVo>> selectWaitingShelveBills(Integer customerID) {
		logger.debug("selectWaitingShelveBills()");
		Result<List<GoodsInBillVo>> result = new Result<List<GoodsInBillVo>>();
		List<String> states = new ArrayList<String>();
		states.add(GoodsInBillState.WAITING_RECEIVED.name());
		states.add(GoodsInBillState.SHELVEING_DOING.name());
		List<GoodsInBill> goodsInBills = this.serviceBeanFactory.getGoodsInBillDas().selectByCustomerIDAndStateWithDetails(customerID, states);
		List<GoodsInBillVo> billVos = new ArrayList<GoodsInBillVo>();
		if(goodsInBills != null && !goodsInBills.isEmpty()){
			for(GoodsInBill bill : goodsInBills){
				List<GoodsInBillDetail> details = bill.getGoodsInBillDetails();
				if(details == null || details.isEmpty()){
					logger.debug(String.format("单据(%s)不包含明细",bill.getGoodsInBillNumber()));
					continue;
				}
				//1. 单据选择只支持包含一箱的单据
				if(!isOnlyContainOneBox(bill)){
					logger.debug(String.format("单据(%s)包含多箱",bill.getGoodsInBillNumber()));
					continue;
				}
				//2. 判断是否已完成
				if(!this.isBillFinish(bill)){
					logger.debug(String.format("单据(%s)未完成",bill.getGoodsInBillNumber()));
					GoodsInBillVo billVo = new GoodsInBillVo();
					billVo.setId(bill.getGoodsInBillID());
					billVo.setName(bill.getGoodsInBillNumber());
					billVos.add(billVo);
				}
			}
		}
		result.setData(billVos);
		logger.debug("selectWaitingShelveBills() done");
		return result;
	} 
	
	
	@Override
	public String doShelve(ShelveRequestDto shelveRequest) {
		logger.debug("doShelve()");
		String errorMsg = "";
		//按单加锁处理
		synchronized (internerPool.intern(shelveRequest.getBillOrBox())) {
					
			List<GoodsInBill> goodsInBills = this.serviceBeanFactory.getGoodsInBillDas().selectByCustomerIDAndBillNumberWithDetailsOnly(shelveRequest.getCustomerID(),shelveRequest.getBillOrBox());
			if(goodsInBills == null || goodsInBills.isEmpty()){
				return String.format("单号(%s),客户(%s)未搜索到对应上架单信息", shelveRequest.getBillOrBox(),shelveRequest.getCustomerID());
			}
			GoodsInBill bill = goodsInBills.get(0);
			if((!StringUtils.equalsIgnoreCase(bill.getState(), GoodsInBillState.WAITING_RECEIVED.name())) && (!StringUtils.equalsIgnoreCase(bill.getState(), GoodsInBillState.SHELVEING_DOING.name()))){
				return String.format("单号(%s),客户(%s)对应上架单状态已不是可上架状态", shelveRequest.getBillOrBox(),shelveRequest.getCustomerID());
			}
			List<GoodsInBillDetail> details = bill.getGoodsInBillDetails();
			if(details == null || details.isEmpty()){
				return String.format("单据(%s)不包含明细",shelveRequest.getBillOrBox());
			}
			Map<String,ShelveSkuDto> detailShelveSkuMap = new HashMap<String, ShelveSkuDto>();
			Map<String,GoodsInBillDetail> billDetailMap = new HashMap<String, GoodsInBillDetail>();
			List<GoodsInBillDetail> updateQuantityDetails = new ArrayList<GoodsInBillDetail>();
			for(ShelveSkuDto shelveSku : shelveRequest.getSkus()){
				String shelveSkuBatchLabel = String.format("%s_%s", shelveSku.getSkuid(),shelveSku.getSkuBatch());
				for(GoodsInBillDetail detail : details){
					String detailSkuBatchLabel = String.format("%s_%s", detail.getSkuID(),detail.getBatchNumber());
					if(StringUtils.equalsIgnoreCase(detailSkuBatchLabel, shelveSkuBatchLabel)){
						detailShelveSkuMap.put(detail.getGoodsInBillDetailID(), shelveSku);
						billDetailMap.put(detail.getGoodsInBillDetailID(), detail);
						break;
					}
				}
			}
		
			Map<String,Integer> remainQuantityMap = calcSkuRemainQuantity(bill);
			ReplenishmentOrder order = new ReplenishmentOrder();
			order.setCustomerID(bill.getCustomerID());
			order.setStationID(shelveRequest.getOfflineStation());
			order.setBucketArea(BucketArea.PICKING);
			order.setSourceBillType(SourceBillType.GOODS_IN);
			order.setSourceBillID(bill.getGoodsInBillID());
			order.setState(OrderState.DONE.name());
			order.setReplenishMode(ReplenishMode.RF_SPARE_BY_BILL);
			order.setCreatedUser(shelveRequest.getUsername());
			order.setLastUpdatedUser(shelveRequest.getUsername());
			String cannotShelveSkus = "";
			List<ReplenishmentOrderDetail> replenishDetails = new ArrayList<ReplenishmentOrderDetail>();
			for (Entry<String,ShelveSkuDto> entry : detailShelveSkuMap.entrySet()) {
				String billDetailID = entry.getKey();
				ShelveSkuDto shelveSku = entry.getValue();
				GoodsInBillDetail billDetail = billDetailMap.get(billDetailID);
				Integer remainQuantity = remainQuantityMap.get(String.format("%s_%s_%s", billDetail.getSkuID(),billDetail.getBatchNumber(),billDetail.getPackID()));
				if(NumberUtils.intValue(remainQuantity) <= 0){
					cannotShelveSkus += shelveSku.getSkuNumber()+",";
					continue;
				}
				if(shelveSku.getSkuQuantity() <= 0){
					logger.debug(String.format("sku(%s) shelve quantity is 0", shelveSku.getSkuid()));
					continue;
				}
				Integer shelveQuantity = Math.min(remainQuantity, shelveSku.getSkuQuantity());
				ReplenishmentOrderDetail replenishDetail = new ReplenishmentOrderDetail();
				replenishDetail.setSkuID(shelveSku.getSkuid());
				replenishDetail.setBatchNumber(shelveSku.getSkuBatch());
				replenishDetail.setPackID(billDetail.getPackID());
				replenishDetail.setQuantity(shelveQuantity);
				replenishDetail.setFulfillQuantity(shelveQuantity);
				replenishDetail.setAssistLocate(shelveRequest.getBucketSlot());
				replenishDetail.setSourceBillDetailID(billDetail.getGoodsInBillDetailID());
				replenishDetail.setCreatedUser(shelveRequest.getUsername());
				replenishDetail.setLastUpdatedUser(shelveRequest.getUsername());
				replenishDetails.add(replenishDetail);
				
				GoodsInBillDetail updateBillDetail = new GoodsInBillDetail();
				updateBillDetail.setGoodsInBillDetailID(billDetail.getGoodsInBillDetailID());
				updateBillDetail.setSkuID(shelveSku.getSkuid());
				updateBillDetail.setBatchNumber(shelveSku.getSkuBatch());
				updateBillDetail.setPackID(billDetail.getPackID());
				updateBillDetail.setReceivedQuantity(shelveQuantity);
				updateBillDetail.setPickAreaQuantity(shelveQuantity);
				updateBillDetail.setLastUpdatedUser(shelveRequest.getUsername());
				updateQuantityDetails.add(updateBillDetail);
			}
			if(!replenishDetails.isEmpty()){
				order.setOrderDetails(replenishDetails);
				this.serviceBeanFactory.getReplenishOrderDas().createPickingAreaStorageOrder(order);
			}
			doSkuShelveUpdateBillInfo(bill, updateQuantityDetails, details.get(0).getBoxNo(), shelveRequest.getUsername());
			if(StringUtils.isNotBlank(cannotShelveSkus)){
				errorMsg = String.format("以下商品已经上架完成了:%s", cannotShelveSkus);
			}
			
			//如果有箱子信息，调用箱子服务更新
			updateBoxInfo(details.get(0).getBoxNo(), shelveRequest.getCustomerID(), shelveRequest.getUsername(), updateQuantityDetails);
		}
		logger.debug("doShelve() done");
		return errorMsg;
	}
	
	
	/**
	 * 判断单据是否只包含一个箱子
	 * @param bill
	 * @return
	 */
	private boolean isOnlyContainOneBox(GoodsInBill bill){
		Set<String> boxSet = new HashSet<String>();
		List<GoodsInBillDetail> details = bill.getGoodsInBillDetails();
		for(GoodsInBillDetail detail : details){
			boxSet.add(detail.getBoxNo());
		}
		return boxSet.size() == 1;
	}
	
	/**
	 * 判断单据是否已收货
	 * @param bill
	 * @return
	 */
	private boolean isReceived(GoodsInBill bill,String boxNo){
		boolean boxReceived =false;
		//明细里boxDone要么全是 1 要么全是 0
		for(GoodsInBillDetail detail : bill.getGoodsInBillDetails()){
			if(!StringUtils.equalsIgnoreCase(boxNo, detail.getBoxNo())){
				continue;
			}
			if(!boxReceived){
				boxReceived = detail.isBoxDone();
			}
		}
		logger.debug(String.format("单据(%s)是否已收货:%s", bill.getGoodsInBillNumber(),boxReceived));
		return boxReceived;
	}
	
	/**
	 * 单据是否作业完成
	 * @param bill
	 * @return
	 */
	private boolean isBillFinish(GoodsInBill bill){
		boolean finish =false;
		boolean boxReceived = isReceived(bill,bill.getGoodsInBillDetails().get(0).getBoxNo());
		List<GoodsInBillDetail> details = bill.getGoodsInBillDetails();
		//未收货的，存在收货数量不等于应收数量的即为未完成
		if(!boxReceived){
			for(GoodsInBillDetail detail : details){
				if(!NumberUtils.equals(detail.getQuantity(), detail.getReceivedQuantity())){
					return false;
				}
			}
		}
		//已收货的，查询额度表，获取剩余数量
		else{
			Map<String,Integer> quotaMap = getReplenishQuota(bill.getGoodsInBillID());
			for(GoodsInBillDetail detail : details){
				String skuIDBatchPack = String.format("%s_%s_%s", detail.getSkuID(),detail.getBatchNumber(),detail.getPackID());
				Integer quantity = quotaMap.get(skuIDBatchPack);
				if(NumberUtils.intValue(quantity) > 0){
					return false;
				}
			}
		}
		return finish;
	}
	
	/**
	 * 获取上架单对应的明细上架额度
	 * @param bill
	 * @return
	 */
	private Map<String,Integer> getReplenishQuota(String goodsInBillID){
		List<QueryCondition> lstQC = new ArrayList<QueryCondition>();
		lstQC.add(new QueryCondition("AND", "", "topSourceBillType", "=", SourceBillType.GOODS_IN.name(), ""));
		lstQC.add(new QueryCondition("AND", "", "topSourceBillID", "=", goodsInBillID, ""));
		List<ReplenishQuota> replenishQuotas = serviceBeanFactory.getReplenishQuotaDas().queryList(lstQC);
		Map<String,Integer> quotaMap = new HashMap<String, Integer>();
		if(replenishQuotas != null && !replenishQuotas.isEmpty()){
			for (ReplenishQuota quota : replenishQuotas) {
				String skuIDBatchPack = String.format("%s_%s_%s", quota.getSkuID(),quota.getBatchNumber(),quota.getPackID());
				if(quotaMap.containsKey(skuIDBatchPack)){
					quotaMap.put(skuIDBatchPack, (quotaMap.get(skuIDBatchPack) + quota.getQuantity()));
				}else{
					quotaMap.put(skuIDBatchPack, quota.getQuantity());
				}
			}
		}
		return quotaMap;
	}

	/**
	 * 计算明细剩余数量
	 * @param bill
	 * @param details 待计算的明细列表
	 * @return
	 */
	private Map<String,Integer> calcSkuRemainQuantity(GoodsInBill bill){
		Map<String,Integer> quantityMap = new HashMap<String, Integer>();
		List<GoodsInBillDetail> details = bill.getGoodsInBillDetails();
		boolean isReceived = this.isReceived(bill,details.get(0).getBoxNo());
		if(isReceived){
			Map<String,Integer> quotaMap = getReplenishQuota(bill.getGoodsInBillID());
			for(GoodsInBillDetail detail : details){
				String skuIDBatchPack = String.format("%s_%s_%s", detail.getSkuID(),detail.getBatchNumber(),detail.getPackID());
				quantityMap.put(skuIDBatchPack, quotaMap.get(skuIDBatchPack));
			}
		}else{
			for(GoodsInBillDetail detail : details){
				Integer remainQuantity = Math.max(0, NumberUtils.intValue(detail.getQuantity()) - NumberUtils.intValue(detail.getReceivedQuantity()));
				String skuIDBatchPack = String.format("%s_%s_%s", detail.getSkuID(),detail.getBatchNumber(),detail.getPackID());
				quantityMap.put(skuIDBatchPack, remainQuantity);
			}
		}
		return quantityMap;
	}

	/**
	 * 对上架单明细进行加锁处理
	 * @param goodsInBillDetailID
	 * @param shelveSku
	 * @return
	 */
	public void doSkuShelveUpdateBillInfo(GoodsInBill bill,List<GoodsInBillDetail> billDetails,String boxNo,String operateUser){
		logger.debug("doSkuShelveUpdateBillInfo()");
		if(!this.isReceived(bill,boxNo)){
			if(StringUtils.equalsIgnoreCase(GoodsInBillState.WAITING_RECEIVED.name(), bill.getState())){
				this.serviceBeanFactory.getGoodsInBillDas().updateState(bill.getGoodsInBillID(), GoodsInBillState.SHELVEING_DOING, operateUser);
			}
			//此次更新数量的明细
			Map<String,Integer> updatedQuantityMap = new HashMap<String, Integer>();
			if(billDetails != null && !billDetails.isEmpty()){
				this.serviceBeanFactory.getGoodsInBillDas().updateReceivedQuantityInBatch(billDetails);
				for(GoodsInBillDetail detail : billDetails){
					updatedQuantityMap.put(detail.getGoodsInBillDetailID(), detail.getReceivedQuantity());
				}
			}
			List<GoodsInBillDetail> allDetails = bill.getGoodsInBillDetails();
			boolean boxDone = true;
			for(GoodsInBillDetail detail : allDetails){
				Integer updateQuantity = updatedQuantityMap.get(detail.getGoodsInBillDetailID());
				if(!StringUtils.equalsIgnoreCase(detail.getBoxNo(), boxNo)){
					continue;
				}
				if(NumberUtils.intValue(detail.getReceivedQuantity()) + NumberUtils.intValue(updateQuantity) < NumberUtils.intValue(detail.getQuantity())){
					boxDone = false;
					break;
				}
			}
			if(boxDone){
				this.serviceBeanFactory.getGoodsInBillDas().updateBoxDone(bill.getGoodsInBillID(), boxNo, boxDone, operateUser);
			}
		}
		logger.debug("doSkuShelveUpdateBillInfo() done");
	}
	
	public void updateBoxInfo(String boxNo,Integer customerID,String operator,List<GoodsInBillDetail> updateQuantityDetails){
		Box box = this.serviceBeanFactory.getBoxDas().selectByNumberAndCustomerWithDetails(boxNo, customerID);
		if(box != null && box.getDetails() != null && !box.getDetails().isEmpty()){
			List<BoxDetail> updateBoxDetails = new ArrayList<BoxDetail>();
			Map<String,GoodsInBillDetail> goodsInBillDetailMap = new HashMap<String, GoodsInBillDetail>();
			if(!updateQuantityDetails.isEmpty()){
				for(GoodsInBillDetail detail : updateQuantityDetails){
					goodsInBillDetailMap.put(detail.getGoodsInBillDetailID(), detail);
				}
			}
			for(BoxDetail detail : box.getDetails()){
				if(goodsInBillDetailMap.containsKey(detail.getSourceBillDetailID())){
					BoxDetail newBoxDetail = new BoxDetail();
					newBoxDetail.setBoxID(detail.getBoxID());
					newBoxDetail.setBoxDetailID(detail.getBoxDetailID());
					newBoxDetail.setQuantity(goodsInBillDetailMap.get(detail.getSourceBillDetailID()).getReceivedQuantity());
					updateBoxDetails.add(newBoxDetail);
				}
			}
			if(!updateBoxDetails.isEmpty()){
				boxBusinessProcessorImpl.doSkuShelveUpdateBoxInfo(box, updateBoxDetails, operator);
			}
		}
	}
}
