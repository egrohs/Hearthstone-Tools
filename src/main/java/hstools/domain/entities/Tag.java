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
//@EqualsAndHashCode(callSuper=true)
public class Tag extends Node {
	private String regex, description, expr;
	private Set<Tag> tags = new HashSet<Tag>();

	public Tag(Long id, String name, String regex, String expr, String description) {
		super(id);
		this.name = name;
		this.regex = regex;
		this.expr = expr;
		this.description = description;
	}
	
	public Tag(Long id, String name, String regex) {
		super(id);
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