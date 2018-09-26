package com.kc.walle.station.engine.daemon.replenish.dto;

import java.util.List;

public class DispatchBucketRequestDto {
	private Integer customerid;
	private String offlineStationCode;
	private List<Integer> bucketType;
	private List<Integer> bucketSlotType;
	private Float minRate;
	private Float maxRate;
	private String username;
	public Integer getCustomerid() {
		return customerid;
	}
	public void setCustomerid(Integer customerid) {
		this.customerid = customerid;
	}
	public String getOfflineStationCode() {
		return offlineStationCode;
	}
	public void setOfflineStationCode(String offlineStationCode) {
		this.offlineStationCode = offlineStationCode;
	}
	public List<Integer> getBucketType() {
		return bucketType;
	}
	public void setBucketType(List<Integer> bucketType) {
		this.bucketType = bucketType;
	}
	public List<Integer> getBucketSlotType() {
		return bucketSlotType;
	}
	public void setBucketSlotType(List<Integer> bucketSlotType) {
		this.bucketSlotType = bucketSlotType;
	}
	
	public Float getMinRate() {
		return minRate;
	}
	public void setMinRate(Float minRate) {
		this.minRate = minRate;
	}
	public Float getMaxRate() {
		return maxRate;
	}
	public void setMaxRate(Float maxRate) {
		this.maxRate = maxRate;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "DispatchBucketRequestDto [customerid=" + customerid
				+ ", offlineStationCode=" + offlineStationCode
				+ ", bucketType=" + bucketType + ", bucketSlotType="
				+ bucketSlotType + ", minRate=" + minRate + ", maxRate="
				+ maxRate + ", username=" + username + "]";
	}
	
	
}
