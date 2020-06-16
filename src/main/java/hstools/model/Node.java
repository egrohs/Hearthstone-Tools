package hstools.model;

import lombok.Data;

/**
 * Entidade abstrada, pode ser carta, mecanica, etc.
 * 
 * @author 99689650068
 *
 */
@Data
//@NodeEntity
public abstract class Node implements Comparable<Node> {
//	@Id
//	@GeneratedValue
	protected Long id;
	protected String name;

//	@Override
//	public String toString() {
//		return name;
//	}

	@Override
	public int compareTo(Node o) {
		return this.name.compareTo(((Node) o).name);
	}
}