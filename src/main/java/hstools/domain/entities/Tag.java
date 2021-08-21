package hstools.domain.entities;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 * Graph Node Keyword derived from card text and specs.
 * @author EGrohs
 *
 */
@Data
//@NodeEntity
//@EqualsAndHashCode(callSuper=true)
public class Tag extends Node {
	static Long idc = 0L;
	private String regex, description, expr;
	//@Relationship
	private Set<Tag> tags = new HashSet<Tag>();
	public Tag() {}
	public Tag(String name, String regex, String expr, String description) {
		id=idc++;
		this.name = name;
		this.regex = regex;
		this.expr = expr;
		this.description = description;
	}
	
	public Tag(String name, String regex) {
		id=idc++;
		this.name = name;
		this.regex = regex;
	}

	public Tag geyByName(String name) {
		for (Tag tag : tags) {
			if (name.equals(tag.getName())) {
				return tag;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return name;
	}
}