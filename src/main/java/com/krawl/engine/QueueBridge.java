/*
 * 
 */
package com.krawl.engine;

import static com.krawl.KrawlApplication.krawlConfig;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class QueueBridge implements Engine, Runnable {

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static @Getter @Setter CopyOnWriteArrayList<List<String>> BackQueueHostList = new CopyOnWriteArrayList<List<String>>();

	private static @Getter @Setter CopyOnWriteArrayList<BlockingQueue<URL>> FrontQueuePool = new CopyOnWriteArrayList<BlockingQueue<URL>>();
	private static @Getter @Setter CopyOnWriteArrayList<BlockingQueue<URL>> BackQueuePool = new CopyOnWriteArrayList<BlockingQueue<URL>>();
	private @Autowired FrontQueue frontQueue;
	private @Autowired BackQueue backQueue;

	/**
	 * Creates the queues.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public void createQueues() throws Exception {

		IntStream.range(0, krawlConfig.getBackQueues()).forEach(i -> {
			QueueBridge.getBackQueuePool().add(new LinkedBlockingQueue<URL>());
			BackQueueHostList.add(new CopyOnWriteArrayList<String>());
		});
		IntStream.range(0, krawlConfig.getFrontQueues())
				.forEach(i -> QueueBridge.getFrontQueuePool().add(new LinkedBlockingQueue<URL>()));
	}

	@Override
	public void run() {
		new Thread(frontQueue).start();
		new Thread(backQueue).start();
	}
}
