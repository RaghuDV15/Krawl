package com.krawl.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public abstract class FilterImpl implements Filter {
	private @Autowired @Getter @Setter Filter filter;
}
