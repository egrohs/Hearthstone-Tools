package hcs;

import java.util.List;

public class Sinergia<T extends Comparable<? super T>> implements Comparable<Sinergia> {
	Entidade e1, e2;
	Float valor;

	public Sinergia(Entidade e1, Entidade e2, Float v) {
		this.e1 = e1;
		this.e2 = e2;
		this.valor = v;
	}

	@Override
	public int compareTo(Sinergia o) {
		if (valor > o.valor) {
			return -1;
		} else if (valor < o.valor) {
			return 1;
		}
		return 0;
	}

	public static Sinergia getSinergy(List<Sinergia> sinergias, Entidade e1, Entidade e2) {
		for (Sinergia s : sinergias) {
			if ((e1 == s.e1 && e2 == s.e2) || (e1 == s.e2 && e2 == s.e1)) {
				return s;
			}
		}
		return null;
	}
}