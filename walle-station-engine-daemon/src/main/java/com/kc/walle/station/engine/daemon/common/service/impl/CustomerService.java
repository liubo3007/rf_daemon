package com.kc.walle.station.engine.daemon.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kc.walle.common.domain.erp.Customer;
import com.kc.walle.station.engine.daemon.common.ErrorCode;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.service.ICustomerService;
import com.kc.walle.station.engine.daemon.common.vo.CustomerVo;

@Service
public class CustomerService extends BaseService implements ICustomerService {

	@Override
	public Result<List<CustomerVo>> listCustomer() {
		logger.debug("listCustomer()");
		Result<List<CustomerVo>> result = new Result<List<CustomerVo>>();
		List<Customer> customers = serviceBeanFactory.getCustomerDas().selectAll();
		if(customers == null || customers.isEmpty()){
			result.setError(ErrorCode.CUSTOMER_EMPTY.getCode(), ErrorCode.CUSTOMER_EMPTY.getMessage());
			return result ;
		}
		List<CustomerVo> customerResult = new ArrayList<CustomerVo>();
		for(Customer customer : customers){
			CustomerVo cus = new CustomerVo();
			cus.setId(customer.getCustomerID());
			cus.setName(customer.getName());
			customerResult.add(cus);
		}
		result.setData(customerResult);
		return result;
	}
	
	
}
