package com.krawl.engine.analyzers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@NoArgsConstructor
public abstract class AnalyzerImpl implements Analyzer {
	private @Autowired @Setter @Getter Analyzer analyzer;
}
