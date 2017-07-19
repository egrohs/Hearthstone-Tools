package hcs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import hcs.model.Carta;
import hcs.model.Entidade;
import hcs.model.Carta.CLASS;

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
	 * Calcula as provaveis jogadas.
	 * 
	 * @param c
	 * @param manaRestante
	 *            Mana restante no turno atual.
	 * @return
	 */
	public static Set<Sinergia> getCardSinergies(Carta c, int manaRestante, CLASS opo) {
		Set<Sinergia> sub = new LinkedHashSet<Sinergia>();
		//Set<Carta> sub = new LinkedHashSet<Carta>();
		if (c != null) {
			for (Sinergia s : Sinergias.cardsSynergies) {
				if (s.e1 == c || s.e2 == c) {
					Carta c2 = (Carta) s.e2;
					if (c == c2) {
						c = (Carta) s.e1;
					}
					// cartas com sinergia com custo provavel no turno
					if (CLASS.contem(opo, c2.classe) && c2.cost <= manaRestante) {
						sub.add(s);
						System.out.println(c2 + "\t" + s.valor + "\t" + s.mechs);
					}
				}
			}
		}
		return sub;
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