/*
 * 
 */
package com.krawl.krawl;

import static com.krawl.KrawlApplication.krawlConfig;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.krawl.aspect.TrackTime;
import com.krawl.data.controller.HostController;
import com.krawl.data.controller.TopicController;
import com.krawl.data.controller.VertexController;
import com.krawl.data.model.Host;
import com.krawl.engine.BackQueue;
import com.krawl.engine.QueueBridge;
import com.krawl.filter.KrawlFilter;
import com.krawl.parse.KrawlParse;

@Component
public class KrawlUrl implements Krawl {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Adds the host from seed list.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@TrackTime
	public static void addHostFromSeedList() throws Exception {
		try {
			int number = 0;
			for (String s : krawlConfig.getSeedList()) {
				Host host = new Host();
				URL url = new URL(s);
				new VertexController().createSimpleVertex(url);
				host = new HostController().createHost(url);
				new TopicController().createTopic(url);
				HostController.getHosts().add(host);
				HostController.getHostToKrawl().add(host);
				QueueBridge.getBackQueuePool().get(number).add(url);
				QueueBridge.getBackQueueHostList().get(number).add(url.getHost());
				number++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		ExecutorService KrawlService = Executors.newFixedThreadPool(krawlConfig.getKrawlThreads());
		LOGGER.info("New Krawl Thread Started");
		/*
		 * Get Urls to Krawl from the BackQueuePool based on Host Latency Host Queue
		 * acts as a minheap which return me the next requestable host based on
		 * politeness, Get the index of BackQueue Pool, where the next Host queryable
		 * link exists and krawl the link
		 * 
		 */
		Stream.generate(() -> {
			try {
				Host host = HostController.getHostToKrawl().poll();
				int index = -1;
				if (host != null && host.getName() != "") {
					try {
						index = new BackQueue().getIndexOfHostInBackQueue(host.getName());
					} catch (Exception e) {
					}
					if (index != -1) {
						host = new HostController().updateHostNextPing(host);
						HostController.getHosts().add(host);
						return (URL) QueueBridge.getBackQueuePool().get(index).take();
					} else
						return null;
				} else
					return null;
			} catch (InterruptedException ie) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					LOGGER.error(e.toString(), e);
				}
				return null;
			}
		}).forEach(u -> {
			if (u instanceof URL) {
				try {
					KrawlService.submit(new Runnable() {
						@Override
						public void run() {
							long timediff = System.nanoTime();
							try {
								new KrawlParse().parse(((URL) u).openStream(), (URL) u);
								timediff = System.nanoTime() - timediff;
								LOGGER.info("Crawled: " + u);
								System.out.println("Crawl Time: " + timediff / 1000000.0 + "ms Crawled: " + u);
								Thread.sleep(100000);
								new KrawlFilter().urlToFile(((URL) u).toString());
							} catch (Exception e) {
								LOGGER.info(e.toString(), e);
							}
						}
					});
				} catch (Exception e) {
					LOGGER.info(e.toString(), e); // Response Code Error from Http Connection
				}
			}
		});
	}
}