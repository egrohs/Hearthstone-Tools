package hcs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Synergy {
	static List<Card> cards = new ArrayList<Card>();
	private static int cont = 0;

	public static void main2(String[] args) {
		init();
		testCombos();
	}

	public static void main(String[] args) {
		init();
		String card = "Frothing Berserker";
		System.out.println("Sinergy for " + card);
		generateTextSynergies(card);
		// printCardMechanics();
	}

	private static void init() {
		// buildMechanics();
		readCards();
		// printCards();
		TGFParser.readMechanics("input/hs.tgf");
		// buildCards();
		parseCards();
	}

	private static void printCards() {
		System.out.println("Cost|Name|Set|Rariry|Type|Race|Class|Attack|Health|Text");
		for (Card c : cards) {
			System.out.println(c.cost + "|" + c.name + "|" + c.set + "|" + c.rarity + "|" + c.type + "|" + c.faction
					+ "|" + c.playerClass + "|" + c.attack + "|" + c.health + "|" + c.dur + "|" + c.text);
		}
	}

	private static void countClassCards() {
		int warrior = 0, neutral = 0;
		for (Card c : cards) {
			// getSinergies(c.name);
			if (c.playerClass == null) {
				warrior++;
			} else if (c.playerClass.equals("Warrior")) {
				neutral++;
			}
		}
		System.out.println(warrior);
		System.out.println(neutral);
		// getSinergies("Wild Pyromancer");

		// printCardMechanics();

		// for (Card c : cards) {
		// printCard(c);
		// }
		// System.out.println(cont);
		// System.out.println(atta);
		// for (Mechanic m : mechanics) {
		// System.out.println(m.name);
		// }
	}

	/**
	 * Lê arquivo de combos, testando quais o algoritmo consegue encontrar.
	 */
	private static void testCombos() {
		Scanner sc = null;
		try {
			sc = new Scanner(new FileReader("input/combos.csv"));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				List<String> ccs = new LinkedList<String>(Arrays.asList(line.split(";")));
				String bse = ccs.remove(0);
				List<String> sin = generateTextSynergies(bse);
				// cards[0] = cards[1];
				for (String c : ccs) {
					if (!sin.contains(c)) {
						System.out.println(bse + "\tnão contem\t" + c);
						System.out.println(getCard(bse).text);
						Card ccc = getCard(c);
						System.out.println(ccc == null ? "" : ccc.text);
						System.out.println();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sc.close();
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
	private static List<String> generateTextSynergies(String cardName) {
		List<String> sin = new ArrayList<String>();
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
									System.out.println(c2.name + ";" + c2.playerClass + ";" + c2.text);
									sin.add(c2.name);
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
			Long numid = (Long) o.get("numid");
			Card c = getCard(id);
			c.setNumid(numid);
		}
	}
	
	/**
	 * Lê os textos das cartas, gerando suas listas de sinergia.
	 */
	private static void parseCards() {
		for (Card c : cards) {
			for (Mechanic m : TGFParser.mechs.values()) {
				if ("AGGRO MINION".equals(m.regex) && c.aggro) {
					c.mechanics.add(m);
				} else if ("DMG SPELL".equals(m.regex) && "SPELL".equals(c.type)
						&& c.text.contains("deal \\d+ damage")) {
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
				} else if (c.text != null) {
					if ("DISADVANTAGES".equals(m.regex) && c.text.contains("attack the wrong enemy")) {
						c.mechanics.add(m);
					} else {
						Matcher ma = Pattern.compile(m.regex).matcher(c.text);
						if (ma.find()) {
							c.mechanics.add(m);
						}
					}
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
				cards.add(new Card((Long) o.get("id"), (String) o.get("name"), (String) o.get("set"),
						(String) o.get("race"), (String) o.get("playerClass"), (String) o.get("type"),
						(String) o.get("text"), (Long) o.get("cost"), (Long) o.get("attack"), (Long) o.get("health"),
						(Long) o.get("durability"), (String) o.get("rarity")));
			}
		}
	}

	/**
	 * Imprime em arquivo as mecanicas mapeadas.
	 */
	private static void printCardMechanics() {
		FileWriter fw = null;
		try {
			fw = new FileWriter("output/hs.csv");
			for (Card c : cards) {
				int acum = 0;
				System.out.print(c.name + "§");
				fw.write(c.name + "§");
				fw.write(c.text + "§");
				for (Mechanic m : c.mechanics) {
					System.out.print(m.regex + "--");
					fw.write(m.regex + "--");
					acum++;
				}
				System.out.println("§" + acum);
				fw.write("§" + acum + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void buildCards() {
		for (Card c : cards) {
			String text = (String) c.text;
			if (text != null) {
				for (Mechanic m : TGFParser.mechs.values()) {
					Matcher ma = Pattern.compile(m.regex).matcher(text);
					// TODO usar ou nao esse eval de Card?
					if (m.eval(c) && ma.find()) {
						c.mechanics.add(m);
					}
				}
			}
		}
	}
}