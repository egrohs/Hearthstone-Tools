package hcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import hcs.model.Carta;
import hcs.model.Deck;

/**
 * Partindo duma lista de cartas, tenta encontrar o metadeck as quais pertencem.
 * 
 * @author egrohs
 *
 */
public class DeckFinder {
	static Set<Deck> decks = new HashSet<Deck>();
	static ClassLoader classLoader = DeckFinder.class.getClassLoader();
	public static void main(String[] args) {
		Universo.leCards();
		DeckFinder.leDecks(new File(classLoader.getResource("decks").getFile()));
		// for (Deck d : Decks.decks) {
		// System.out.println(d);
		// }
		DeckFinder.similaridade(new String[] { "N'Zoth's First Mate", "FIERY WAR AXE", "AZURE DRAKE" });
	}

	public DeckFinder() {
		DeckFinder.leDecks(new File(classLoader.getResource("decks").getFile()));
	}

	public static Map<Deck, Double> similaridade(Collection<Carta> searched) {
		List<String> nomes = new ArrayList<String>();
		for (Carta carta : searched) {
			// System.out.println("similaridade: " +carta.getName());
			nomes.add(carta.getName());
		}
		return similaridade(nomes.toArray(new String[searched.size()]));
	}

	public static Map<Deck, Double> similaridade(String[] cartas) {
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
	 * Carrega os decks em memória.
	 */
	public static void leDecks(File dir) {
		// FileUtils.listFiles(dir, true, true);
		File listOfFiles[] = dir.listFiles();
		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				leDecks(file);
			} else {
				Map<Carta, Integer> cartas = new HashMap<Carta, Integer>();
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
								cartas.put(Universo.getCard(vals[i]), Integer.parseInt(vals[i + 1]));
							} catch (Exception rt) {
								cartas.put(Universo.getCard(vals[i + 1]), Integer.parseInt(vals[i]));
							}
						}
					}
					Deck deck = new Deck(file.getName(), cartas);
					decks.add(deck);
					System.out.println(deck.getName() + " deck loaded.");
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
}
