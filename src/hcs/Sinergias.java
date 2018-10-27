package hcs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hcs.model.Carta;
import hcs.model.Carta.CLASS;
import hcs.model.Entidade;
import hcs.model.Mecanica;
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

	public static Set<Carta> getMechsCards(Map<Mecanica, Integer> mechs, int manaRestante, CLASS opo) {
		Set<Carta> cs = new HashSet<Carta>();
		for (Mecanica mecanica : new ArrayList<Mecanica>(mechs.keySet())) {
			for (Sinergia s : SinergyFromText.mechanicsSynergies) {
				if (s.e1 == mecanica) {
					mechs.put(mecanica, 0);
				}
				if (s.e2 == mecanica) {
					mechs.put(mecanica, 0);
				}
			}
		}
		for (Mecanica mecanica : mechs.keySet()) {
			for (Carta c1 : Universo.cards) {
				if (c1.mechanics.contains(mecanica) && CLASS.contem(opo, c1.classe) && c1.cost <= manaRestante) {
					cs.add(c1);
				}
			}
		}
		return cs;
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
		// Set<Carta> sub = new LinkedHashSet<Carta>();
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

	// public static void main(String[] args) {
	// TODO charge e DD.
	// secret: when an enemy minion attacks, return it to its owner's hand
	// and it costs (2) more.
	static Pattern[] pts = new Pattern[] {
			Pattern.compile("return (a|an|all) (enemy )?minion(s)? to ((its|their) owner's|your opponent's) hand"),
			Pattern.compile("silence (a|all) (enemy )?(minion(s)?|[race])(\\.|\\, |\\s)?(with [ability])?"),
			// contem random antes de "into"?
			// secret: after your opponent plays a minion, transform it into
			// a 1/1
			// sheep. (somente esse caso de it)
			Pattern.compile("transform (a|all|another random) (enemy )?(minion(s)? )?into"),
			// control a secret? random control?
			Pattern.compile("(take|gain) control of"), Pattern.compile(
					"destroy (a|an|all|\\d) (random )?(damaged |frozen |legendary )?(enemy |other )?minion(s)?(\\s|\\.|\\,)(with (taunt|\\d+ or less attack|an attack of \\d+ or more))?") };
	// }

	public static Map<Pattern, Integer> calc(int manaRestante, CLASS opo) {
		Map<Pattern, Integer> res = new HashMap<Pattern, Integer>();
		for (Carta card : Universo.cards) {
			// System.out.println(card.text);
			if (CLASS.contem(opo, card.classe) && card.cost <= manaRestante) {
				for (Pattern p : pts) {
					Matcher matcher = p.matcher(card.text);
					Integer i = res.get(p);
					if (matcher.find()) {
						res.put(p, i == null ? 1 : (i + 1));
					}
				}
			}
		}
		return res;
	}

	public static void main(String[] args) {
		Universo.leCards();
		Map<Pattern, Integer> m = Sinergias.calc(3, CLASS.MAGE);
		for (Pattern p : m.keySet()) {
			System.out.println(m.get(p) + "\t" + p);
		}
	}
}