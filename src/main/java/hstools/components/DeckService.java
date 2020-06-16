package hstools.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hstools.deckstrings.VarInt;
import hstools.model.Card;
import hstools.model.Deck;
import hstools.model.Deck.Formato;
import hstools.model.SynergyEdge;
import lombok.Getter;

/**
 * Partindo duma lista de cartas, tenta encontrar o metadeck as quais pertencem.
 * 
 * @author egrohs
 *
 */
@Service
public class DeckService {
	@Autowired
	private CardService cb;
	@Getter
	private Set<Deck> decks = new LinkedHashSet<Deck>();

	public void loadProDecks() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File("src/main/resources/decks/pro/pro.txt"));
			while (sc.hasNextLine()) {
				String deckstr = sc.nextLine();
				//System.out.println(deckstr);
				decks.add(decode(deckstr));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}

	/**
	 * Load meta decks.
	 */
	public void loadDecks(File dir) {
		File listOfFiles[] = dir.listFiles();
//		Arrays.sort(listOfFiles, new Comparator<File>() {
//			@Override
//			public int compare(File o1, File o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				loadDecks(file);
			} else {
				Map<Card, Integer> cartas = new LinkedHashMap<Card, Integer>();
				try {
					Scanner sc = new Scanner(file);
					String obs = null;
					while (sc.hasNextLine()) {
						// Apenas para ceitar ctrl-c-v do
						// http://www.hearthstonetopdecks.com
						String line = sc.nextLine().replaceAll("﻿", "").replaceAll("�", "'")
								.replaceFirst("^(\\d+)(\\w)", "$1\t$2").replaceFirst("(\\w)(\\d+)$", "$1\t$2");// .toLowerCase();
						if (line.startsWith("#")) {
							obs = line.substring(1, line.length());
						} else {
							String[] vals = line.split("\t");
							int i = 0;
							if (vals.length == 1)
								vals = line.split(";");
							if (vals.length > 2)
								i = 1;
							if (vals.length > 1 && !"".equals(vals[i]) && !"".equals(vals[i + 1])) {
								try {
									cartas.put(cb.getCard(vals[i]), Integer.parseInt(vals[i + 1]));
								} catch (Exception rt) {
									cartas.put(cb.getCard(vals[i + 1]), Integer.parseInt(vals[i]));
								}
							}
						}
					}
					Deck deck = new Deck(file.getName(), cartas);
					deck.setArchtype(obs);
					decks.add(deck);
					sc.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(decks.size() + " decks loaded.");
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
			Card c = cb.getCard(cardname);
			for (SynergyEdge<Card> s : cb.getCardSinergies(c, 10, classe)) {
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
	 *
	 * @param data the base64 decoded data. This method intentionnaly does not
	 *             decode Base64 as implementation differ greatly between android
	 *             and other JVM. See https://github.com/auth0/java-jwt/issues/131
	 *             for more details
	 * @return
	 * @throws Exception
	 */
	// TODO calcular a expansao e epoca do deck pelas suas cartas mais recentes
	public Deck decode(String encodedString) {
		byte[] data = Base64.getDecoder().decode(encodedString);
		// String decodedString = new String(data);
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		byteBuffer.get(); // reserverd
		int version = byteBuffer.get();
		if (version != 1) {
			// throw new ParseException("bad version: " + version);
		}

		Formato formato = Formato.getByValor(VarInt.getVarInt(byteBuffer));
		Map<Card, Integer> cartas = new HashMap<>();
//		if (result.format != FT_STANDARD && result.format != FT_WILD) {
//           throw new ParseException("bad format: " + result.format);
//		}

		int heroCount = VarInt.getVarInt(byteBuffer);
		// result.heroes = new ArrayList<>();
		for (int i = 0; i < heroCount; i++) {
			// result.heroes.add(VarInt.getVarInt(byteBuffer));
			// TODO pegar instancia do cardbuilder
			cartas.put(cb.getCard(String.valueOf(VarInt.getVarInt(byteBuffer))), 1);
		}

		for (int i = 1; i <= 3; i++) {
			int c = VarInt.getVarInt(byteBuffer);
			for (int j = 0; j < c; j++) {
				int dbfId = VarInt.getVarInt(byteBuffer);
				int count;
				if (i == 3) {
					count = VarInt.getVarInt(byteBuffer);
				} else {
					count = i;
				}
				cartas.put(cb.getCard(String.valueOf(dbfId)), count);
			}
		}
		Deck deck = new Deck("", cartas);
		deck.setFormato(formato);
		return deck;
	}
}