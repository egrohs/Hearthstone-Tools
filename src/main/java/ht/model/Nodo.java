package ht.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@NodeEntity
public class Nodo {
	public Nodo(String string) {
		this.name = string;
	}

	@Id
	@GeneratedValue
	Long id;
	@JsonProperty("name")
	String name;
}
