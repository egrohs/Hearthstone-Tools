package hcs.model;

import lombok.Data;

/**
 * Entidade abstrada, pode ser carta, mecanica, etc.
 * 
 * @author 99689650068
 *
 */
@Data
public class Entity implements Comparable<Entity> {
	protected String name, id;

//	@Override
//	public String toString() {
//		return name;
//	}

	@Override
	public int compareTo(Entity o) {
		return this.name.compareTo(((Entity) o).name);
	}
}