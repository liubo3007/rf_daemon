package com.kc.walle.station.engine.daemon.replenish.dto;

public class ScanBillOrBoxRequestDto {
	private String billOrBox;
	private Integer customerID;
	private String type;
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
	@Override
	public String toString() {
		return "ScanBillOrBoxRequestDto [billOrBox=" + billOrBox
				+ ", customerID=" + customerID + ", type=" + type + "]";
	}
	
}
