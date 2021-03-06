package hstools.domain.entities;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import lombok.Data;

/**
 * Keeps a sinergy relationship between any two game entities.
 * 
 * @author EGrohs
 *
 * @param <T>
 */
@Data
@RelationshipEntity(type = "SYNERGY")
//@EqualsAndHashCode(callSuper=true)
public class SynergyEdge<S extends Node, T extends Node> extends Node {// implements Comparable<SynergyEdge<S, T>> {
	@StartNode
	private S source;
	@EndNode
	private T target;
	private int freq;
	private Float weight = 0f;
	private String label, mechs;

	public SynergyEdge(S e1, T e2, int freq, Float v, String mm) {
		this.source = e1;
		this.target = e2;
		this.freq = freq;
		this.weight = v;
		this.mechs = mm;
	}

	public SynergyEdge(S e1, T e2, String label, Float v) {
		this.source = e1;
		this.target = e2;
		this.label = label;
		this.weight = v;
	}

	public SynergyEdge(S e1, T e2, float v, String mm) {
		this.source = e1;
		this.target = e2;
		this.weight = v;
		this.mechs = mm;
	}

	public SynergyEdge(S e1, T e2, int freq) {
		if (e1 == null || e2 == null)
			throw new RuntimeException("Null nodes");
		this.source = e1;
		this.target = e2;
		this.freq = freq;
	}

	// TODO verificar se esse equals n�o esta duplicando os resultados e
	// deixando mais lento.
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SynergyEdge))
			return false;
		SynergyEdge<S, T> s2 = (SynergyEdge<S, T>) obj;
		// from here, must not test for nulls.
		if (weight != s2.getWeight())
			return false;
		if ((source != s2.getSource() || target != s2.getTarget()) && source != s2.getTarget())
			return false;
		return true;
	}
//	@Override
//	public int compareTo(Node o) {
//		// TODO Auto-generated method stub
//		return super.compareTo(o);
//	}
//	@Override
//	public int compareTo(SynergyEdge<S, T> o) {
//		if (freq > o.freq) {
//			return -1;
//		} else if (freq < o.freq) {
//			return 1;
//		}
//		if (weight > o.getWeight()) {
//			return -1;
//		} else if (weight < o.getWeight()) {
//			return 1;
//		}
//		return 0;
//	}

	@Override
	public String toString() {
		return source + "\t" + target + "\t" + label;
	}
}