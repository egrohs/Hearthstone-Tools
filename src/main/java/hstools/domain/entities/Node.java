package hstools.domain.entities;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

import lombok.Data;

/**
 * Abstract Entity for neo4j graph visualization.
 * 
 * @author EGrohs
 *
 */
@Data
public abstract class Node implements Comparable<Node> {
	@Id
	@GeneratedValue
	protected Long id; // temp id used by neo4j at runtime, must never be set by app
	protected String name;
	protected Double size;

	public Node() {
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