package hcs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import hcs.model.Carta;
import hcs.model.Carta.CLASS;
import hcs.model.Entidade;
import hcs.model.Sinergia;

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