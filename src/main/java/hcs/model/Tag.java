package hcs.model;

import lombok.Data;

@Data
//@EqualsAndHashCode(callSuper=true)
public class Tag extends Entity {
	private String regex, tags;
	// private List<Tag> sinergies = new ArrayList<Tag>();

	public Tag(String name, String regex, String tags) {
		this.name = name;
		this.regex = regex;
		this.tags = tags;
	}

	@Override
	public String toString() {
		return name;
	}
}