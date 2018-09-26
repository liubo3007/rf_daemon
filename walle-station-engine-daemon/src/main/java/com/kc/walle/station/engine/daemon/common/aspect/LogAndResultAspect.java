package com.kc.walle.station.engine.daemon.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kc.walle.common.infrastructure.util.JacksonJsonUtil;


@Aspect
@Component
public class LogAndResultAspect {
	private Logger logger = LoggerFactory.getLogger(LogAndResultAspect.class);

    @Pointcut("execution(* com.kc.walle.station.engine.daemon.*.controller.*.*(..))")
    public void loggerController(){}

    @Around("loggerController()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable{
    	Object result = null;
    	Object target = joinPoint.getTarget();
    	String className = target.getClass().getName();
    	Signature signature = joinPoint.getSignature();
    	String methodName = signature.getName();
    	StringBuffer argsName = new StringBuffer("(");
    	Object[] args = joinPoint.getArgs();
		if (args != null) {
			for (Object object : args) {
				if (object == null) {
					continue;
				}
				// 获取参数的类型
				Class<? extends Object> clazz = object.getClass();
				String argsType = clazz.getName();
				argsName.append(argsType + " " + object + ",");
			}
			if (argsName.length() > 1) {
				argsName = new StringBuffer(argsName.substring(0,argsName.length() - 1));
			}
		}
		argsName.append(")");
		String description = className + "." + methodName + argsName.toString();
		logger.debug(String.format("请求方法:%s", description));
		try {
			result = joinPoint.proceed();
			logger.debug(String.format("方法(%s)执行的返回值:%s", description,JacksonJsonUtil.beanToJson(result)));
		} catch (Exception e) {
			logger.error("方法(%s)执行异常:",e);
			throw e;
		}
		return result;
    }
}
