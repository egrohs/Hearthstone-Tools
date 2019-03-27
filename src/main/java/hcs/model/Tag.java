package hcs.model;

import lombok.Data;

@Data
public class Tag extends Entity {
    private String regex, tags;

    // private List<Tag> sinergias = new ArrayList<>();
    public Tag(String name, String regex, String tags) {
	this.name = name;
	this.regex = regex;
	this.tags = tags;
    }
}
