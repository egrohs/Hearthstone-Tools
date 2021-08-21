package hstools.domain.entities;

import com.sun.istack.NotNull;

import lombok.Data;

/**
 * Abstract Entity for neo4j graph visualization. You should write your equals
 * and hashCode in a domain specific way for managed entities. We strongly
 * advise developers to not use the native id described by a Long field in
 * combination with @Id @GeneratedValue in these methods.
 * 
 * @author EGrohs
 *
 */
@Data
public abstract class Node implements Comparable<Node> {
//	@Id
//	@GeneratedValue
	protected Long id; // temp id used by neo4j at runtime, must never be set by app
	@NotNull
	protected String name;
	protected Double size;

	public Node() {
		this.size = Double.valueOf("1");
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Node o) {
		return this.name.compareTo(((Node) o).name);
	}
}