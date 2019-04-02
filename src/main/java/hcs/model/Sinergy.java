package hcs.model;

import lombok.Data;

/**
 * Classe que mantem vinculo de sinergia entre duas entidades do jogo (cartas,
 * mecanicas, etc).
 * 
 * @author 99689650068
 *
 * @param <T>
 */
@Data
//@EqualsAndHashCode(callSuper=true)
public class Sinergy<T extends Entity> implements Comparable<Sinergy<T>> {
	private Entity e1, e2;
	private int freq;
	private Float weight = 0f;
	private String label, mechs;

	public Sinergy(Entity e1, Entity e2, int freq, Float v, String mm) {
		this.e1 = e1;
		this.e2 = e2;
		this.freq = freq;
		this.weight = v;
		this.mechs = mm;
	}

	public Sinergy(Entity e1, Entity e2, String label, Float v) {
		this.e1 = e1;
		this.e2 = e2;
		this.weight = v;
		this.label = label;
	}

	public Sinergy(Entity e1, Entity e2, float v, String mm) {
		this.e1 = e1;
		this.e2 = e2;
		this.weight = v;
		this.mechs = mm;
	}

	public Sinergy(Entity e1, Entity e2, int freq) {
		this.e1 = e1;
		this.e2 = e2;
		this.freq = freq;
	}

	// TODO verificar se esse equals n�o esta duplicando os resultados e
	// deixando mais lento.
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Sinergy))
			return false;
		Sinergy<T> s2 = (Sinergy<T>) obj;
		// from here, must not test for nulls.
		if (weight != s2.getWeight())
			return false;
		if ((e1 != s2.getE1() || e2 != s2.getE2()) && e1 != s2.getE2())
			return false;
		return true;
	}

	@Override
	public int compareTo(Sinergy<T> o) {
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
}