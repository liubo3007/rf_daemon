package com.kc.walle.station.engine.daemon.common.service;

import java.util.List;

import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.vo.CustomerVo;

public interface ICustomerService {
	/**
	 * 查询客户列表
	 * @return
	 */
	Result<List<CustomerVo>> listCustomer();
}
