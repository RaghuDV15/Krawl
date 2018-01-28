/*
 * 
 */
package com.krawl.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Component
@Primary
public class KrawlConfig implements Configuration {

	@Value("${krawl.indexPath}")
	private @Getter @Setter String indexPath;
	@Value("${krawl.extension}")
	private @Getter @Setter String extension;
	@Value("${krawl.krawlThreads}")
	private @Getter @Setter int krawlThreads;
	@Value("${krawl.poolThreads}")
	private @Getter @Setter int poolThreads;
	@Value("${krawl.backThreads}")
	private @Getter @Setter int backThreads;
	@Value("${krawl.filterThreads}")
	private @Getter @Setter int filterThreads;
	@Value("${krawl.backQueues}")
	private @Getter @Setter int backQueues;
	@Value("${krawl.frontQueues}")
	private @Getter @Setter int frontQueues;
	@Value("#{'${krawl.seedList}'.split(',')}")
	private @Getter @Setter List<String> seedList;

	// Krawl Server configs
	@Value("${krawl.host.serverPort}")
	private @Getter @Setter int serverPort;
	@Value("${krawl.host.maxThreads}")
	private @Getter @Setter int maxThreads;
	@Value("${krawl.host.minThreads}")
	private @Getter @Setter int minThreads;
	@Value("${krawl.host.timeOutMillis}")
	private @Getter @Setter int timeOutMillis;

	@Value("${krawl.host.hostQueueSize}")
	private @Getter @Setter int hostQueueSize;

	@Value("#{'${krawl.node.hosts}'.split(',')}")
	private @Getter @Setter List<String> hosts;
	@Value("#{'${krawl.node.ports}'.split(',')}")
	private @Getter @Setter List<String> ports;
	@Value("${krawl.node.username}")
	private @Getter @Setter String username;
	@Value("${krawl.node.password}")
	private @Getter @Setter String password;
}