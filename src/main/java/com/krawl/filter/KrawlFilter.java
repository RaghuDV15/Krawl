/*
 * Krawl Filter implements
 * -> Filtering based on Host disallowed links confining to robots.txt
 * -> Visted Links are not crawled again
 */
package com.krawl.filter;

import static com.krawl.KrawlApplication.krawlConfig;

import java.io.FileInputStream;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krawl.data.controller.HostController;
import com.krawl.data.controller.TopicController;
import com.krawl.data.model.Vertex;
import com.krawl.parse.KrawlParse;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class KrawlFilter implements Filter, Runnable {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static @Getter @Setter BlockingQueue<URL> UrlFilterPool = new LinkedBlockingQueue<>();
	private static @Getter @Setter CopyOnWriteArraySet<String> urlList = new CopyOnWriteArraySet<String>();

	/**
	 * Checks if is visited.
	 *
	 * @param u
	 *            the u
	 * @return true, if is visited
	 * @throws Exception
	 *             the exception
	 */
	public boolean isVisited(URL u) throws Exception {
		@SuppressWarnings("unused")
		long timediff = System.nanoTime();
		ObjectMapper objectMapper = new ObjectMapper();
		if (urlList.stream().anyMatch(s -> {
			try {
				return new URL(s).sameFile(u);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			return false;
		}))
			;
		try {
			List<String> files = Files
					.walk(Paths.get(krawlConfig.getIndexPath() + ((URL) u).getHost() + "/" + ((URL) u).getPath()), 1)
					.filter(f -> f.toString().endsWith(krawlConfig.getExtension())).map(f -> f.toString())
					.collect(Collectors.toList());
			for (String s : files) {
				try {
					if (objectMapper.readValue((new FileInputStream(s)), Vertex.class).getUrl().sameFile(u))
						return true;
				} catch (Exception e) {
					LOGGER.error(e.toString(), e);
				}
			}
		} catch (NoSuchFileException e) {
			return false;
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ExecutorService FilterService = Executors.newFixedThreadPool(krawlConfig.getFilterThreads());
		LOGGER.info("New Filter Thread Started");
		Stream.generate(() -> {
			try {
				return (URL) KrawlParse.getParsedPool().take();
			} catch (InterruptedException ie) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					LOGGER.warn(e.toString(), e);
				}
				return "Interrupted!";
			}
		}).forEach((url) -> {
			if (url instanceof URL) {

				FilterService.submit(new Runnable() {
					@Override
					public void run() {
						try {
							if (!HostController.isDisallowed((URL) url)) {
								LOGGER.info("Checking if disallowed URL: " + url);
								if (!isVisited((URL) url)) {
									LOGGER.info("Checking if visited URL: " + url);
									if (TopicController.isTopicValid((URL) url)) {
										KrawlFilter.getUrlFilterPool().add((URL) url);
									} else
										LOGGER.info("URL doesn't belong to the list of topics: " + url);
								} else
									LOGGER.info("URL is already visited: " + url);
							} else
								LOGGER.info("URL is disallowed: " + url);
						} catch (Exception e) {
							LOGGER.error(e.toString(), e);
						}
					}
				});
			}
		});
	}

	/**
	 * Url to file.
	 *
	 * @param url
	 *            the url
	 * @throws Exception
	 *             the exception
	 */
	public void urlToFile(String url) throws Exception {
		Path file = Paths.get(krawlConfig.getIndexPath() + "/Urls.list");
		urlList.add(url);
		if (urlList.size() > 10000) {
			Files.write(file, urlList, StandardOpenOption.APPEND);
			urlList.clear();
		}
	}
}
