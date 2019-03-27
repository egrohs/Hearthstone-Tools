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
public class Sinergy<T extends Entity> implements Comparable<Sinergy<T>> {
	private Entity e1, e2;
	private int freq;
	private Float valor = 0f;
	private String mechs;

	public Sinergy(Entity e1, Entity e2, int freq, Float v, String mm) {
		this.e1 = e1;
		this.e2 = e2;
		this.freq = freq;
		this.valor = v;
		this.mechs = mm;
	}

	public Sinergy(Entity e1, Entity e2, float v, String mm) {
		this.e1 = e1;
		this.e2 = e2;
		this.valor = v;
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
		Sinergy<Entity> s2 = (Sinergy<Entity>) obj;
		// from here, must not test for nulls.
		if (valor != s2.getValor())
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
		if (valor > o.getValor()) {
			return -1;
		} else if (valor < o.getValor()) {
			return 1;
		}
		return 0;
	}
}