/*
 * 
 */
package com.krawl.data.model;

import java.util.Set;

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
@Builder(builderMethodName = "TopicBuilder")
@NoArgsConstructor
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Topic {

	private @Setter @Getter String id;
	private @Setter @Getter Set<String> related;

	public void addRelated(String topic) {
		this.related.add(topic);
	}

	public void removeRelated(String topic) {
		this.related.remove(topic);
	}
}
