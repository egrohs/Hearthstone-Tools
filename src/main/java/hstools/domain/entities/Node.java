package hstools.domain.entities;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import lombok.Data;

/**
 * Abstract Entity for neo4j graph visualization.
 * 
 * @author EGrohs
 *
 */
@Data
@NodeEntity
public abstract class Node implements Comparable<Node> {
	@Id
	@GeneratedValue
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