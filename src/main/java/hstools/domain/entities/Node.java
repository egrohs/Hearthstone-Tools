package hstools.domain.entities;

import lombok.Data;

/**
 * Abstract Entity for neo4j graph visualization.
 * 
 * @author EGrohs
 *
 */
@Data
//@NodeEntity
public abstract class Node implements Comparable<Node> {
//	@Id
//	@GeneratedValue
	protected Long id;
	protected String name;
	protected Double size;

	public Node(Long id) {
		this.id = id;
		this.size = Double.valueOf("1");
	}

//	@Override
//	public String toString() {
//		return name;
//	}

	@Override
	public int compareTo(Node o) {
		return this.name.compareTo(((Node) o).name);
	}
}