package com.kc.walle.station.engine.daemon.replenish.dto;

import java.util.List;

public class ShelveScanBucketSlotRequestDto {
	private List<ShelveSkuDto> skus;
	private String bucketSlot;
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
	
	
}
