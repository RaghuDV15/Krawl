/*
 * 
 */
package com.krawl.data.controller;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.krawl.data.model.Node;

import lombok.Getter;
import lombok.Setter;

@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NodeController extends Node {

	private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private @Autowired @Getter @Setter Node node;

}
