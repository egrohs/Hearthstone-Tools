package hcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Decks {
	static Set<Deck> decks = new HashSet<Deck>();

	public static void main(String[] args) {
		Universo.leCards();
		Decks.leDecks();
		// for (Deck d : Decks.decks) {
		// System.out.println(d);
		// }
		Decks.similaridade(new String[] { "N'Zoth's First Mate", "FIERY WAR AXE", "AZURE DRAKE" });
	}

	private static void similaridade(String[] cartas) {
		Map<Deck, Double> prob = new HashMap<Deck, Double>();
		for (Deck deck : decks) {
			for (String c : cartas) {
				Integer qnt = deck.getQnt(c);
				if (qnt != null && qnt > 0) {
					prob.put(deck, prob.get(deck) == null ? 1d : prob.get(deck) + 1);
					// break;
				}
			}
			System.out.println(deck.nome + ": " + prob.get(deck));
		}
	}

	/**
	 * Carrega os decks em memória.
	 */
	private static void leDecks() {
		File folder = new File("metadecks");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			Map<Carta, Integer> cartas = new HashMap<Carta, Integer>();
			try {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
					String line = sc.nextLine().toLowerCase();
					String[] vals = line.split("\t");
					if (!"".equals(vals[0]) && !"".equals(vals[1])) {
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
}

class Deck {
	String nome;
	Carta.CLASS classe;
	Map<Carta, Integer> cartas = new HashMap<Carta, Integer>();

	public Deck(String nome, Map<Carta, Integer> cartas) {
		this.nome = nome;
		this.cartas = cartas;
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
