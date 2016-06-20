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

public class Sinergy {
	static List<Card> cards = new ArrayList<Card>();
	private static Long atta = 0l;
	private static int cont = 0;
	static FileWriter fw = null;

	private static void printCards() {
		System.out.println("Cost|Name|Set|Rariry|Type|Race|Class|Attack|Health|Text");
		for (Card c : cards) {
			System.out.println(c.cost + "|" + c.name + "|" + c.set + "|" + c.rarity + "|" + c.type + "|" + c.faction
					+ "|" + c.playerClass + "|" + c.attack + "|" + c.health + "|" + c.dur + "|" + c.text);
		}
	}

	public static void main(String[] args) {
		try {
			fw = new FileWriter("output/hs.csv");

			// buildMechanics();
			readCards();
			// printCards();

			TGFParser.readMechanics("input/hs.tgf");
			// buildCards();
			parseCards();

			// readCombos();

			// for (Card c : cards) {
			// getSinergies(c.name);
			// }
			String card = "Whirlwind";
			System.out.println("Sinergy for " + card);
			getSinergies(card);
			// printCardMechanics();

			// for (Card c : cards) {
			// printCard(c);
			// }
			// System.out.println(cont);
			// System.out.println(atta);
			// for (Mechanic m : mechanics) {
			// System.out.println(m.name);
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	private static void readCombos() {
		Scanner sc = null;
		try {
			sc = new Scanner(new FileReader("input/combos.csv"));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				List<String> ccs = new LinkedList<String>(Arrays.asList(line.split(";")));
				String bse = ccs.remove(0);
				List<String> sin = getSinergies(bse);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sc.close();
			// try {
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	private static Card getCard(String name) {
		for (Card c : cards) {
			if (c.name.equals(name)) {
				return c;
			}
		}
		return null;
	}

	private static List<String> getSinergies(String cardName) {
		List<String> sin = new ArrayList<String>();
		try {
			Card c1 = getCard(cardName);
			// TODO remover
			if (c1 != null && c1.text != null) {
				cont = 0;
				fw.write(c1.name + ";" + c1.text + ";");
				for (Mechanic m1 : c1.mechanics) {
					for (Card c2 : cards) {
						if (c1.playerClass == null || c2.playerClass == null || c2.playerClass.equals(c1.playerClass)) {
							for (Mechanic m2 : c2.mechanics) {
								if (m1.aff.contains(m2)) {
									fw.write(c2.name + ";" + c2.playerClass + ";" + c2.text + "\n");
									System.out.println(c2.name + ";" + c2.playerClass + ";" + c2.text);
									sin.add(c2.name);
									cont++;
									break;
								}
							}
						}
					}
				}
				fw.write(";" + cont + "\n");
			} else {
				System.out.println(cardName + " NÃO ENCONTRADA");
			}
		} catch (Exception e) {
			System.err.println(e);
		}
		return sin;
	}

	private static void parseCards() {
		for (Card c : cards) {
			for (Mechanic m : TGFParser.mechs.values()) {
				if (c.text != null) {
					Matcher ma = Pattern.compile(m.regex).matcher(c.text);
					if (ma.find()) {
						c.mechanics.add(m);
					}
				}
			}
		}
	}

	/*
	 * TODO talvez os textos não devem ir pra lowercase, pois palavras curtas
	 * como "all" podem ser dificeis de identificar.
	 */

	private static void readCards() {
		JSONParser parser = new JSONParser();

		try {
			JSONArray sets = (JSONArray) parser.parse(new FileReader("input/cards.collectible.json"));
			readJSON(sets);
			System.out.println(cards.size() + " cards imported");
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// parser.close
		}
	}

	private static void readJSON(JSONArray array) {
		Iterator<JSONObject> iterator = array.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			Boolean col = (Boolean) o.get("collectible");
			if (col != null && col == true && !"HERO".equals((String) o.get("type"))) {
				cards.add(new Card((String) o.get("name"), (String) o.get("set"), (String) o.get("race"),
						(String) o.get("playerClass"), (String) o.get("type"), (String) o.get("text"),
						(Long) o.get("cost"), (Long) o.get("attack"), (Long) o.get("health"),
						(Long) o.get("durability"), (String) o.get("rarity")));
				if ((Long) o.get("health") != null) {
					atta += (Long) o.get("health");
					cont++;
				}
			}
		}
	}

	// static List<Mechanic> mechanics = new ArrayList<Mechanic>();

	// public static Mechanic getMechanic(int id) {
	// for (Mechanic m : mechanics) {
	// if (m.id == id) {
	// return m;
	// }
	// }
	// return null;
	// }

	private static void printCardMechanics() {
		FileWriter fw = null;
		try {
			fw = new FileWriter("output/hs.csv");
			for (Card c : cards) {
				// TODO remover essa linha
				if (c.text != null) {
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
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
					if (m.eval(c) && ma.find()) {
						c.mechanics.add(m);
					}
				}
			}
		}
	}
}