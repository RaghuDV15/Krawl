/*
 * 
 */
package com.krawl.data.model;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Component
@Builder(builderMethodName = "VertexBuilder")
@NoArgsConstructor
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Vertex {

	private @Getter @Setter String id;
	private @Setter @Getter URL url;
	private @Getter @Setter int rank;
	private @Getter @Setter List<Date> krawlDate;
	private @Getter @Setter Date nextKrawlDate;
	private @Setter @Getter Set<URL> next;
	private @Setter @Getter Set<URL> previous;

	/**
	 * Adds the next.
	 *
	 * @param url
	 *            the vertex
	 */
	public void addNext(URL url) {
		this.next.add(url);
		updateRank(this.getRank());
	}

	/**
	 * Adds the previous.
	 *
	 * @param vertex
	 *            the vertex
	 */
	public void addPrevious(URL url) {
		this.previous.add(url);
		updateRank((-1) * this.getRank());
	}

	/**
	 * Removes the next.
	 *
	 * @param vertex
	 *            the vertex
	 */
	public void removeNext(URL url) {
		this.next.remove(url);
		updateRank((-1) * this.getRank());
	}

	/**
	 * Removes the previous.
	 *
	 * @param vertex
	 *            the vertex
	 */
	public void removePrevious(URL url) {
		this.previous.remove(url);
		updateRank(this.getRank());
	}

	/**
	 * Update rank.
	 *
	 * @param update
	 *            the update
	 */
	public void updateRank(int update) {
		this.rank += update;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
}
