package hcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hcs.model.Card;
import hcs.model.Card.CLASS;
import hcs.model.Mechanic;
import hcs.model.Sinergy;
import hcs.model.Tag;

public class CardBuilder {
	static List<Card> cards = new ArrayList<Card>();
	Set<Sinergy<Card>> cardsSynergies = new HashSet<Sinergy<Card>>();
	private ClassLoader cl = this.getClass().getClassLoader();
	private TagBuilder tb;
	final String api = "https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json";

	public CardBuilder() {
		tb = new TagBuilder();
		buildCards();
		tb.buildCardTags();
		// TODO
		// generateCardsSynergies(tb.tagsSynergies);
	}

	private int containsTag(Tag tag) {
		int i = 0;
		for (Card card : cards) {
			if (card.getTags().contains(tag)) {
				i++;
				System.out.println(card.getName());
			}
		}
		System.out.println(i);
		return i;
	}

	/**
	 * Load json card db api in memory.
	 */
	private List<Card> buildCards() {
		if (cards.size() == 0) {
			long ini = System.currentTimeMillis();
			try {
				File file = new File("cards.collectible.json");
				if (!file.exists()) {
					// file.delete();
					Files.copy(new URL(api).openStream(), Paths.get("cards.collectible.json"));
				}
				JSONParser parser = new JSONParser();
				JSONArray sets = (JSONArray) parser.parse(new FileReader(file));
				generateCards(sets);
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(
					cards.size() + " cards imported in " + (System.currentTimeMillis() - ini) / 1000 + " seconds.");
		}
		return cards;
	}

	/**
	 * Parse json array and create card objects.
	 * 
	 * @param array JSONObject with cards data.
	 */
	private void generateCards(JSONArray array) {
		Iterator<JSONObject> iterator = array.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			Boolean col = (Boolean) o.get("collectible");
			if (col != null && col == true /*
											 * && !"HERO".equals((String) o.get("type"))
											 */) {
				Card.CLASS classe;
				String c = (String) o.get("multiClassGroup");
				if (c == null) {
					c = (String) o.get("cardClass");
				}
				if (c == null) {
					c = (String) o.get("playerClass");
				}
				classe = Card.CLASS.valueOf(c);

				// TODO card mechanics??
				// List<String> mechs = (List<String>) o.get("mechanics");

				String text = (String) o.get("text");
				try {
					if (text != null) {
						text = new String(text.getBytes("ISO-8859-1"), "UTF-8");
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JSONArray reftags = (JSONArray) o.get("referencedTags");
				JSONArray mechanics = (JSONArray) o.get("mechanics");

				cards.add(new Card((String) o.get("id"), (String) o.get("name"), (String) o.get("set"),
						(String) o.get("race"), classe, (String) o.get("type"), text, (Long) o.get("cost"),
						(Long) o.get("attack"), (Long) o.get("health"), (Long) o.get("durability"),
						(String) o.get("rarity"), reftags == null ? "" : reftags.toString(),
						mechanics == null ? "" : mechanics.toString()));
			}
		}
		// if (getCard("The Coin") == null)
		{
			// TODO adiciona a moeda
			cards.add(new Card("game_005", "the coin", "CORE", "ALLIANCE", CLASS.NEUTRAL, "SPELL",
					"Add 1 mana this turn...", 0L, null, null, null, "COMMON", "", ""));
		}
		Collections.sort(cards);
	}

	/**
	 * Find a card by name or id.
	 * 
	 * @param idORname
	 * @return Card.
	 */
	public static Card getCard(String idORname) {
		if (idORname != null && !"".equals(idORname)) {
			for (Card c : cards) {
				if (c.getName().equalsIgnoreCase(idORname.trim().replaceAll("’", "'"))) {
					return c;
				}
				if (c.getId().equalsIgnoreCase(idORname)) {
					return c;
				}
				if (idORname.equalsIgnoreCase(c.getNumid())) {
					return c;
				}
			}
		}
		// TODO CS2_013t excess mana not found..
		throw new RuntimeException("Carta não encontrada: " + idORname);
		// return null;
	}

	/**
	 * Generate all cards sinergies.
	 */
	private void generateCardsSynergies() {
		System.out.println("generateCardsSynergies...");
		long ini = System.currentTimeMillis();

		FileWriter fw = null;
		try {
			fw = new FileWriter("cardSinergies.csv");
			for (Sinergy<Tag> tagSin : tb.getTagsSynergies()) {
				Tag tag1 = (Tag) tagSin.getE1();
				Tag tag2 = (Tag) tagSin.getE2();
				for (Card c1 : cards) {
					if (c1.getTags().contains(tag1)) {
						for (Card c2 : cards) {
							if (c2.getTags().contains(tag2)) {
								Sinergy<Card> cardSin = getCardSinergy(c1, c2);
								if (cardSin == null) {
									cardSin = new Sinergy<Card>(c1, c2, tagSin.getWeight(),
											tag1.getRegex() + "+" + tag2.getRegex());
									cardsSynergies.add(cardSin);
									fw.write(c1.getName() + "\t" + tag1.getName() + "\t" + c2.getName() + "\t"
											+ tag2.getName() + "\r\n");
								} else {
									cardSin.setWeight(cardSin.getWeight() + tagSin.getWeight());
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		imprimSins();

		// Collections.sort(Sinergias.cardsSynergies);
		System.out.println(cardsSynergies.size() + " sinergies calculated from parsed card texts in "
				+ (System.currentTimeMillis() - ini) / 60000 + " minutes.");
	}

	private void generateCardSynergies(Card c) {
		if (!c.isCalculada()) {
			for (Sinergy<Tag> ts : tb.getTagsSynergies()) {
				Tag tag1 = (Tag) ts.getE1();
				Tag tag2 = (Tag) ts.getE2();
				if (c.getTags().contains(tag1)) {
					for (Card c2 : cards) {
						if (c2.getTags().contains(tag2)) {
							Sinergy<Card> cs = getCardSinergy(c, c2);
							if (cs == null) {
								cs = new Sinergy<Card>(c, c2, ts.getWeight(), tag1.getRegex() + "+" + tag2.getRegex());
								cardsSynergies.add(cs);
							} else {
								cs.setWeight(cs.getWeight() + ts.getWeight());
							}
						}
					}
				}
			}
			// Collections.sort(Sinergias.cardsSynergies);
			c.setCalculada(true);
		}
	}

	/**
	 * Find sinergy between 2 Cards.
	 * 
	 * @param e1 Card 1
	 * @param e2 Card 2
	 * @return Return the sinergy with those Cards.
	 */
	// TODO retornar lista de sinergias, não apenas uma.
	private Sinergy<Card> getCardSinergy(Card e1, Card e2) {
		for (Sinergy<Card> s : cardsSynergies) {
			if ((e1 == s.getE1() && e2 == s.getE2()) || (e1 == s.getE2() && e2 == s.getE1())) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Read sinergy cached file.
	 */
	private void readSynergies() {
		JSONParser parser = new JSONParser();
		try {
			JSONArray sets = (JSONArray) parser
					.parse(new FileReader(new File(cl.getResource("sinergy/synergy.json").getFile())));
			System.out.println(sets.size() + " synergies imported");
			generateNumIds(sets);
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateNumIds(JSONArray sets) {
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
						cardsSynergies.add(new Sinergy<Card>(c, c2, value, ""));
					}
				}
			}
		}
	}

	private void printCard(String n) {
		Card card = getCard(n);
		for (Mechanic m : card.getMechanics()) {
			System.out.println(m.getRegex());
		}
	}

	/**
	 * Read sinergies from cache file.
	 */
	private void readCardSinergies() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(cl.getResource("sinergias.csv").getFile()));
		} catch (FileNotFoundException e) {
			// TODO deve gera-lo...
			e.printStackTrace();
		}
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split("\t");
			Card c1 = getCard(line[0]);
			Card c2 = getCard(line[1]);
			int freq = Integer.parseInt(line[2]);
			float val = Float.parseFloat(line[3]);
			String mech = line[4];
			if (c1 != null && c2 != null) {
				cardsSynergies.add(new Sinergy<Card>(c1, c2, freq, val, mech));
			}
		}
		sc.close();
		System.out.println(cardsSynergies.size() + " pre calculated sinergies loaded.");
	}

	private void imprimSins() {
		// Collections.sort((List<Sinergy<Card>>) cardsSynergies);
		StringBuilder sb = new StringBuilder();
		for (Sinergy<Card> s : cardsSynergies) {
			String line = s.getE1() + "\t" + s.getE2() + "\t" + s.getFreq() + "\t" + s.getWeight() + "\t" + s.getMechs();
			sb.append(line + "\r\n");
			System.out.println(line);
		}
		// EscreveArquivo.escreveArquivo("sinergias.csv", sb.toString());
		PrintWriter out = null;
		try {
			out = new PrintWriter(new File(cl.getResource("sinergias.csv").getFile()));
			out.println(sb.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}

	/**
	 * Calculate pairs of sinergies from game files.
	 */
	private void geraSinergias(JSONArray games) {
		Iterator<JSONObject> iterator = games.iterator();
		while (iterator.hasNext()) {
			JSONObject game = iterator.next();
			Iterator<JSONObject> card_history = ((JSONArray) game.get("card_history")).iterator();
			Card myprev = null, opoprev = null, myatual = null, opoatual = null;
			while (card_history.hasNext()) {
				JSONObject hist = card_history.next();
				JSONObject card = (JSONObject) hist.get("card");
				String id = (String) card.get("id");
				String player = (String) hist.get("player");
				if ("me".equals(player)) {
					if (myprev == null) {
						myprev = getCard(id);
						continue;
					}
					myatual = getCard(id);
					if (myatual != null) {
						Sinergy<Card> s = getCardSinergy(myprev, myatual);
						if (s == null) {
							s = new Sinergy<Card>(myprev, myatual, 1);
							cardsSynergies.add(s);
						}
						s.setWeight(s.getWeight() + 1);
					}
				} else if ("opponent".equals(player)) {
					if (opoprev == null) {
						opoprev = getCard(id);
						continue;
					}
					opoatual = getCard(id);
					if (opoatual != null) {
						Sinergy<Card> s = getCardSinergy(opoprev, opoatual);
						if (s == null) {
							s = new Sinergy<Card>(opoprev, opoatual, 1);
							cardsSynergies.add(s);
						}
						s.setWeight(s.getWeight() + 1);
					}
				}
			}
		}
	}

	/**
	 * Calculate possible cards sinergy.
	 * 
	 * @param c
	 * @param currentMana Mana restante no turno atual.
	 * @return Set of Cards with sinergy.
	 */
	// TODO here or in DeckBuilder?
	public Set<Card> provaveis(Card c, int currentMana, CLASS hsClass) {
		// Set<Sinergia> sub = new LinkedHashSet<Sinergia>();
		Set<Card> sub = new LinkedHashSet<Card>();
		if (c != null) {
			for (Sinergy<Card> s : cardsSynergies) {
				if (s.getE1() == c || s.getE2() == c) {
					Card c2 = (Card) s.getE2();
					if (c == c2) {
						c = (Card) s.getE1();
					}
					// cartas com sinergia com custo provavel no turno
					if (CLASS.contem(hsClass, c2.getClasse()) && c2.getCost() <= currentMana) {
						sub.add(c2);
						System.out.println(c2 + "\t" + s.getWeight() + "\t" + s.getMechs());
					}
				}
			}
		}
		return sub;
	}

	/**
	 * Calcula as provaveis jogadas.
	 * 
	 * @param c
	 * @param manaRestante Mana restante no turno atual.
	 * @return
	 */
	public Set<Sinergy<Card>> getCardSinergies(Card c, int manaRestante, CLASS opo) {
		Set<Sinergy<Card>> sub = new LinkedHashSet<Sinergy<Card>>();
		// Set<Carta> sub = new LinkedHashSet<Carta>();
		if (c != null) {
			for (Sinergy<Card> s : cardsSynergies) {
				if (s.getE1() == c || s.getE2() == c) {
					Card c2 = (Card) s.getE2();
					if (c == c2) {
						c = (Card) s.getE1();
					}
					// cartas com sinergia com custo provavel no turno
					if (CLASS.contem(opo, c2.getClasse()) && c2.getCost() <= manaRestante) {
						sub.add(s);
						System.out.println(c2 + "\t" + s.getWeight() + "\t" + s.getMechs());
					}
				}
			}
		}
		return sub;
	}

	public static void main(String[] args) {
		CardBuilder cb = new CardBuilder();
		for (Card c : cards) {
			for (Tag t : c.getTags()) {
				System.out.println(c + " TAG: " + t);
			}
		}
	}
}