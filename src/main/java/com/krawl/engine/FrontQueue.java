package com.krawl.engine;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.Random;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FrontQueue implements Runnable {
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public void run() {
		LOGGER.info("New Front Pool Thread Started");
		Stream.generate(() -> {
			try {
				return (URL) QueueBridge.getFrontQueuePool()
						.get(new Random().nextInt(QueueBridge.getFrontQueuePool().size())).take();
			} catch (InterruptedException ie) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					LOGGER.warn(e.toString(), e);
				}
				return "Interrupted!";
			}
		}).forEach((u) -> {
			if (u instanceof URL) {
				try {
					int index = -1;
					index = new BackQueue().getIndexOfHostInBackQueue(((URL) u).getHost());
					if (index != -1)
						QueueBridge.getBackQueuePool().get(index).add((URL) u);
					else
						QueueBridge.getBackQueuePool().get(new Random().nextInt(QueueBridge.getBackQueuePool().size()))
								.add((URL) u);
				} catch (Exception e) {
					LOGGER.error(e.toString(), e);
				}
			}
		});
	}

}
