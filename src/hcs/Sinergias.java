package hcs;

import java.util.ArrayList;
import java.util.List;

import hcs.Carta.CLASS;

public class Sinergias {
	public static List<Sinergia> cardsSynergies = new ArrayList<Sinergia>();

	public Sinergias() {
		new SinergyFromGames();
		new SinergyFromText();
	}

	/**
	 * return the sinergy with those entities.
	 * 
	 * @param tipo
	 * 
	 * @param e1
	 * @param e2
	 * @return
	 */
	public static Sinergia getSinergy(Entidade e1, Entidade e2) {
		for (Sinergia s : cardsSynergies) {
			if ((e1 == s.e1 && e2 == s.e2) || (e1 == s.e2 && e2 == s.e1)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * return all sinegies for that entity.
	 * 
	 * @param e1
	 * @return
	 */
	public static List<Sinergia> getSinergias(Entidade e1, int manaRestante, CLASS opo) {
		List<Sinergia> sins = new ArrayList<Sinergia>();
		for (Sinergia s : cardsSynergies) {
			if ((e1 == s.e1 && CLASS.contem(opo, ((Carta) s.e2).classe) && ((Carta) s.e2).cost <= manaRestante)
					|| (e1 == s.e2 && CLASS.contem(opo, ((Carta) s.e1).classe)
							&& ((Carta) s.e1).cost <= manaRestante)) {
				sins.add(s);
			}
		}
		return sins;
	}
}

/**
 * Classe que mantem vinculo de sinergia entre duas entidades do jogo (cartas,
 * mecanicas, etc).
 * 
 * @author 99689650068
 *
 * @param <T>
 */
class Sinergia<T extends Comparable<? super T>> implements Comparable<Sinergia> {
	Entidade e1, e2;
	int freq;
	Float valor = 0f;
	String mechs;

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