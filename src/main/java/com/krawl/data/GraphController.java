/*
 * 
 */
package com.krawl.data;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.krawl.data.controller.VertexController;
import com.krawl.data.model.Vertex;

@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GraphController extends Graph {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Creates the.
	 *
	 * @param u
	 *            the u
	 * @param urls
	 *            the urls
	 * @throws Exception
	 *             the exception
	 */
	public void create(URL u, List<URL> urls) throws Exception {
		try {
			Vertex vertex = new VertexController().createSimpleVertex(u);
			urls.stream().map(l -> {
				try {
					return getVertex(l);
				} catch (Exception e) {
					LOGGER.error(e.toString(), e);
				}
				return null;
			}).forEach(v -> {
				if (v != null) {
					v.addPrevious(vertex.getUrl());
					Graph.addVertex(v);
					vertex.addNext(v.getUrl());
				} else
					LOGGER.error(" Null Vertex");
			});
			Graph.addVertex(vertex);
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}
	}
}