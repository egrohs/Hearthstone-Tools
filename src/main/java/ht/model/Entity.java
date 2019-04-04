package ht.model;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

import lombok.Data;

/**
 * Entidade abstrada, pode ser carta, mecanica, etc.
 * 
 * @author 99689650068
 *
 */
@Data
//@NodeEntity
public abstract class Entity implements Comparable<Entity> {
	@Id
	@GeneratedValue
	protected Long id;
	protected String cod, name;

//	@Override
//	public String toString() {
//		return name;
//	}

	@Override
	public int compareTo(Entity o) {
		return this.name.compareTo(((Entity) o).name);
	}
}