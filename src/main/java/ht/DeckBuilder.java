package ht;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ht.model.Card;
import ht.model.Card.CLASS;
import ht.model.Deck;
import ht.model.Player;
import ht.model.Sinergy;

/**
 * Partindo duma lista de cartas, tenta encontrar o metadeck as quais pertencem.
 * 
 * @author egrohs
 *
 */
public class DeckBuilder {
	Set<Deck> decks = new HashSet<Deck>();
	private ClassLoader cl = this.getClass().getClassLoader();
	private CardBuilder cb;

	public static void main(String[] args) {
		CardBuilder cb = new CardBuilder();
		DeckBuilder db = new DeckBuilder();
		db.loadDecks(new File(db.cl.getResource("decks").getFile()));
		// for (Deck d : Decks.decks) {
		// System.out.println(d);
		// }
		db.similaridade(new String[] { "N'Zoth's First Mate", "FIERY WAR AXE", "AZURE DRAKE" });
	}

	public DeckBuilder() {
		cb = new CardBuilder();
		loadDecks(new File(cl.getResource("decks").getFile()));
		System.out.println(decks.size() + " decks loaded.");
	}

	private Map<Deck, Double> similaridade(Collection<Card> searched) {
		List<String> nomes = new ArrayList<String>();
		for (Card carta : searched) {
			// System.out.println("similaridade: " +carta.getName());
			nomes.add(carta.getName());
		}
		return similaridade(nomes.toArray(new String[searched.size()]));
	}

	private Map<Deck, Double> similaridade(String[] cartas) {
		Map<Deck, Double> prob = new HashMap<Deck, Double>();
		System.out.println("----------------------");
		for (Deck deck : decks) {
			int cont = 0;
			for (String c : cartas) {
				Integer qnt = deck.getQnt(c);
				if (qnt != null && qnt > 0) {
					cont += 1;
					double p = Math.round(10000.0 * cont / 30.0) / 100.0;
					prob.put(deck, p);
				}
			}
			if (prob.get(deck) != null) {
				System.out.println(deck.getName() + ": " + prob.get(deck));
			}
		}
		return prob;
	}

