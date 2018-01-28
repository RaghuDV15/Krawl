/*
 * 
 */
package com.krawl.engine;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.krawl.data.Graph;
import com.krawl.data.model.Vertex;
import com.krawl.filter.KrawlFilter;
import com.krawl.util.KrawlUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class BackQueue.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BackQueue implements Runnable {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Gets the index of host in back queue.
	 *
	 * @param name
	 *            the name
	 * @return the index of host in back queue
	 * @throws Exception
	 *             the exception
	 */
	public int getIndexOfHostInBackQueue(String name) throws Exception {
		int index = -1;
		OptionalInt o = IntStream.range(0, QueueBridge.getBackQueueHostList().size())
				.filter(i -> QueueBridge.getBackQueueHostList().get(i).contains(name)).findFirst();
		if (o.isPresent())
			index = o.getAsInt();
		return index;
	}

	@Override
	public void run() {
		LOGGER.info("New Back Pool Thread Started");
		Stream.generate(() -> {
			try {
				return (URL) KrawlFilter.getUrlFilterPool().take();
			} catch (InterruptedException ie) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					LOGGER.error(e.toString(), e);
				}
				return "Interrupted!";
			}
		}).forEach((u) -> {
			if (u instanceof URL) {
				try {
					Vertex v = Graph.getVertex((URL) u);
					new KrawlUtil().writeToJson(v);
					QueueBridge.getFrontQueuePool().get(new Random().nextInt(QueueBridge.getFrontQueuePool().size()))
							.add((URL) u);
				} catch (Exception e) {
					LOGGER.error(e.toString(), e);
				}
			}
		});
	}

}
