package com.kc.walle.station.engine.daemon.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.kc.walle.station.engine.daemon.common.Result;
import com.kc.walle.station.engine.daemon.common.dto.LoginRequestDto;
import com.kc.walle.station.engine.daemon.common.service.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {
	private final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private IUserService userService;
	
	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	@ResponseBody
	public Result<String> login(@RequestBody LoginRequestDto loginRequest) {
		logger.debug("login()");
		Result<String> result = userService.login(loginRequest);
		logger.debug("login() done");
		return result;
	}
}