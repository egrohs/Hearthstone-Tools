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
	protected String id;// ex. card id = "GAME_005", deck id = deckstring...
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