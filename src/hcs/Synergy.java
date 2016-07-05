package hcs;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Synergy {
	static List<Card> cards = new ArrayList<Card>();
	private static int cont = 0;
	private static TGFParser tgfp;

	public static void main2(String[] args) {
		init();
		// testCombos();
		readSynergies();
		// printCards();
		for (Card card : cards) {
			List<Card> sins = generateTextSynergies(card.name);
			// System.out.println(card.name + "\t" + card.text);
			for (Card card2 : card.synergies.keySet()) {
				if (!sins.contains(card2)) {
					System.out.println(card.name + "\t"
							+ /* card.text + ";" + */ card2.name /*
																	 * + ";" +
																	 * card2.
																	 * text
																	 */);
					// System.out.println(card.race+" *** "+card2.race);
				}
			}
		}
	}

	public static void main(String[] args) {
		init();
		// String card = "Frothing Berserker";
		// System.out.println("Sinergy for " + card);
		// generateTextSynergies(card);
		// printCardMechanics();
		// TGFToMatrix();
		countCMechanics();
		//countMAffinities();
	}

	private static void init() {
		// buildMechanics();
		readCards();
		// printCards();
		tgfp = new TGFParser();
		// buildCards();
		parseCardsText2Mechanics();
	}

	private static void printCards() {
		// System.out.println("Cost|Name|Set|Rariry|Type|Race|Class|Attack|Health|Text");
		for (Card c : cards) {
			// System.out.println(c.cost + "|" + c.name + "|" + c.set + "|" +
			// c.rarity + "|" + c.type + "|" + c.faction
			// + "|" + c.playerClass + "|" + c.attack + "|" + c.health + "|" +
			// c.dur + "|" + c.text);
			// System.out.println(c.name + "\t" +c.text + "\t"
			// +c.synergies.size());
			for (Card card : c.synergies.keySet()) {
				Float val = c.synergies.get(card);
				if (val > 4.0){
					System.out.println(c.name + "\t" + c.text);
					System.out.println(card.name + "\t" + card.text + "\n");
				}
			}
		}
	}

	private static void countCMechanics() {
		for (Card c : cards) {
			System.out.print(c.name + "\t" + c.mechanics.size() + "\t");
			for (Mechanic m : c.mechanics) {
				System.out.print(m.regex + "\t");
			}
			System.out.println();
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
	private static List<Card> generateTextSynergies(String cardName) {
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
									// System.out.println(c2.name + ";" +
									// c2.playerClass + ";" + c2.text);
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
				} else if ("HIGH ATTACK MINION".equals(m.regex) && "MINION".equals(c.type) && c.attack > 4) {
					c.mechanics.add(m);
				} else if ("HIGH HP MINION".equals(m.regex) && "MINION".equals(c.type) && c.health > 4) {
					c.mechanics.add(m);
				} else if ("LOW HP MINION".equals(m.regex) && "MINION".equals(c.type) && c.health < 4) {
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
					//if (!c.text.toString().contains("battlecry"))
					{
						c.mechanics.add(m);
					}
				} else if (Pattern.compile(m.regex).matcher(c.text).find()) {
					c.mechanics.add(m);
				}
			}
		}
	}

	private static void TGFToMatrix() {
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
			System.out.println(m.regex + "\t" + m.aff.size());
		}
	}
}