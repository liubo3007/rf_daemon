package com.kc.walle.station.engine.daemon.common.service;

import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.dto.LoginRequestDto;

public interface IUserService {
	/**
	 * ��¼
	 * @param loginRequest
	 * @return
	 */
	Result<String> login(LoginRequestDto loginRequest);
}
