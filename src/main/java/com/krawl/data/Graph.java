/*
 * 
 */
package com.krawl.data;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.krawl.data.controller.VertexController;
import com.krawl.data.model.Vertex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Component
@Builder(builderMethodName = "GraphBuilder")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Graph extends Vertex {

	private static @Getter @Setter Set<Vertex> vertices = Collections.synchronizedSet(new HashSet<Vertex>());

	/**
	 * Adds the vertex.
	 *
	 * @param vertex
	 *            the vertex
	 */
	public static void addVertex(Vertex vertex) {
		if (!vertices.contains(vertex))
			vertices.add(vertex);
		else {
			removeVertex(vertex);
			Graph.getVertices().add(vertex);
		}
	}

	/**
	 * Gets the vertex.
	 *
	 * @param u
	 *            the Url
	 * @return the vertex
	 * @throws Exception
	 *             the exception
	 */
	public static Vertex getVertex(URL u) throws Exception {
		if (isPresent(u))
			return vertices.parallelStream().filter(l -> l.getUrl().equals(u)).findAny().get();
		else
			return new VertexController().createSimpleVertex(u);
	}

	/**
	 * Checks if is present.
	 *
	 * @param u
	 *            the u
	 * @return true, if is present
	 */
	public static boolean isPresent(URL u) {
		return vertices.parallelStream().filter(l -> l.getUrl().equals(u)).findAny().isPresent();
	}

	/**
	 * Removes the vertex.
	 *
	 * @param vertex
	 *            the vertex
	 */
	public static void removeVertex(Vertex vertex) {
		vertices.remove(vertex);
	}

	private @Autowired Vertex vertex;
}
