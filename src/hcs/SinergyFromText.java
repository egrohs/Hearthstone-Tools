package hcs;

import java.io.FileReader;
import java.io.IOException;
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

//TODO guardar as sinergias calculadas em arquivo, para evitar lentid�o ao rodar.
public class SinergyFromText {
	// static List<Carta> cards = new ArrayList<Carta>();
	// private static int cont = 0;
	private static TGFParser tgfp;
	// private static List<Card> deck = new ArrayList<Card>();

	public static void main(String[] args) {
		new SinergyFromText();
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
				Sinergia s = Sinergias.getSinergy(c1, c2);
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

	public SinergyFromText() {
		// buildMechanics();
		// cards = Universo.leCards();
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
			for (Sinergia s : Sinergias.cardsSynergies) {
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
	 * @param card
	 *            Carta consultada.
	 */
	private static void printCardSynergies(Carta card) {
		List<Sinergia> minhaS = Sinergias.getSinergias(card, 10, card.classe);
		Collections.sort(minhaS);
		for (Sinergia s : minhaS) {
			System.out.println(s.e1.name + "\t");
		}
	}

	/**
	 * Le arquivo de sinergias da web.
	 */
	private static void readSynergies() {
		JSONParser parser = new JSONParser();
		try {
			JSONArray sets = (JSONArray) parser.parse(new FileReader("res/sinergy/synergy.json"));
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
						Sinergias.cardsSynergies.add(new Sinergia(c, c2, value, ""));
					}
				}
			}
		}
	}

	/**
	 * Lê os textos das cartas, gerando suas listas de sinergia.
	 */
	private static void parseCardsText2Mechanics() {
		// List<Sinergia> cardSynergies = new ArrayList<Sinergia>();
		for (Mecanica m : TGFParser.mechanics.values()) {
			for (Carta c : Universo.cards) {
				if (Pattern.compile(m.regex).matcher(c.text).find()) {
					c.mechanics.add(m);
				}
			}
		}
		// generateCardsSynergies();
	}

	public static void generateCardSynergies(Carta c) {
		if (!c.calc) {
			for (Sinergia s : TGFParser.mechanicsSynergies) {
				Mecanica m1 = (Mecanica) s.e1;
				Mecanica m2 = (Mecanica) s.e2;
				// for (Carta c : Universo.cards)
				{
					if (c.mechanics.contains(m1)) {
						for (Carta c2 : Universo.cards) {
							if (c2.mechanics.contains(m2)) {
								Sinergia ss = Sinergias.getSinergy(c, c2);
								if (ss == null) {
									ss = new Sinergia(c, c2, s.valor, m1.regex + "+" + m2.regex);
									Sinergias.cardsSynergies.add(ss);
								} else {
									ss.valor += s.valor;
								}
							}
						}
					}
				}
			}
			Collections.sort(Sinergias.cardsSynergies);
			c.calc = true;
		}
	}

	/**
	 * Gera as sinergias de todas as cartas.
	 */
	private static void generateCardsSynergies() {
		long ini = System.currentTimeMillis();
		for (Sinergia s : TGFParser.mechanicsSynergies) {
			Mecanica m1 = (Mecanica) s.e1;
			Mecanica m2 = (Mecanica) s.e2;
			for (Carta c : Universo.cards) {
				if (c.mechanics.contains(m1)) {
					for (Carta c2 : Universo.cards) {
						if (c2.mechanics.contains(m2)) {
							Sinergia ss = Sinergias.getSinergy(c, c2);
							if (ss == null) {
								ss = new Sinergia(c, c2, s.valor, m1.regex + "+" + m2.regex);
								Sinergias.cardsSynergies.add(ss);
							} else {
								ss.valor += s.valor;
							}
						}
					}
				}
			}
		}
		System.out.println(System.currentTimeMillis() - ini);
		Collections.sort(Sinergias.cardsSynergies);
		System.out.println(Sinergias.cardsSynergies.size() + " sinergies calculated from parsed card texts.");
	}

	private static void printQntMAffinities() {
		for (Mecanica m : TGFParser.mechanics.values()) {
			int cont = 0;
			// System.out.println(m.regex + "\t" + m.aff.size());
			for (Carta card : Universo.cards) {
				if (card.mechanics.contains(m)) {
					cont++;
				}
			}
			System.out.println(m.regex + "\t" + cont);
		}
	}
}