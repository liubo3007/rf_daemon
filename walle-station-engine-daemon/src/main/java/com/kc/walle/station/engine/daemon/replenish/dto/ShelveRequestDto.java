package com.kc.walle.station.engine.daemon.replenish.dto;

import java.util.List;

public class ShelveRequestDto {
	private String offlineStation;
	private List<ShelveSkuDto> skus;
	private String bucketSlot;
	private String billOrBox;
	private Integer customerID;
	private String type;
	private String username;
	public String getOfflineStation() {
		return offlineStation;
	}
	public void setOfflineStation(String offlineStation) {
		this.offlineStation = offlineStation;
	}
	public List<ShelveSkuDto> getSkus() {
		return skus;
	}
	public void setSkus(List<ShelveSkuDto> skus) {
		this.skus = skus;
	}
	public String getBucketSlot() {
		return bucketSlot;
	}
	public void setBucketSlot(String bucketSlot) {
		this.bucketSlot = bucketSlot;
	}
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "ShelveRequestDto [offlineStation=" + offlineStation + ", skus="
				+ skus + ", bucketSlot=" + bucketSlot + ", billOrBox="
				+ billOrBox + ", customerID=" + customerID + ", type=" + type
				+ ", username=" + username + "]";
	}
	
	
}
