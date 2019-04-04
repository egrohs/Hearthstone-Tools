package ht.model;

import lombok.Data;

@Data
//@EqualsAndHashCode(callSuper=true)
public class Tag extends Entity {
	private String regex, description, expr;

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