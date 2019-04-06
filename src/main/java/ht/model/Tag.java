package ht.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
//@EqualsAndHashCode(callSuper=true)
public class Tag extends Node {
	private String regex, description, expr;
	private Set<Tag> tags = new HashSet<Tag>();

	public Tag(String name, String regex, String expr, String description) {
		this.name = name;
		this.regex = regex;
		this.expr = expr;
		this.description = description;
	}

	@Override
	public String toString() {
		return name;
	}
}