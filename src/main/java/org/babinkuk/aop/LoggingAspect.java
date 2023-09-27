package org.babinkuk.aop;

import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

	private Logger log = Logger.getLogger(getClass().getName());
	
	// setup pointcut declaration
	// match any class in the package (first *), any method (second *), any number of args (..)
	@Pointcut("execution(* org.babinkuk.controller.*.*(..))")
	private void forControllerPackage() {
		
	}
	
	@Pointcut("execution(* org.babinkuk.service.*.*(..))")
	private void forServicePackage() {
		
	}
	
	@Pointcut("execution(* org.babinkuk.dao.*.*(..))")
	private void forDaoPackage() {
		
	}
	
	// combine all pointcuts above
	@Pointcut("forControllerPackage() || forServicePackage() || forDaoPackage()")
	private void forAppFlow() {
		
	}
	
	@Before("forAppFlow()")
	private void before(JoinPoint joinPoint) {
		// display method
		String method = joinPoint.getSignature().toShortString();
		log.info("-----> in @Before : " + method);
		
		// get method arguments
		Object[] args = joinPoint.getArgs();
		
		// display method arguments
		for (Object tempArg : args) {
			log.info("-----> argument : " + tempArg);
		}
	}
	
	@AfterReturning(
			pointcut = "forAppFlow()",
			returning = "result"
	)
	private void afterReturning(JoinPoint joinPoint, Object result) {
		// display method
		String method = joinPoint.getSignature().toShortString();
		log.info("-----> in @AfterReturning : " + method);
		
		// display data returned
		log.info("-----> data : " + result);
	}
}