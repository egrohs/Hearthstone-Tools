package hcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import hcs.Carta.CLASS;

/**
 * Partindo duma lista de cartas, tenta encontrar o metadeck as quais pertencem.
 * 
 * @author egrohs
 *
 */
public class DeckFinder {
	static Set<Deck> decks = new HashSet<Deck>();

	public static void main(String[] args) {
		Universo.leCards();
		DeckFinder.leDecks();
		// for (Deck d : Decks.decks) {
		// System.out.println(d);
		// }
		DeckFinder.similaridade(new String[] { "N'Zoth's First Mate", "FIERY WAR AXE", "AZURE DRAKE" });
	}

	public DeckFinder() {
		DeckFinder.leDecks();
	}

	public static Map<Deck, Double> similaridade(Set<Carta> searched) {
		List<String> nomes = new ArrayList<String>();
		for (Carta carta : searched) {
			nomes.add(carta.name);
		}
		return similaridade(nomes.toArray(new String[30]));
	}

	public static Map<Deck, Double> similaridade(String[] cartas) {
		Map<Deck, Double> prob = new HashMap<Deck, Double>();
		System.out.println("----------------------");
		for (Deck deck : decks) {
			int cont = 0;
			for (String c : cartas) {
				Integer qnt = deck.getQnt(c);
				if (qnt != null && qnt > 0) {
					cont += qnt;
					double p = Math.round(10000.0 * cont / 30.0) / 100.0;
					prob.put(deck, p);
				}
			}
			if (prob.get(deck) != null) {
				System.out.println(deck.nome + ": " + prob.get(deck));
			}
		}
		return prob;
	}

	/**
	 * Carrega os decks em memória.
	 */
	public static void leDecks() {
		File folder = new File("res/metadecks");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			Map<Carta, Integer> cartas = new HashMap<Carta, Integer>();
			try {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
					String line = sc.nextLine().toLowerCase();
					String[] vals = line.split("\t");
					if (vals.length > 1 && !"".equals(vals[0]) && !"".equals(vals[1])) {
						try {
							cartas.put(Universo.getCard(vals[0]), Integer.parseInt(vals[1]));
						} catch (Exception rt) {
							cartas.put(Universo.getCard(vals[1]), Integer.parseInt(vals[0]));
						}
					}
				}
				decks.add(new Deck(file.getName(), cartas));
				sc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
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

class Deck {
	String nome;
	Carta.CLASS classe = CLASS.NEUTRAL;
	Map<Carta, Integer> cartas = new HashMap<Carta, Integer>();

	public Deck(String nome, Map<Carta, Integer> cartas) {
		this.nome = nome;
		this.cartas = cartas;
		this.classe = Universo.whichClass(cartas.keySet());
	}

	public Integer getQnt(String nome) {
		Carta c = Universo.getCard(nome);
		if (cartas.containsKey(c)) {
			return cartas.get(c);
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(nome + "\r\n");
		sb.append(classe + "\r\n");
		for (Carta c : cartas.keySet()) {
			sb.append(c.name + "\t" + cartas.get(c) + "\r\n");
		}
		return sb.toString();
	}
}
