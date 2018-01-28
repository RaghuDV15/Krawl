package com.krawl.config;

public abstract interface Configuration {

	int getBackQueues();

	String getExtension();

	int getFilterThreads();

	int getFrontQueues();

	String getIndexPath();

	int getKrawlThreads();

	int getMaxThreads();

	int getMinThreads();

	int getServerPort();

	int getTimeOutMillis();

}
