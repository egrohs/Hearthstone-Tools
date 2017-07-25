package hcs.model;

/**
 * Classe que mantem vinculo de sinergia entre duas entidades do jogo (cartas,
 * mecanicas, etc).
 * 
 * @author 99689650068
 *
 * @param <T>
 */
public class Sinergia<T extends Comparable<? super T>> implements Comparable<Sinergia> {
	public Entidade e1, e2;
	public int freq;
	public Float valor = 0f;
	public String mechs;

	public Sinergia(Entidade e1, Entidade e2, int freq, Float v, String mm) {
		this.e1 = e1;
		this.e2 = e2;
		this.freq = freq;
		this.valor = v;
		this.mechs = mm;
	}

	public Sinergia(Entidade e1, Entidade e2, float v, String mm) {
		this.e1 = e1;
		this.e2 = e2;
		this.valor = v;
		this.mechs = mm;
	}

	public Sinergia(Entidade e1, Entidade e2, int f) {
		this.e1 = e1;
		this.e2 = e2;
		this.freq = f;
	}

	// TODO verificar se esse equals não esta duplicando os resultados e
	// deixando mais lento.
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Sinergia))
			return false;
		Sinergia s2 = (Sinergia) obj;
		// from here, must not test for nulls.
		if (valor != s2.valor)
			return false;
		if ((e1 != s2.e1 || e2 != s2.e2) && e1 != s2.e2)
			return false;
		return true;
	}

	@Override
	public int compareTo(Sinergia o) {
		if (freq > o.freq) {
			return -1;
		} else if (freq < o.freq) {
			return 1;
		}
		if (valor > o.valor) {
			return -1;
		} else if (valor < o.valor) {
			return 1;
		}
		return 0;
	}
}