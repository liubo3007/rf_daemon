package com.kc.walle.station.engine.daemon.replenish.dto;

public class ShelveSkuDto {
	private String skuid;
	private String skuNumber;
	private String skuBatch;
	private String batchCode;
	private Integer skuQuantity;
	public String getSkuid() {
		return skuid;
	}
	public void setSkuid(String skuid) {
		this.skuid = skuid;
	}
	public String getSkuNumber() {
		return skuNumber;
	}
	public void setSkuNumber(String skuNumber) {
		this.skuNumber = skuNumber;
	}
	public String getSkuBatch() {
		return skuBatch;
	}
	public void setSkuBatch(String skuBatch) {
		this.skuBatch = skuBatch;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public Integer getSkuQuantity() {
		return skuQuantity;
	}
	public void setSkuQuantity(Integer skuQuantity) {
		this.skuQuantity = skuQuantity;
	}
	
}
