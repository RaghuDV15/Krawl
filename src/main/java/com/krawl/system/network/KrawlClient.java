/*
 * 
 */
package com.krawl.system.network;

import java.lang.invoke.MethodHandles;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krawl.data.model.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class KrawlClient.
 */
public class KrawlClient {
	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Adds the topic to node.
	 */
	public static void addTopicToNode() {

	}

	/**
	 * Delete topic from node.
	 */
	public static void deleteTopicFromNode() {

	}

	/**
	 * Gets the node details.
	 *
	 * @return the node details
	 */
	public static Node getNodeDetails() {
		return null;
	}

	/**
	 * Gets the parent node.
	 *
	 * @return the parent node
	 */
	public static Node getParentNode() {
		return null;
	}

	/**
	 * Submit url to node.
	 *
	 * @param url
	 *            the url
	 */
	public static void submitUrlToNode(URL url) {

	}

	public static boolean topicExists(URL url) {
		LOGGER.warn("Method Not implemented");
		return false;
	}
}
