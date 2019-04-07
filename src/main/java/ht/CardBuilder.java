package ht;

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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ht.model.Card;
import ht.model.Card.CLASS;
import ht.model.SynergyEdge;
import ht.model.Tag;

public class CardBuilder {
	public static List<Card> cards = new ArrayList<Card>();
	List<SynergyEdge<Card>> cardsSynergies = new ArrayList<SynergyEdge<Card>>();
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
			System.out.println(cards.size() + " cards imported.");
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
				if (c.getCod().equalsIgnoreCase(idORname)) {
					return c;
				}
				if (idORname.equalsIgnoreCase(c.getNumid())) {
					return c;
				}
			}
		}
		// TODO CS2_013t excess mana not found..
		throw new RuntimeException("Card not found: " + idORname);
		// return null;
	}

	/**
	 * Write synergies on csv edges file gephi format.
	 */
	private void writeCardSynergies() {
		FileWriter fw = null;
		try {
			fw = new FileWriter("cardSinergies.csv");
			fw.write("Source\tTarget\tm1\tm2\r\n");
			for (SynergyEdge<Card> s : cardsSynergies) {
				Card c1 = (Card) s.getE1();
				Card c2 = (Card) s.getE2();
				fw.write(c1.getName() + "\t" + c2.getName() + "\r\n");
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
	}

	/**
	 * Generate all cards synergies.
	 */
	private void generateCardsSynergies() {
		System.out.println("generateCardsSynergies...");
		long ini = System.currentTimeMillis();
		for (Card c1 : cards) {
			generateCardSynergies(c1, false);
		}
		// imprimSins();

		// Collections.sort(Sinergias.cardsSynergies);
		System.out.println(cardsSynergies.size() + " sinergies calculated from parsed card texts in "
				+ (System.currentTimeMillis() - ini) / 60000 + " minutes.");
	}

	/**
	 * Generate all synergies for card c1
	 * 
	 * @param everyCard if true, generate all synergies, class independ.
	 */
	public void generateCardSynergies(Card c1, boolean everyCard) {
		if (c1 != null && !c1.isCalculada()) {
			for (SynergyEdge<Tag> ts : tb.getTagsSynergies()) {
				Tag tag = null;
				Tag tag1 = (Tag) ts.getE1();
				Tag tag2 = (Tag) ts.getE2();
				if (c1.getTags().contains(tag1)) {
					tag = tag2;
				} else if (c1.getTags().contains(tag2)) {
					tag = tag1;
				}
				if (tag != null) {
					for (Card c2 : cards) {
						if ((everyCard || c2.getClasse() == CLASS.NEUTRAL || c1.getClasse() == c2.getClasse())
								&& c2.getTags().contains(tag)) {
							SynergyEdge<Card> cs = new SynergyEdge<Card>(c1, c2,
									c2.getText() + "\t" + tag1.getRegex() + " + " + tag2.getRegex(), ts.getWeight());
							cardsSynergies.add(cs);

							System.out.println(cs);
						}
					}
				}
			}
			// Collections.sort(Sinergias.cardsSynergies);
			c1.setCalculada(true);
		}
	}

	/**
	 * Find synergy between 2 Cards.
	 * 
	 * @param e1 Card 1
	 * @param e2 Card 2
	 * @return Return the synergy with those Cards.
	 */
	// TODO retornar lista de sinergias, não apenas uma.
	private SynergyEdge<Card> getCardSynergy(Card e1, Card e2) {
		for (SynergyEdge<Card> s : cardsSynergies) {
			if ((e1 == s.getE1() && e2 == s.getE2()) || (e1 == s.getE2() && e2 == s.getE1())) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Read synergy cached file.
	 */
	private void readSynergies() {
		JSONParser parser = new JSONParser();
		try {
			JSONArray sets = (JSONArray) parser
					.parse(new FileReader(new File(cl.getResource("synergy/synergy.json").getFile())));
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
						cardsSynergies.add(new SynergyEdge<Card>(c, c2, value, ""));
					}
				}
			}
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
				cardsSynergies.add(new SynergyEdge<Card>(c1, c2, freq, val, mech));
			}
		}
		sc.close();
		System.out.println(cardsSynergies.size() + " pre calculated sinergies loaded.");
	}

	private void imprimSins() {
		// Collections.sort((List<SynergyEdge<Card>>) cardsSynergies);
		StringBuilder sb = new StringBuilder();
		for (SynergyEdge<Card> s : cardsSynergies) {
			String line = s.getE1() + "\t" + s.getE2() + "\t" + s.getFreq() + "\t" + s.getWeight() + "\t"
					+ s.getMechs();
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
						SynergyEdge<Card> s = getCardSynergy(myprev, myatual);
						if (s == null) {
							s = new SynergyEdge<Card>(myprev, myatual, 1);
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
						SynergyEdge<Card> s = getCardSynergy(opoprev, opoatual);
						if (s == null) {
							s = new SynergyEdge<Card>(opoprev, opoatual, 1);
							cardsSynergies.add(s);
						}
						s.setWeight(s.getWeight() + 1);
					}
				}
			}
		}
	}

	/**
	 * Calculate possible cards synergy.
	 * 
	 * @param c
	 * @param currentMana Available Mana.
	 * @return Set of Cards with synergy.
	 */
	// TODO here or in DeckBuilder?
	public Set<Card> provaveis(Card c, int currentMana, CLASS hsClass) {
		// Set<Sinergia> sub = new LinkedHashSet<Sinergia>();
		Set<Card> sub = new LinkedHashSet<Card>();
		if (c != null) {
			for (SynergyEdge<Card> s : cardsSynergies) {
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
	public Set<SynergyEdge<Card>> getCardSinergies(Card c, int manaRestante, CLASS opo) {
		Set<SynergyEdge<Card>> sub = new LinkedHashSet<SynergyEdge<Card>>();
		// Set<Carta> sub = new LinkedHashSet<Carta>();
		if (c != null) {
			for (SynergyEdge<Card> s : cardsSynergies) {
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
		// Card c = new Card("Teste");
		CardBuilder cb = new CardBuilder();
		Card c1 = CardBuilder.getCard("Zilliax");
		cb.generateCardSynergies(c1, false);
		cb.writeCardSynergies();
	}
}