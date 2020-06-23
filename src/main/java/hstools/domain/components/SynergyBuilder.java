package hstools.domain.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import hstools.domain.entities.Card;
import hstools.domain.entities.Card.CLASS;
import hstools.domain.entities.SynergyEdge;
import hstools.domain.entities.Tag;
import hstools.util.GoogleSheets;
import hstools.util.Util;
import lombok.Getter;

/**
 * Load tags and synergies from google sheets. Should it be on scrap or
 * datascience service?
 * 
 * @author EGrohs
 *
 */
@Component("Synergies")
@DependsOn(value = { "Cards" })
public class SynergyBuilder {
	@Autowired
	private CardComponent cardComp;
	@Getter
	private List<SynergyEdge<Card>> cardsSynergies = new ArrayList<SynergyEdge<Card>>();
	@Getter
	private List<SynergyEdge<Tag>> tagsSynergies = new ArrayList<SynergyEdge<Tag>>();
	private ClassLoader clsLoader = this.getClass().getClassLoader();
	
	public void loadCombos() {
		List<String[]> syns = Util.csv2CardSyns();
		for (String[] combo : syns) {
			for (int i = 0; i < combo.length; i++) {
				String cName1 = combo[i];
				for (int j = i; j < combo.length; j++) {
					String cName2 = combo[j];
					if (!cName1.isBlank() && !cName2.isBlank()) {
						Card c1 = cardComp.getCard(cName1);
						Card c2 = cardComp.getCard(cName2);
						cardsSynergies.add(new SynergyEdge<>(c1, c2, 1));
					}
				}
			}
		}
		// System.out.println(tags.size() + " tags loaded.");
	}

