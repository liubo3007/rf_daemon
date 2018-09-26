package com.kc.walle.station.engine.daemon.replenish.service;

import com.kc.walle.common.domain.erp.Sku;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.replenish.dto.ShelveRequestDto;
import com.kc.walle.station.engine.daemon.replenish.vo.ScanedSkuRenderVo;

public interface IBusinessProcessor {
	/**
	 * ��������ǰ�˵�ɨ�����Ʒ������Ⱦ��Ϣ
	 * @return
	 */
	public Result<ScanedSkuRenderVo> createScanedSkuRenderInfo(String billNumber,Integer customerID,Sku sku);
	
	/**
	 * У��ɨ��ĵ��ݻ������
	 * @param billOrBox
	 * @param customerID
	 * @return
	 */
	public String checkScanBillOrBox(String billOrBox, Integer customerID);
	
	/**
	 * �ϼܲ���
	 * @param shelveRequest
	 * @return
	 */
	public String doShelve(ShelveRequestDto shelveRequest);
}
