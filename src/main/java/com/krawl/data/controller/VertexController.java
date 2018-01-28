package com.krawl.data.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.krawl.data.Graph;
import com.krawl.data.model.Vertex;
import com.krawl.util.KrawlUtil;

import lombok.Getter;
import lombok.Setter;

@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VertexController extends Vertex {

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private @Autowired @Setter @Getter Vertex vertex;

	/**
	 * Creates the simple vertex.
	 *
	 * @param u
	 *            the u
	 * @return the vertex
	 * @throws Exception
	 *             the exception
	 */
	public Vertex createSimpleVertex(URL u) throws Exception {
		vertex = Vertex.VertexBuilder().id(KrawlUtil.getTinyFileName()).rank(0).url(new URL(u.toString()))
				.krawlDate(new ArrayList<Date>()).next(new HashSet<URL>()).previous(new HashSet<URL>()).build();
		vertex.getKrawlDate().add(new Date());
		Graph.addVertex(vertex);
		LOGGER.info("New Vertex added:" + vertex);
		return vertex;
	}
}
