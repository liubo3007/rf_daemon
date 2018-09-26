package com.kc.walle.station.engine.daemon.common.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.service.ICustomerService;
import com.kc.walle.station.engine.daemon.common.vo.CustomerVo;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	private final Logger logger = LoggerFactory.getLogger(CustomerController.class);
	
	@Autowired
	private ICustomerService customerService;
	
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	public Result<List<CustomerVo>> listCustomer() {
		logger.debug("listCustomer()");
		Result<List<CustomerVo>> result = customerService.listCustomer();
		logger.debug("listCustomer() done");
		return result;
	}
}