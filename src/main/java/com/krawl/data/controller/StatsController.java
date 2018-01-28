/*
 * 
 */
package com.krawl.data.controller;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.krawl.data.model.Stats;

import lombok.Getter;
import lombok.Setter;

@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class StatsController extends Stats {

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static @Autowired @Getter @Setter Stats stats;

	/**
	 * Generate stat.
	 */
	public static void generateStat() {
		// nodeStats.setProcessId(ProcessHandle.current().getPid());
		stats.setOSName(System.getProperty("os.name"));
		stats.setOSVersion(System.getProperty("os.version"));
		stats.setOSType(System.getProperty("os.arch"));
		try {
			stats.setHostName(InetAddress.getLocalHost().getHostName());
			stats.setHostName(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		stats.setCores(Runtime.getRuntime().availableProcessors());
		stats.setMaxMemory(Runtime.getRuntime().maxMemory());
		stats.setFreeMemory(Runtime.getRuntime().freeMemory());
		stats.setTotalMemory(Runtime.getRuntime().totalMemory());
		stats.setRoots(File.listRoots());
		stats.setEnvVariables(System.getenv());
	}
}
