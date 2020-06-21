package hstools.domain.entities;

import lombok.Data;

/**
 * Keeps a sinergy relationship between any two game entities.
 * 
 * @author EGrohs
 *
 * @param <T>
 */
@Data
//@RelationshipEntity(type = "SYNERGY")
//@EqualsAndHashCode(callSuper=true)
public class SynergyEdge<T extends Node> implements Comparable<SynergyEdge<T>> {
	private Long id;
//	@StartNode
	private Node e1;
//	@EndNode
	private Node e2;
	private int freq;
	private Float weight = 0f;
	private String label, mechs;

	public SynergyEdge(Node e1, Node e2, int freq, Float v, String mm) {
		this.e1 = e1;
		this.e2 = e2;
		this.freq = freq;
		this.weight = v;
		this.mechs = mm;
	}

	public SynergyEdge(Node e1, Node e2, String label, Float v) {
		this.e1 = e1;
		this.e2 = e2;
		this.label = label;
		this.weight = v;
	}

	public SynergyEdge(Node e1, Node e2, float v, String mm) {
		this.e1 = e1;
		this.e2 = e2;
		this.weight = v;
		this.mechs = mm;
	}

	public SynergyEdge(Node e1, Node e2, int freq) {
		if (e1 == null || e2 == null)
			throw new RuntimeException("Null nodes");
		this.e1 = e1;
		this.e2 = e2;
		this.freq = freq;
	}

	// TODO verificar se esse equals n�o esta duplicando os resultados e
	// deixando mais lento.
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SynergyEdge))
			return false;
		SynergyEdge<T> s2 = (SynergyEdge<T>) obj;
		// from here, must not test for nulls.
		if (weight != s2.getWeight())
			return false;
		if ((e1 != s2.getE1() || e2 != s2.getE2()) && e1 != s2.getE2())
			return false;
		return true;
	}

	@Override
	public int compareTo(SynergyEdge<T> o) {
		if (freq > o.freq) {
			return -1;
		} else if (freq < o.freq) {
			return 1;
		}
		if (weight > o.getWeight()) {
			return -1;
		} else if (weight < o.getWeight()) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return e1 + "\t" + e2 + "\t" + label;
	}
}