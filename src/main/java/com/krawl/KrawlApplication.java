/*
 * 
 */
package com.krawl;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.krawl.config.KrawlConfig;
import com.krawl.engine.QueueBridge;
import com.krawl.engine.analyzers.DuplicateAnalyzer;
import com.krawl.engine.analyzers.LoopAnalyzer;
import com.krawl.filter.KrawlFilter;
import com.krawl.krawl.Krawl;
import com.krawl.krawl.KrawlUrl;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class KrawlApplication.
 */
@Configuration
@SpringBootApplication
@PropertySource("classpath:application.properties")
@ComponentScan("com.krawl")
public class KrawlApplication implements CommandLineRunner {

	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static @Getter @Setter KrawlConfig krawlConfig;
	public static @Getter @Setter QueueBridge engine;
	public static @Getter @Setter KrawlFilter krawlFilter;
	public static @Getter @Setter Krawl krawl;
	public static @Getter @Setter DuplicateAnalyzer duplicateAnalyzer;
	public static @Getter @Setter LoopAnalyzer loopAnalyzer;

	public static void main(String[] args) throws Exception {
		try (AnnotationConfigApplicationContext appctx = new AnnotationConfigApplicationContext(
				KrawlApplication.class)) {
			krawlConfig = appctx.getBean(KrawlConfig.class);
			duplicateAnalyzer = appctx.getBean(DuplicateAnalyzer.class);
			loopAnalyzer = appctx.getBean(LoopAnalyzer.class);
			engine = appctx.getBean(QueueBridge.class);
			krawlFilter = appctx.getBean(KrawlFilter.class);
			krawl = appctx.getBean(Krawl.class);
		}
		SpringApplication.run(KrawlApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		engine.createQueues();
		KrawlUrl.addHostFromSeedList();
		new Thread(krawl).start();
		new Thread(krawlFilter).start();
		new Thread(engine).start();
		new Thread(duplicateAnalyzer).start();
		new Thread(loopAnalyzer).start();
	}
}