	// Depends on previous tagsSynergies calculated
	public List<SynergyEdge<Card>> sinergias(Card c1, boolean everyCard) {
		List<SynergyEdge<Card>> cardsSynergies = new ArrayList<SynergyEdge<Card>>();
		for (SynergyEdge<Tag> ts : tagsSynergies) {
			Tag tag = null;
			Tag tag1 = (Tag) ts.getE1();
			Tag tag2 = (Tag) ts.getE2();
			if (c1.getTags().contains(tag1)) {
				tag = tag2;
			} else if (c1.getTags().contains(tag2)) {
				tag = tag1;
			}
			if (tag != null) {
				for (Card c2 : cardComp.getCards()) {
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
		return cardsSynergies;
	}

	/**
	 * Generate all synergies for card c1
	 * 
	 * @param everyCard if true, generate all synergies, class independ.
	 */
	public List<SynergyEdge<Card>> generateCardSynergies(Card c1, boolean everyCard) {
		List<SynergyEdge<Card>> cardsSynergies = new ArrayList<SynergyEdge<Card>>();
		if (c1 != null && !c1.isCalculada()) {
			cardsSynergies.addAll(sinergias(c1, everyCard));
			// Collections.sort(Sinergias.cardsSynergies);
			c1.setCalculada(true);
		}
		return cardsSynergies;
	}

	/**
	 * Generate all cards synergies.
	 */
	private void generateCardsSynergies() {
		List<SynergyEdge<Card>> cardsSynergies = new ArrayList<SynergyEdge<Card>>();
		System.out.println("generateCardsSynergies...");
		long ini = System.currentTimeMillis();
		for (Card c1 : cardComp.getCards()) {
			cardsSynergies.addAll(generateCardSynergies(c1, false));
		}
		// imprimSins();

		// Collections.sort(Sinergias.cardsSynergies);
		System.out.println(cardsSynergies.size() + " sinergies calculated from parsed card texts in "
				+ (System.currentTimeMillis() - ini) / 60000 + " minutes.");
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
		JSONArray sets = (JSONArray) Util.file2JSONObject("src/main/resources/synergy/synergy.json");
		System.out.println(sets.size() + " synergies imported");
		generateNumIds(sets);
	}

	private void generateNumIds(JSONArray sets) {
		Iterator<JSONObject> iterator = sets.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			String id = (String) o.get("id");
			Card c = cardComp.getCard(id);
			JSONArray sin = (JSONArray) o.get("synergies");
			if (sin != null) {
				Iterator<JSONArray> iterator2 = sin.iterator();
				while (iterator2.hasNext()) {
					JSONArray o2 = iterator2.next();
					Card c2 = cardComp.getCard((String) o2.get(0));
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
			sc = new Scanner(new File(clsLoader.getResource("sinergias.csv").getFile()));
		} catch (FileNotFoundException e) {
			// TODO deve gera-lo...
			e.printStackTrace();
		}
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split("\t");
			Card c1 = cardComp.getCard(line[0]);
			Card c2 = cardComp.getCard(line[1]);
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

	public List<SynergyEdge<Card>> generateMatchesCardsSim() {
		JSONObject jo = (JSONObject) Util.file2JSONObject("src/main/resources/synergy/matchesCardHerthSim.json");
		JSONArray nodes = (JSONArray) jo.get("nodes"), links = (JSONArray) jo.get("links");
		List<SynergyEdge<Card>> cardSins = new ArrayList<SynergyEdge<Card>>();
		Iterator<JSONObject> iterator = links.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			try {
				Card source = cardComp.getCard((String) o.get("source"));
				Card target = cardComp.getCard((String) o.get("target"));
				cardSins.add(new SynergyEdge<Card>(source, target, 1));
			} catch (RuntimeException e) {
				// TODO: handle exception
			}
		}

//		iterator = nodes.iterator();
//		while (iterator.hasNext()) {
//			JSONObject o = iterator.next();
//			Card c = cb.getCard((String) o.get("id"));
//			if (c != null)
//				c.setSize((Double) o.get("radius"));
//		}
		return cardSins;
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
			out = new PrintWriter(new File(clsLoader.getResource("sinergias.csv").getFile()));
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
						myprev = cardComp.getCard(id);
						continue;
					}
					myatual = cardComp.getCard(id);
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
						opoprev = cardComp.getCard(id);
						continue;
					}
					opoatual = cardComp.getCard(id);
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
	public Set<Card> myPlays(Card c, int currentMana, CLASS hsClass) {
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
	public Set<SynergyEdge<Card>> opponentPlays(Card c, int manaRestante, CLASS opo) {
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

	/**
	 * Gera lista de cartas que tem sinergia com as cartas informadas.
	 * 
	 * @param classe       Limita as classes de cartas que podem entrar na lista.
	 * @param initialCards Cartas para se verificar sinergia com.
	 * @param deck         Lista de saida?!
	 * @param depth        Limita profundidade de busca no grafo das sinergias.
	 * @return Lista de cartas com sinergia às informadas.
	 */
	private Set<Card> buildDeck(Card.CLASS classe, String[] initialCards, Set<Card> deck, int depth) {
		System.out.println("Sinergias para " + initialCards[0]);
		for (String cardname : initialCards) {
			Card c = cardComp.getCard(cardname);
			for (SynergyEdge<Card> s : opponentPlays(c, 10, classe)) {
				Card c1 = (Card) s.getE1();
				Card c2 = (Card) s.getE2();
				if (c == c1 || c == c2) {
					if (Card.CLASS.contem(classe, c1.getClasse()) || Card.CLASS.contem(classe, c2.getClasse())) {
						deck.add(c1);
						deck.add(c2);
					}
				}
			}
		}
		return deck;
	}

	/**
	 * Import all card tags form google sheet
	 */
	public void importTagSinergies() {
		List<List<Object>> values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14",
				"TAG_EDGES!A2:D");
		for (List<Object> row : values) {
			String source = (String) row.get(0);
			String taget = row.size() > 1 ? (String) row.get(1) : "";
			String label = row.size() > 2 ? (String) row.get(2) : "";
			Float weight = row.size() > 3 ? (Float) row.get(3) : 0.0f;
			Tag t1 = cardComp.getTags().get(source);
			Tag t2 = cardComp.getTags().get(taget);
			if (t2 != null) {
				tagsSynergies.add(new SynergyEdge<Tag>(t1, t2, label, weight));
			}
		}
		for (Tag t1 : cardComp.getTags().values()) {
			if (t1.getRegex() != null && !"".equals(t1.getRegex())) {
				// Almost every tag synergies with itself.
				tagsSynergies.add(new SynergyEdge<Tag>(t1, t1, t1.getName(), 0.0f));
			}
		}
		System.out.println(tagsSynergies.size() + " tags-synergies imported.");
	}
}