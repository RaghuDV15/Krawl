package com.krawl.krawl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public abstract class KrawImpl implements Krawl {
	private @Autowired @Getter @Setter Krawl krawl;
}
