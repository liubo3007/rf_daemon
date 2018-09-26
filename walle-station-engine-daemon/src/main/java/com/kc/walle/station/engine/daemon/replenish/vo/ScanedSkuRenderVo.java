package com.kc.walle.station.engine.daemon.replenish.vo;

import java.util.List;

public class ScanedSkuRenderVo {
	private String skuid;
	private String skuName;
	private String skuNumber;
	private List<ScanedSkuBatchRenderVo> skuBatch;
	private Integer quantity;
	public String getSkuid() {
		return skuid;
	}
	public void setSkuid(String skuid) {
		this.skuid = skuid;
	}
	public String getSkuName() {
		return skuName;
	}
	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}
	public String getSkuNumber() {
		return skuNumber;
	}
	public void setSkuNumber(String skuNumber) {
		this.skuNumber = skuNumber;
	}
	public List<ScanedSkuBatchRenderVo> getSkuBatch() {
		return skuBatch;
	}
	public void setSkuBatch(List<ScanedSkuBatchRenderVo> skuBatch) {
		this.skuBatch = skuBatch;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
}
