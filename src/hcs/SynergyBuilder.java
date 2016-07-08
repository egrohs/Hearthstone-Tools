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
	static List<Card> cards = new ArrayList<Card>();
	static List<Synergy> sins = new ArrayList<Synergy>();
	// private static int cont = 0;
	private static TGFParser tgfp;
	// private static List<Card> deck = new ArrayList<Card>();

	public enum CLASS {
		WARRIOR, DRUID, HUNTER, PRIEST, MAGE, SHAMAN, ROGUE, PALADIN, WARLOCK;
	}

	public static void main(String[] args) {
		init();
		// String card = "Frothing Berserker";
		// System.out.println("Sinergy for " + card);
		// generateTextSynergies(card);
		// printCardMechanics();
		// TGFToMatrix();
		// generateTextSynergies("Azure Drake");
		// countMAffinities();
		// printCard("Acidic Swamp Ooze");
		printDeck(buildDeck(CLASS.WARRIOR, "Whirlwind", new HashSet<Card>()));
		// printCard("Dread Corsair");
		// printM2M();
		// for (Sinergia s : sins) {
		// System.out.println(s.e1 + "\t" + s.e2 + "\t" + s.valor);
		// }
		// System.out.println(sins.size());
	}

	private static void printDeck(Collection<Card> deck) {
		for (Card card : deck) {
			System.out
					.println(card.mechanics.size() + "\t" + card.name + "\t\t\t" + card.playerClass + "\t" + card.text);
		}
	}

	private static void printM2M() {
		for (Synergy s : TGFParser.mechanicsSynergies) {
			System.out.println(((Mechanic) s.e1).regex + "\t" + ((Mechanic) s.e2).regex);
		}
	}

	private static void init() {
		// buildMechanics();
		readCards();
		// printCards();
		tgfp = new TGFParser();
		// buildCards();
		parseCardsText2Mechanics();
		// generateCardSynergies();
	}

	private static Set<Card> buildDeck(CLASS classe, String cardname, Set<Card> deck) {
		Card c = getCard(cardname);
		for (Synergy s : sins) {
			if (c == s.e1) {
				if (((Card) s.e2).playerClass == null || classe.toString().equals(((Card) s.e2).playerClass)) {
					if (s.valor > 2) {
						deck.add((Card) s.e2);
					}
					// if (deck.size() < 20) {
					// buildDeck(classe, s.name, deck);
					// }
				}
			}
		}
		return deck;
	}

	private static void printCard(String n) {
		Card card = getCard(n);
		for (Mechanic m : card.mechanics) {
			System.out.println(m.regex);
		}
	}

	private static Card getCard(String idORname) {
		for (Card c : cards) {
			if (c.name.equals(idORname)) {
				return c;
			}
			if (c.id.equals(idORname)) {
				return c;
			}
			if (idORname.equals(c.numid)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Imprime todas cartas que tem sinergia com a informada.
	 * 
	 * @param cardName
	 *            Nome da carta consultada.
	 */
	private static void printCardSynergies(String cardName) {
		Card card = getCard(cardName);
		for (Synergy s : sins) {
			if (s.e1 == card || s.e2 == card) {
				System.out.println(s.e1.name + "\t");// + s.e1.playerClass + "\t" + s.e1.text);
			}
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
			Card c = getCard(id);
			c.setNumid(numid);

		}
		iterator = sets.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			String id = (String) o.get("id");
			Card c = getCard(id);
			JSONArray sin = (JSONArray) o.get("synergies");
			if (sin != null) {
				Iterator<JSONArray> iterator2 = sin.iterator();
				while (iterator2.hasNext()) {
					JSONArray o2 = iterator2.next();
					Card c2 = getCard((String) o2.get(0));
					Float value = Float.parseFloat(o2.get(1).toString());
					// TODO remover esse if
					if (value > 4.0) {
						sins.add(new Synergy(c, c2, value));
					}
				}
			}
		}
	}

	/**
	 * Lê os textos das cartas, gerando suas listas de sinergia.
	 */
	private static void parseCardsText2Mechanics() {
		List<Synergy> cardSynergies = new ArrayList<>();

		for (Mechanic m : TGFParser.mechanics.values()) {
			for (Card c : cards) {
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
		for (Synergy s : TGFParser.mechanicsSynergies) {
			Mechanic m1 = (Mechanic) s.e1;
			Mechanic m2 = (Mechanic) s.e2;
			for (Card c : cards) {
				if (c.mechanics.contains(m1)) {
					for (Card c2 : cards) {
						if (c2.mechanics.contains(m2)) {
							Synergy ss = getSinergy(c, c2);
							if (ss == null) {
								ss = new Synergy(c, c2, s.valor);
								sins.add(ss);
							} else {
								ss.valor += s.valor;
							}
						}
					}
				}
			}
		}
		Collections.sort(sins);
	}

	private static Synergy getSinergy(Entidade e1, Entidade e2) {
		for (Synergy s : sins) {
			if (e1 == s.e1 && e2 == s.e2) {
				return s;
			}
		}
		return null;
	}

	private static void parseCardsText2Mechanics2() {
		for (Card c : cards) {
			for (Mechanic m : TGFParser.mechanics.values()) {
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

	/**
	 * Carrega o db json de cartas em memória.
	 */
	private static void readCards() {
		JSONParser parser = new JSONParser();
		try {
			JSONArray sets = (JSONArray) parser.parse(new FileReader("input/cards.collectible.json"));
			generateCards(sets);
			System.out.println(cards.size() + " cards imported");
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Instancia os objetos cards.
	 * 
	 * @param array
	 *            JSONObject contendo o db de cartas.
	 */
	private static void generateCards(JSONArray array) {
		Iterator<JSONObject> iterator = array.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			Boolean col = (Boolean) o.get("collectible");
			if (col != null && col == true && !"HERO".equals((String) o.get("type"))) {
				cards.add(new Card((String) o.get("id"), (String) o.get("name"), (String) o.get("set"),
						(String) o.get("race"), (String) o.get("playerClass"), (String) o.get("type"),
						(String) o.get("text"), (Long) o.get("cost"), (Long) o.get("attack"), (Long) o.get("health"),
						(Long) o.get("durability"), (String) o.get("rarity")));
			}
		}
	}

	private static void countMAffinities() {
		for (Mechanic m : TGFParser.mechanics.values()) {
			int cont = 0;
			// System.out.println(m.regex + "\t" + m.aff.size());
			for (Card card : cards) {
				if (card.mechanics.contains(m)) {
					cont++;
				}
			}
			System.out.println(m.regex + "\t" + cont);
		}
	}
}