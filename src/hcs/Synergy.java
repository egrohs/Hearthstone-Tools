package hcs;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Synergy {
	static List<Card> cards = new ArrayList<Card>();
	private static int cont = 0;
	private static TGFParser tgfp;

	public static void main(String[] args) {
		init();
		// String card = "Frothing Berserker";
		// System.out.println("Sinergy for " + card);
		// generateTextSynergies(card);
		// printCardMechanics();
		// TGFToMatrix();
		generateCardSynergies();
		// generateTextSynergies("Azure Drake");
		// countMAffinities();
		// printCard("Acidic Swamp Ooze");
		printDeck(buildDeck("Azure Drake"));
	}

	private static void printDeck(Set<Card> deck) {
		for (Card card : deck) {
			System.out.println(card.name + "\t\t\t" + card.playerClass + "\t" + card.text);
		}
	}

	private static void init() {
		// buildMechanics();
		readCards();
		// printCards();
		tgfp = new TGFParser();
		// buildCards();
		parseCardsText2Mechanics();
	}

	// TODO verificar a classe das cartas do deck?
	private static Set<Card> buildDeck(String cardname) {
		Set<Card> deck = new HashSet<Card>();
		Card c = getCard(cardname);
		deck.add(c);
		for (Card s : c.synergies.keySet()) {
			if (c.synergies.get(s) > 2) {
				deck.add(s);
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

	/**
	 * Gera as sinergias de todas as cartas.
	 */
	private static void generateCardSynergies() {
		for (Card c : cards) {
			Set<Mechanic> set = new HashSet<Mechanic>();
			// System.out.print(c.name + "\t" + c.mechanics.size() + "\t");
			for (Mechanic m : c.mechanics) {
				for (Mechanic m2 : m.aff) {
					set.add(m2);
				}
			}

			for (Mechanic m3 : set) {
				// System.out.print(m.regex + "\t");
				for (Card c2 : cards) {
					if (c2.mechanics.contains(m3)) {
						Float sin = c.synergies.get(c2);
						if (sin == null) {
							sin = 0.0f;
						}
						c.synergies.put(c2, sin + 1);
					}
				}
			}
			// System.out.println();
			// System.out.println(c.name + "\t" + c.synergies.size());
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
	 * Identifica todas cartas que tem sinergia com a informada.
	 * 
	 * @param cardName
	 *            Nome da carta consultada.
	 * @return Lista das sinergias.
	 */
	private static List<Card> printCardSynergies(String cardName) {
		// TODO auto loops sinergies
		List<Card> sin = new ArrayList<Card>();
		// FileWriter fw = null;
		try {
			// fw = new FileWriter("output/hs.csv");
			Card c1 = getCard(cardName);
			if (c1 != null) {
				cont = 0;
				// fw.write(c1.name + ";" + c1.text + ";");
				for (Mechanic m1 : c1.mechanics) {
					for (Card c2 : cards) {
						if (c1.playerClass == null || c2.playerClass == null || c2.playerClass.equals(c1.playerClass)) {
							for (Mechanic m2 : c2.mechanics) {
								if (m1.aff.contains(m2)) {
									// fw.write(c2.name + ";" + c2.playerClass +
									// ";" + c2.text + "\n");
									System.out.println(c2.name + "\t" + c2.playerClass + "\t" + c2.text);
									sin.add(c2);
									cont++;
									break;
								}
							}
						}
					}
				}
				// fw.write(";" + cont + "\n");
			} else {
				System.out.println(cardName + " NÃO ENCONTRADA");
			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			// try {
			// fw.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}
		return sin;
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
						c.synergies.put(c2, value);
					}
				}
			}
		}
	}

	/**
	 * Lê os textos das cartas, gerando suas listas de sinergia.
	 */
	private static void parseCardsText2Mechanics() {
		for (Card c : cards) {
			int a = 0;
			for (Mechanic m : TGFParser.mechs.values()) {
				if ("AGGRO MINION".equals(m.regex) && c.aggro) {
					c.mechanics.add(m);
				} else if ("DMG SPELL".equals(m.regex) && "SPELL".equals(c.type)
						&& Pattern.compile("deal \\d+(\\-\\d+)? damage").matcher(c.text).find()) {
					c.mechanics.add(m);
				} else if ("HIGH ATTACK MINION".equals(m.regex) && "MINION".equals(c.type) && c.attack > 7) {
					c.mechanics.add(m);
				} else if ("HIGH HP MINION".equals(m.regex) && "MINION".equals(c.type) && c.health > 6) {
					c.mechanics.add(m);
				} else if ("LOW HP MINION".equals(m.regex) && "MINION".equals(c.type) && c.health < 3) {
					c.mechanics.add(m);
				} else if ("HIGH COST CARD".equals(m.regex) && c.cost > 5) {
					c.mechanics.add(m);
				} else if ("LOW COST CARD".equals(m.regex) && c.cost < 3) {
					c.mechanics.add(m);
				} else if ("LOW COST MINION".equals(m.regex) && "MINION".equals(c.type) && c.cost < 3) {
					c.mechanics.add(m);
				} else if ("LOW COST SPELL".equals(m.regex) && "SPELL".equals(c.type) && c.cost < 3) {
					c.mechanics.add(m);
				} else if (m.regex.contains("DISADVANTAGES") && "MINION".equals(c.type)
						&& Pattern.compile(m.regex).matcher(c.text).find()) {
					c.mechanics.add(m);
				} else if (Pattern.compile(m.regex).matcher(c.text).find()) {
					c.mechanics.add(m);
				}
			}
		}
	}

	private static void printTGFToMatrix() {
		for (Mechanic m : tgfp.mechs.values()) {
			System.out.print(m.regex + "\t");
			for (Mechanic m2 : tgfp.mechs.values()) {
				if (m.aff.contains(m2) || m2.aff.contains(m)) {
					System.out.print("1\t");
				} else {
					System.out.print("0\t");
				}
			}
			System.out.println();
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
		for (Mechanic m : TGFParser.mechs.values()) {
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