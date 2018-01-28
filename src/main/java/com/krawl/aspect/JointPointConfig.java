package com.krawl.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class JointPointConfig {

	@Pointcut("@annotation(com.krawl.aspect.TrackTime)")
	public void trackTimeAnnotation() {
	}
}
