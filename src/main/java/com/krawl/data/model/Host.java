/*
 * 
 */
package com.krawl.data.model;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
@Builder(builderMethodName = "HostBuilder")
@NoArgsConstructor
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Host {

	/** The Krawl comparator. */
	public static Comparator<Host> KrawlComparator = new Comparator<Host>() {
		@Override
		public int compare(Host h1, Host h2) {
			return h1.getNextPing().compareTo(h2.getNextPing());
		}
	};
	private @Getter @Setter String name;
	private @Getter @Setter long latency;
	private @Getter @Setter int status;
	private @Getter @Setter Date lastPinged;
	private @Getter @Setter Date nextPing;
	private @Getter @Setter List<String> userAgent;
	private @Getter @Setter List<String> allow;

	private @Getter @Setter List<String> disallow;

	private @Getter @Setter List<String> sitemap;

	/**
	 * Adds the allow.
	 *
	 * @param value
	 *            the value
	 */
	public void addAllow(String value) {
		this.allow.add(value);
	}

	/**
	 * Adds the disallow.
	 *
	 * @param value
	 *            the value
	 */
	public void addDisallow(String value) {
		this.disallow.add(value);
	}

	/**
	 * Adds the site map.
	 *
	 * @param value
	 *            the value
	 */
	public void addSiteMap(String value) {
		this.sitemap.add(value);
	}

	/**
	 * Adds the user agent.
	 *
	 * @param value
	 *            the value
	 */
	public void addUserAgent(String value) {
		this.userAgent.add(value);
	}
}
