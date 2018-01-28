/*
 * 
 */
package com.krawl.data.model;

import java.io.File;
import java.util.Map;

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
@Builder(builderMethodName = "StatsBuilder")
@NoArgsConstructor
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Stats {

	private @Getter @Setter long processId;
	private @Getter @Setter String hostName;
	private @Getter @Setter String hostAddress;
	private @Getter @Setter String port;
	private @Getter @Setter String OSName;
	private @Getter @Setter String OSArch;
	private @Getter @Setter String OSType;
	private @Getter @Setter String OSVersion;
	private @Getter @Setter int cores;
	private @Getter @Setter long freeMemory;
	private @Getter @Setter long maxMemory;
	private @Getter @Setter long totalMemory;
	private @Getter @Setter String MACAddr;
	private @Getter @Setter File[] roots;
	private @Getter @Setter Map<String, String> envVariables;
}