	/**
	 * Load meta decks.
	 */
	private void loadDecks(File dir) {
		// FileUtils.listFiles(dir, true, true);
		File listOfFiles[] = dir.listFiles();
		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				loadDecks(file);
			} else {
				Map<Card, Integer> cartas = new HashMap<Card, Integer>();
				try {
					Scanner sc = new Scanner(file);
					while (sc.hasNextLine()) {
						// Apenas para ceitar ctrl-c-v do
						// http://www.hearthstonetopdecks.com
						String line = sc.nextLine().replaceAll("�", "'").replaceFirst("^(\\d+)(\\w)", "$1\t$2")
								.replaceFirst("(\\w)(\\d+)$", "$1\t$2");// .toLowerCase();
						String[] vals = line.split("\t");
						int i = 0;
						if (vals.length == 1)
							vals = line.split(";");
						if (vals.length > 2)
							i = 1;
						if (vals.length > 1 && !"".equals(vals[i]) && !"".equals(vals[i + 1])) {
							try {
								cartas.put(CardBuilder.getCard(vals[i]), Integer.parseInt(vals[i + 1]));
							} catch (Exception rt) {
								cartas.put(CardBuilder.getCard(vals[i + 1]), Integer.parseInt(vals[i]));
							}
						}
					}
					Deck deck = new Deck(file.getName(), cartas);
					decks.add(deck);
					sc.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// TODO abordagem 2, por matrix de afinidade.
	double[][] affinity = new double[40][40];// iniciada com zeros

	private void matrix() {
		String card = null;
		// int ind = hunter_neutral.indexOf(card);
		for (Deck deck : decks) {
			for (int i = 0; i < affinity.length; i++) {
				for (int j = 0; j < i && j < affinity[i].length; j++) {
					// TODO origem e destino sao mesmo strings?
					String origem = "";// all.get(i);
					String destino = "";// all.get(j);
					if (Arrays.asList(deck).contains(origem) && Arrays.asList(deck).contains(destino)) {
						affinity[i][j]++;
						// affinity[j][i]++;
						// TODO ainda nao leva em conta a qnt das cartas
						// TODO ainda é apenas uma matrix triangular
					}
				}
			}
		}
	}

	/**
	 * Download web hearthstone decks.
	 */
	// function depth-limited-crawl(page p, int d)
	// if d == 0
	// return
	/// * do something with p, store it or so */
	// foreach (page l in links(p))
	// depth-limited-crawl(linked, d-1)

	// Currently supports:
	// - arenavalue.com
	// - arenamastery.com
	// - elitedecks.net
	// - gosugamers.net
	// - heartharena.com
	// - hearthbuilder.com
	// - hearthhead.com (including deckbuilder)
	// - hearthnews.fr
	// - hearthpwn.com (including deckbuilder)
	// - hearthstats.net / hss.io
	// - hearthstone-decks.com
	// - hearthstonechampion.com
	// - hearthstoneheroes.com
	// - hearthstoneplayers.com
	// - hearthstonetopdeck.com
	// - hearthstonetopdecks.com
	// - hearthstone.judgehype.com
	// - hs.inven.co.kr
	// - icy-veins.com
	// - ls.duowan.com
	// - netdeck.n4ru.it
	// - playhs.es
	// - powned.it
	// - pro.eslgaming.com
	// - teamarchon.com
	// - tempostorm.com
	public void hearthstonetopdecks() {
		try {
			String page = "1";
			String url = "http://www.hearthstonetopdecks.com/deck-category/type/page/" + page;
			// login(loginUrl, url);
			Document docDecks = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
					.timeout(3000).post();
			Elements decks = docDecks.select("tbody tr a");
			for (Element deck : decks) {
				String deckLink = deck.attr("href");
				// score : 'td:first-child',
				// deck : 'a@href',
				// classe : 'td:nth-child(3) span',
				// type : 'td:nth-child(5) a',
				Document docDeck = Jsoup.connect(deckLink).data("query", "Java").userAgent("Mozilla")
						.cookie("auth", "token").timeout(3000).post();
				Elements cards = docDeck.select("#deck-master a[data-tooltip-img]");
				for (Element card : cards) {
					String cost = card.select("span.card-cost").text();
					// cost : 'span.card-cost',
					// card : 'span.card-name',
					// count : 'span.card-count',
					System.out.println(cost);
				}
				System.out.println(deckLink);
				// TODO remove
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void login(String loginUrl, String url) throws IOException {
		// First login. Take the cookies
		Connection.Response res = Jsoup.connect(loginUrl).data("eid", "i110013").data("pw", "001")
				.referrer("http://www.google.com")
				.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				.method(Method.POST).timeout(0).execute();

		Map<String, String> loginCookies = res.cookies();

		// Now you can parse any page you want, as long as you pass the
		// cookies
		Document doc = Jsoup.connect(url).timeout(0).cookies(loginCookies).referrer("http://www.google.com")
				.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				.get();

		System.out.println("Title : " + doc.title());
	}

//	private void printDeck(Deck deck) {
//		for (Card c1 : deck.getCartas().keySet()) {
//			int cont = 0;
//			Float acum = 0f;
//			for (Card c2 : deck.getCartas().keySet()) {
//				if (!c1.equals(c2)) {
//					Sinergy<Card> s = cb.getCardSinergy(c1, c2);
//					acum += s != null ? s.getValor() : 0f;
//					cont++;
//				}
//			}
//			System.out.println(acum / cont + "\t" + c1.getName() + "\t" + c1.getClasse() + "\t" + c1.getText());
//		}
//	}

	/**
	 * Calcula a similariade de deck partindo de todas cartas jogadas ate agora.
	 * 
	 * @return
	 */
	private StringBuilder simi(Player opponent) {
		// calcula similaridade de deck.
		Map<Deck, Double> probs = similaridade(ZoneLogReader.playMap.values());
		StringBuilder sbb = new StringBuilder();
		for (Deck k : probs.keySet()) {
			// TODO buscar a classe do opo no log?
			if (k.getClasse() == opponent.getClasse()) {
				sbb.append(k.getName() + " = " + probs.get(k) + "%\n");
			}
		}
		return sbb;
	}

	public Set<Sinergy<Card>> getCardSinergies(Card card, int manaRestante, CLASS classe) {
		return cb.getCardSinergies(card, manaRestante, classe);
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
			Card c = CardBuilder.getCard(cardname);
			for (Sinergy<Card> s : cb.cardsSynergies) {
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
}