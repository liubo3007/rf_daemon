package com.kc.walle.station.engine.daemon.common.service.impl;

import org.springframework.stereotype.Service;

import com.kc.walle.common.domain.admin.User;
import com.kc.walle.common.infrastructure.util.StringUtils;
import com.kc.walle.station.engine.daemon.common.ErrorCode;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.dto.LoginRequestDto;
import com.kc.walle.station.engine.daemon.common.service.IUserService;

@Service
public class UserService extends BaseService implements IUserService {
	
	@Override
	public Result<String> login(LoginRequestDto loginRequest) {
		logger.debug("login()");
		Result<String> result = new Result<String>();
		if(loginRequest == null){
			result.setError(ErrorCode.LOGIN_INFO_EMTPY.getCode(), ErrorCode.LOGIN_INFO_EMTPY.getMessage());
			return result ;
		}
		if(StringUtils.isBlank(loginRequest.getUsername())){
			result.setError(ErrorCode.LOGIN_ACCOUNT_EMTPY.getCode(), ErrorCode.LOGIN_ACCOUNT_EMTPY.getMessage());
			return result ;
		}
		if(StringUtils.isBlank(loginRequest.getPassword())){
			result.setError(ErrorCode.LOGIN_PASSWORD_EMTPY.getCode(), ErrorCode.LOGIN_PASSWORD_EMTPY.getMessage());
			return result ;
		}
		User user = serviceBeanFactory.getUserDas().selectOneByAccount(loginRequest.getUsername());
		if(user == null){
			result.setError(ErrorCode.LOGIN_ACCOUNT_NOT_EXIST.getCode(), String.format(ErrorCode.LOGIN_ACCOUNT_NOT_EXIST.getMessage(), loginRequest.getUsername()));
			return result ;
		}
		if(!StringUtils.equalsIgnoreCase(user.getPassword(), loginRequest.getPassword())){
			result.setError(ErrorCode.LOGIN_PASSWORD_ERROR.getCode(), ErrorCode.LOGIN_PASSWORD_ERROR.getMessage());
			return result ;
		}
		result.setData(user.getName());
		logger.debug("login() done");
		return result;
	}
	
}
