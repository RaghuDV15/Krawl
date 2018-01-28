/*
 * 
 */
package com.krawl.data.controller;

import static com.krawl.KrawlApplication.krawlConfig;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.krawl.data.model.Host;

import lombok.Getter;
import lombok.Setter;

@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class HostController extends Host {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static @Getter @Setter PriorityBlockingQueue<Host> HostToKrawl = new PriorityBlockingQueue<Host>(
			krawlConfig.getHostQueueSize(), Host.KrawlComparator);
	private static @Getter @Setter CopyOnWriteArrayList<Host> Hosts = new CopyOnWriteArrayList<Host>();
	private @Autowired @Getter @Setter Host host;

	/**
	 * Creates the host.
	 *
	 * @param url
	 *            the url
	 * @return the host
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("deprecation")
	public Host createHost(URL url) throws Exception {
		long latency = 0;
		String key = "", value = "", str = "";
		int status = 0;
		List<String> userAgent = new ArrayList<>();
		List<String> allow = new ArrayList<>();
		List<String> disallow = new ArrayList<>();
		List<String> siteMap = new ArrayList<>();
		Scanner read = null;

		try {
			URL newUrl = new URL(url.getProtocol() + "://" + url.getHost() + "/robots.txt");
			latency = System.currentTimeMillis();
			HttpURLConnection con = (HttpURLConnection) newUrl.openConnection();
			status = con.getResponseCode();
			con.disconnect();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(newUrl.openStream()));
			latency = System.currentTimeMillis() - latency;
			while ((str = dis.readLine()) != null) {
				read = new Scanner(str);
				read.useDelimiter(":");
				if (read.hasNext()) {
					key = read.next();
					if (read.hasNext())
						value = read.next();
					if (key.equalsIgnoreCase("User-agent")) {
						userAgent.add(value);
					} else if (key.equalsIgnoreCase("Allow")) {
						allow.add(value);
					} else if (key.equalsIgnoreCase("Disallow")) {
						disallow.add(value);
					} else if (key.equalsIgnoreCase("Sitemap")) {
						siteMap.add(value);
					} else {
					}
				}
			}
			read.close();
			host = Host.HostBuilder().name(newUrl.getHost()).latency(latency).allow(allow).disallow(disallow)
					.userAgent(userAgent).sitemap(siteMap).status(status).lastPinged(new Date()).build();
			host = updateHostNextPing(host);
			if (host != null) {
				Hosts.add(host);
				HostToKrawl.add(host);
				LOGGER.info("New Host Added:" + host + " Count:" + Hosts.size());
				return host;
			}
		} catch (IOException e) {
			LOGGER.info(e.toString(), e);
		} catch (Exception e) {
			LOGGER.info(e.toString(), e);
		}
		return null;
	}

	/**
	 * Update host next ping.
	 *
	 * @param host
	 *            the host
	 * @return the host
	 */
	public Host updateHostNextPing(Host host) {
		Date pingTime = new Date();
		pingTime.setTime((10 * host.getLatency()) + new Date().getTime());
		host.setNextPing(pingTime);
		return host;
	}

	/**
	 * Gets the host.
	 *
	 * @param u
	 *            the u
	 * @return the host
	 * @throws Exception
	 *             the exception
	 */
	public Host getHost(URL u) throws Exception {
		if (isPresent(u))
			return Hosts.stream().filter(h -> h.getName() == ((URL) u).getHost()).findFirst().get();
		return null;
	}

	/**
	 * Checks if is present.
	 *
	 * @param u
	 *            the u
	 * @return true, if is present
	 * @throws Exception
	 *             the exception
	 */
	public boolean isPresent(URL u) throws Exception {
		return Hosts.stream().filter(h -> h.getName() == ((URL) u).getHost()).findFirst().isPresent();
	}

	/**
	 * Checks if is disallowed.
	 *
	 * @param u
	 *            the u
	 * @return true, if is disallowed
	 * @throws Exception
	 *             the exception
	 */
	public static boolean isDisallowed(URL u) throws Exception {
		Host host = new HostController().getHost(u);
		if (host != null) {
			try {
				return host.getDisallow().stream().anyMatch(d -> u.toString().contains(d));
			} catch (java.util.NoSuchElementException e) {
				LOGGER.warn(e.toString(), e);
			}
		}
		return false;
	}

}
