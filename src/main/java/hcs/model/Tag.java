package hcs.model;

import lombok.Data;

@Data
//@EqualsAndHashCode(callSuper=true)
public class Tag extends Entity {
	private String regex, description, type, cost, attack, hp, sinergies;

	public Tag(String name, String regex, String type, String cost, String attack, String hp, String sinergies, String description) {
		this.name = name;
		this.regex = regex;
		this.type = type;
		this.cost = cost;
		this.attack = attack;
		this.hp = hp;
		this.sinergies = sinergies;
		this.description = description;
	}

	@Override
	public String toString() {
		return name;
	}
}