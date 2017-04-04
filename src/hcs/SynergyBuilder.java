package hcs;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SynergyBuilder {
	static List<Carta> cards = new ArrayList<Carta>();
	static List<Sinergia> cardSynergies = new ArrayList<Sinergia>();
	// private static int cont = 0;
	private static TGFParser tgfp;
	// private static List<Card> deck = new ArrayList<Card>();

	public enum CLASS {
		WARRIOR, DRUID, HUNTER, PRIEST, MAGE, SHAMAN, ROGUE, PALADIN, WARLOCK;
	}

	public static void main(String[] args) throws Exception {
		init();
		// String card = "Frothing Berserker";
		// System.out.println("Sinergy for " + card);
		// generateTextSynergies(card);
		// printCardMechanics();
		// TGFToMatrix();
		// generateTextSynergies("Azure Drake");
		// countMAffinities();
		// printCard("Acidic Swamp Ooze");
		printDeck(buildDeck(CLASS.SHAMAN, new String[] { "Tunnel Trogg" }, new HashSet<Carta>(), 0));
		// printCard("Dread Corsair");
		// printM2M();
		// for (Sinergia s : sins) {
		// System.out.println(s.e1 + "\t" + s.e2 + "\t" + s.valor);
		// }
		// System.out.println(sins.size());
	}

	private static void printDeck(Collection<Carta> deck) {
		for (int i = 0; i < deck.size(); i++) {
			Carta c1 = (Carta) deck.toArray()[i];
			int cont = 0;
			Float acum = 0f;
			for (int j = i; j < deck.size(); j++) {
				Carta c2 = (Carta) deck.toArray()[j];
				Sinergia s = Sinergia.getSinergy(cardSynergies, c1, c2);
				acum += s != null ? s.valor : 0f;
				cont++;
			}
			System.out.println(acum / cont + "\t" + c1.name + "\t" + c1.playerClass + "\t" + c1.text);
		}
	}

	private static void printM2M() {
		for (Sinergia s : TGFParser.mechanicsSynergies) {
			System.out.println(((Mecanica) s.e1).regex + "\t" + ((Mecanica) s.e2).regex);
		}
	}

	private static void init() {
		// buildMechanics();
		cards = LeCartas.readCards();
		// printCards();
		tgfp = new TGFParser();
		// buildCards();
		parseCardsText2Mechanics();
		// generateCardSynergies();
	}

	private static Set<Carta> buildDeck(CLASS classe, String[] initialCards, Set<Carta> deck, int depth)
			throws Exception {
		System.out.println("Sinergias para " + initialCards[0]);
		for (String cardname : initialCards) {
			Carta c = LeCartas.getCard(cardname);
			if (c == null) {
				throw new Exception("Carta não encontrada, " + cardname);
			}
			for (Sinergia s : cardSynergies) {
				Carta c1 = (Carta) s.e1;
				Carta c2 = (Carta) s.e2;
				if (c == c1 || c == c2) {
					if (classe == null || (c1.playerClass == null || classe.toString().equals(c1.playerClass))
							&& (c2.playerClass == null || classe.toString().equals(c2.playerClass))) {
						deck.add(c1);
						deck.add(c2);
					}
				}
			}
		}
		return deck;
	}

	private static void printCard(String n) {
		Carta card = LeCartas.getCard(n);
		for (Mecanica m : card.mechanics) {
			System.out.println(m.regex);
		}
	}

	/**
	 * Imprime todas cartas que tem sinergia com a informada.
	 * 
	 * @param cardName
	 *            Nome da carta consultada.
	 */
	private static void printCardSynergies(String cardName) {
		Carta card = LeCartas.getCard(cardName);
		List<Sinergia> minhaS = new ArrayList<>();
		for (Sinergia s : cardSynergies) {
			if (s.e1 == card || s.e2 == card) {
				minhaS.add(s);
				// + s.e1.playerClass +
				// "\t" + s.e1.text);
			}
		}
		Collections.sort(minhaS);
		for (Sinergia s : minhaS) {
			System.out.println(s.e1.name + "\t");
		}
	}

	/**
	 * Lê arquivo de sinergias.
	 */
	private static void readSynergies() {
		JSONParser parser = new JSONParser();
		try {
			JSONArray sets = (JSONArray) parser.parse(new FileReader("input/synergy.json"));
			System.out.println(sets.size() + " synergies imported");
			generateNumIds(sets);
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateNumIds(JSONArray sets) {
		Iterator<JSONObject> iterator = sets.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			String id = (String) o.get("id");
			String numid = (String) o.get("numid");
			Carta c = LeCartas.getCard(id);
			c.setNumid(numid);

		}
		iterator = sets.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			String id = (String) o.get("id");
			Carta c = LeCartas.getCard(id);
			JSONArray sin = (JSONArray) o.get("synergies");
			if (sin != null) {
				Iterator<JSONArray> iterator2 = sin.iterator();
				while (iterator2.hasNext()) {
					JSONArray o2 = iterator2.next();
					Carta c2 = LeCartas.getCard((String) o2.get(0));
					Float value = Float.parseFloat(o2.get(1).toString());
					// TODO remover esse if
					if (value > 4.0) {
						cardSynergies.add(new Sinergia(c, c2, value));
					}
				}
			}
		}
	}

	/**
	 * Lê os textos das cartas, gerando suas listas de sinergia.
	 */
	private static void parseCardsText2Mechanics() {
		List<Sinergia> cardSynergies = new ArrayList<Sinergia>();

		for (Mecanica m : TGFParser.mechanics.values()) {
			for (Carta c : cards) {
				if (Pattern.compile(m.regex).matcher(c.text).find()) {
					c.mechanics.add(m);
				}
			}
		}
		generateCardSynergies();
	}

	/**
	 * Gera as sinergias de todas as cartas.
	 */
	private static void generateCardSynergies() {
		for (Sinergia s : TGFParser.mechanicsSynergies) {
			Mecanica m1 = (Mecanica) s.e1;
			Mecanica m2 = (Mecanica) s.e2;
			for (Carta c : cards) {
				if (c.mechanics.contains(m1)) {
					for (Carta c2 : cards) {
						if (c2.mechanics.contains(m2)) {
							Sinergia ss = Sinergia.getSinergy(cardSynergies, c, c2);
							if (ss == null) {
								ss = new Sinergia(c, c2, s.valor);
								cardSynergies.add(ss);
							} else {
								ss.valor += s.valor;
							}
						}
					}
				}
			}
		}
		Collections.sort(cardSynergies);
	}

	private static void parseCardsText2Mechanics2() {
		for (Carta c : cards) {
			for (Mecanica m : TGFParser.mechanics.values()) {
				if ("AGGRO MINION".equals(m.regex) && c.aggro) {
					c.mechanics.add(m);
				} else if ("DMG SPELL".equals(m.regex) && "SPELL".equals(c.type)
						&& Pattern.compile("deal \\d+(\\-\\d+)? damage").matcher(c.text).find()) {
					c.mechanics.add(m);
					/*
					 * } else if ("HIGH ATTACK MINION".equals(m.regex) &&
					 * "MINION".equals(c.type) && c.attack > 7) {
					 * c.mechanics.add(m); } else if ("HIGH HP MINION"
					 * .equals(m.regex) && "MINION".equals(c.type) && c.health >
					 * 6) { c.mechanics.add(m); } else if ("LOW HP MINION"
					 * .equals(m.regex) && "MINION".equals(c.type) && c.health <
					 * 3) { c.mechanics.add(m); } else if ("HIGH COST CARD"
					 * .equals(m.regex) && c.cost > 5) { c.mechanics.add(m); }
					 * else if ("LOW COST CARD".equals(m.regex) && c.cost < 3) {
					 * c.mechanics.add(m); } else if ("LOW COST MINION"
					 * .equals(m.regex) && "MINION".equals(c.type) && c.cost <
					 * 3) { c.mechanics.add(m); } else if ("LOW COST SPELL"
					 * .equals(m.regex) && "SPELL".equals(c.type) && c.cost < 3)
					 * { c.mechanics.add(m);
					 */
				} else if (m.regex.contains("DISADVANTAGES") && "MINION".equals(c.type)
						&& Pattern.compile(m.regex).matcher(c.text).find()) {
					c.mechanics.add(m);
				} else if (Pattern.compile(m.regex).matcher(c.text).find()) {
					c.mechanics.add(m);
				}
			}
		}
	}

	private static void countMAffinities() {
		for (Mecanica m : TGFParser.mechanics.values()) {
			int cont = 0;
			// System.out.println(m.regex + "\t" + m.aff.size());
			for (Carta card : cards) {
				if (card.mechanics.contains(m)) {
					cont++;
				}
			}
			System.out.println(m.regex + "\t" + cont);
		}
	}
}