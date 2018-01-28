/*
 * Node Manager is responsible for 
 * -> Start, Stop and Restart with preferablly different configurations, a Krawl Node remotely 
 * -> Initialize Setup with the list of remote servers make this ready by moving required binaries, Creatin directories, Creating a Krawl User
 * -> 
 */
package com.krawl.system;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrawlNodeManager {
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void start() throws Exception {

	}

	public static void stop() throws Exception {

	}

	public static void restart() throws Exception {

	}

	public static void initHost(List<String> hosts, List<String> ports) throws Exception {
		// In all Hosts
		// Create User
		// SCP required binaries
		// Add rules to firewall for communication
	}

}
