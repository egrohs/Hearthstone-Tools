package hcs;

public class Synergy<T extends Comparable<? super T>> implements Comparable<Synergy> {
	Entidade e1, e2;
	Float valor;

	public Synergy(Entidade e1, Entidade e2, Float v) {
		this.e1 = e1;
		this.e2 = e2;
		this.valor = v;
	}

	@Override
	public int compareTo(Synergy o) {
		if (valor > o.valor) {
			return -1;
		} else if (valor < o.valor) {
			return 1;
		}
		return 0;
	}
}