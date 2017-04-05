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
		printDeck(buildDeck(Carta.CLASS.SHAMAN, new String[] { "Tunnel Trogg" }, new HashSet<Carta>(), 0));
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
			System.out.println(acum / cont + "\t" + c1.name + "\t" + c1.classe + "\t" + c1.text);
		}
	}

	private static void printM2M() {
		for (Sinergia s : TGFParser.mechanicsSynergies) {
			System.out.println(((Mecanica) s.e1).regex + "\t" + ((Mecanica) s.e2).regex);
		}
	}

	private static void init() {
		// buildMechanics();
		cards = Universo.leCards();
		// printCards();
		tgfp = new TGFParser();
		// buildCards();
		parseCardsText2Mechanics();
		// generateCardSynergies();
	}

	/**
	 * Gera lista de cartas que tem sinergia com as cartas informadas.
	 * 
	 * @param classe
	 *            Limita as classes de cartas que podem entrar na lista.
	 * @param initialCards
	 *            Cartas para se verificar sinergia com.
	 * @param deck
	 *            Lista de saida?!
	 * @param depth
	 *            Limita profundidade de busca no grafo das sinergias.
	 * @return Lista de cartas com sinergia às informadas.
	 */
	private static Set<Carta> buildDeck(Carta.CLASS classe, String[] initialCards, Set<Carta> deck, int depth) {
		System.out.println("Sinergias para " + initialCards[0]);
		for (String cardname : initialCards) {
			Carta c = Universo.getCard(cardname);
			for (Sinergia s : cardSynergies) {
				Carta c1 = (Carta) s.e1;
				Carta c2 = (Carta) s.e2;
				if (c == c1 || c == c2) {
					if (Carta.CLASS.contem(classe, c1.classe) || Carta.CLASS.contem(classe, c2.classe)) {
						deck.add(c1);
						deck.add(c2);
					}
				}
			}
		}
		return deck;
	}

	private static void printCard(String n) {
		Carta card = Universo.getCard(n);
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
		Carta card = Universo.getCard(cardName);
		List<Sinergia> minhaS = new ArrayList<>();
		for (Sinergia s : cardSynergies) {
			if (s.e1 == card || s.e2 == card) {
				minhaS.add(s);
				// + s.e1.classe +
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

	/**
	 * Apos arquivo de sinergias lido, gera a lista de sinergias.
	 * 
	 * @param sets
	 */
	private static void generateNumIds(JSONArray sets) {
		Iterator<JSONObject> iterator = sets.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			String id = (String) o.get("id");
			String numid = (String) o.get("numid");
			Carta c = Universo.getCard(id);
			c.setNumid(numid);

		}
		iterator = sets.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			String id = (String) o.get("id");
			Carta c = Universo.getCard(id);
			JSONArray sin = (JSONArray) o.get("synergies");
			if (sin != null) {
				Iterator<JSONArray> iterator2 = sin.iterator();
				while (iterator2.hasNext()) {
					JSONArray o2 = iterator2.next();
					Carta c2 = Universo.getCard((String) o2.get(0));
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

	private static void printQntMAffinities() {
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