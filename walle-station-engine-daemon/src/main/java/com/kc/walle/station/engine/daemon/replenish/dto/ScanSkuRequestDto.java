package com.kc.walle.station.engine.daemon.replenish.dto;

public class ScanSkuRequestDto {
	private String billOrBox;
	private Integer customerID;
	private String type;
	private String skuNumber;
	public String getBillOrBox() {
		return billOrBox;
	}
	public void setBillOrBox(String billOrBox) {
		this.billOrBox = billOrBox;
	}
	public Integer getCustomerID() {
		return customerID;
	}
	public void setCustomerID(Integer customerID) {
		this.customerID = customerID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSkuNumber() {
		return skuNumber;
	}
	public void setSkuNumber(String skuNumber) {
		this.skuNumber = skuNumber;
	}
	@Override
	public String toString() {
		return "ScanSkuRequestDto [billOrBox=" + billOrBox + ", customerID="
				+ customerID + ", type=" + type + ", skuNumber=" + skuNumber
				+ "]";
	}
	
	
}
