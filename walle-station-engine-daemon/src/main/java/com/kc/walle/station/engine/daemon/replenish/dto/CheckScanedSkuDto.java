package com.kc.walle.station.engine.daemon.replenish.dto;

import java.util.List;

import com.kc.walle.common.domain.erp.BoxDetail;
import com.kc.walle.common.domain.erp.GoodsInBill;
import com.kc.walle.common.domain.erp.GoodsInBillDetail;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuRenderVo;

public class CheckScanedSkuDto {
	private String errorMsg;
	private GoodsInBill bill;
	private List<GoodsInBillDetail> matchedBillDetails;
	private List<BoxDetail> matchBoxDetails;
	private ScanedSkuRenderVo scanedSkuVo;
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public GoodsInBill getBill() {
		return bill;
	}
	public void setBill(GoodsInBill bill) {
		this.bill = bill;
	}
	public List<GoodsInBillDetail> getMatchedBillDetails() {
		return matchedBillDetails;
	}
	public void setMatchedBillDetails(List<GoodsInBillDetail> matchedBillDetails) {
		this.matchedBillDetails = matchedBillDetails;
	}
	public List<BoxDetail> getMatchBoxDetails() {
		return matchBoxDetails;
	}
	public void setMatchBoxDetails(List<BoxDetail> matchBoxDetails) {
		this.matchBoxDetails = matchBoxDetails;
	}
	public ScanedSkuRenderVo getScanedSkuVo() {
		return scanedSkuVo;
	}
	public void setScanedSkuVo(ScanedSkuRenderVo scanedSkuVo) {
		this.scanedSkuVo = scanedSkuVo;
	}
	
	
}
