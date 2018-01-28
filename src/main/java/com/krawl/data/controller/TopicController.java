package com.krawl.data.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.krawl.data.model.Topic;
import com.krawl.system.network.KrawlClient;
import com.krawl.util.KrawlUtil;

import lombok.Getter;
import lombok.Setter;

@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TopicController {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static @Getter @Setter CopyOnWriteArrayList<Topic> Topics = new CopyOnWriteArrayList<Topic>();
	private @Autowired @Setter @Getter Topic topic;

	public Topic createTopic(URL url) throws Exception {
		if (!KrawlClient.topicExists(url)) {
			topic = Topic.TopicBuilder().id(url.getHost()).related(getRelatedTopics(url.getHost())).build();
			Topics.add(topic);
			LOGGER.info("New Topic added:" + topic);
		} else {
			LOGGER.info("Topic already exists in other Node:" + topic);
		}
		return null;
	}

	public Set<String> getRelatedTopics(String id) {
		return Topics.stream().filter(t -> 0.5 < KrawlUtil.getJaroWinklerDistance(id, t.getId())).map(Topic::getId)
				.collect(Collectors.toSet());
	}

	public static boolean isTopicValid(URL url) {
		return Topics.stream().filter(t -> t.getId().equals(url.getHost())).findFirst().isPresent();
	}
}
