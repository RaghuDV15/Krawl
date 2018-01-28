/*
 * 
 */
package com.krawl.data.model;

import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.krawl.data.NodeStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Component
@Builder(builderMethodName = "NodeBuilder")
@NoArgsConstructor
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Node {
	private @Getter @Setter String id;
	private @Getter @Setter String parent;
	private @Getter @Setter List<String> child;
	private @Getter @Setter NodeStatus status;
	private @Getter @Setter Stats stats;
	private @Getter @Setter List<Topic> topics;
}
