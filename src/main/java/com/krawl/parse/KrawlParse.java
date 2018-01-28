/*
 * 
 */
package com.krawl.parse;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krawl.data.GraphController;

import lombok.Getter;
import lombok.Setter;

public class KrawlParse {
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static @Getter @Setter BlockingQueue<URL> ParsedPool = new LinkedBlockingQueue<>();

	/**
	 * Parses the.
	 *
	 * @param in
	 *            the in
	 * @param url
	 *            the url
	 * @throws Exception
	 *             the exception
	 */
	public void parse(InputStream in, URL url) throws Exception {
		List<URL> urlList = new CopyOnWriteArrayList<>();
		@SuppressWarnings("unused")
		long timediff = System.nanoTime();
		try {
			Elements links = Jsoup.parse(in, "utf-8", "").select("a[href]:not([href~=(?i)\\.tel])");
			for (Element link : links) {
				try {
					urlList.add(new URL(new URL(url.toString()), link.attr("href")));
				} catch (MalformedURLException e) {
					LOGGER.error(e.toString(), e);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}
		new GraphController().create(url, urlList);
		Set<URL> urlSet = new CopyOnWriteArraySet<>(urlList);
		ParsedPool.addAll(urlSet);
	}
}