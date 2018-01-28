package com.krawl.aspect;

import java.lang.invoke.MethodHandles;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class MethodExecutionTime {
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Around("com.krawl.aspect.JointPointConfig.trackTimeAnnotation()")
	public void around(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();

		joinPoint.proceed();

		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.info("Time Taken by {} is {}", joinPoint, timeTaken);
	}
}
