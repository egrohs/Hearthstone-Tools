package hcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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

import hcs.model.Card;
import hcs.model.Card.CLASS;
import hcs.model.Sinergy;

/**
 * Base de dados de jogos em json HS, para analise estatistica.
 * 
 * @author egrohs
 *
 */
public class SinergyFromGames {
	// TODO LinkedHashSet???
	// static Set<Sinergia> sinergias = new HashSet<Sinergia>();
	// static Sinergias sinergias = new Sinergias();
	static JSONArray games = new JSONArray();

	public static void main(String[] args) {
		Universo.leCards();
		leJogos();
		// leSinergias();
		provaveis(Universo.getCard("entomb"), 1, CLASS.PRIEST);
	}

	public SinergyFromGames() {
		// leJogos(); //muito lento...
	}

	/**
	 * Calcula as provaveis jogadas.
	 * 
	 * @param c
	 * @param manaRestante
	 *            Mana restante no turno atual.
	 * @return
	 */
	public static Set<Card> provaveis(Card c, int manaRestante, CLASS opo) {
		// Set<Sinergia> sub = new LinkedHashSet<Sinergia>();
		Set<Card> sub = new LinkedHashSet<Card>();
		if (c != null) {
			for (Sinergy s : Sinergias.cardsSynergies) {
				if (s.getE1() == c || s.getE2() == c) {
					Card c2 = (Card) s.getE2();
					if (c == c2) {
						c = (Card) s.getE1();
					}
					// cartas com sinergia com custo provavel no turno
					if (CLASS.contem(opo, c2.getClasse()) && c2.getCost() <= manaRestante) {
						sub.add(c2);
						System.out.println(c2 + "\t" + s.getValor() + "\t" + s.getMechs());
					}
				}
			}
		}
		return sub;
	}

	/**
	 * Le jogos (usar -Xmx1300m).
	 */
	private static void leJogos() {
		// TODO ler do site http://www.hearthscry.com/CollectOBot
		JSONParser parser = new JSONParser();
		File folder = new File(Universo.cl.getResource("jogos").getFile());
		//File folder = new File("res/jogos");
		File[] listOfFiles = folder.listFiles();
		FileReader fr = null;
		JSONObject jo = null;
		for (File file : listOfFiles) {
			System.out.println("Reading " + file.getName() + "...");
			try {
				fr = new FileReader(file);
				jo = (JSONObject) parser.parse(fr);
				games.addAll((JSONArray) jo.get("games"));
				System.out.println(games.size() + " games caregados.");
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				jo = null;
				if (fr != null) {
					try {
						fr.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		geraSinergias();
		System.out.println(Sinergias.cardsSynergies.size() + " sinergias");
		// imprimSins();
	}

	/**
	 * Le sinergias precalculadas do arquivo cache.
	 */
	public static void leSinergias() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(Universo.cl.getResource("output/sinergias.csv").getFile()));
		} catch (FileNotFoundException e) {
			// TODO deve gera-lo...
			e.printStackTrace();
		}
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split("\t");
			Card c1 = Universo.getCard(line[0]);
			Card c2 = Universo.getCard(line[1]);
			int freq = Integer.parseInt(line[2]);
			float val = Float.parseFloat(line[3]);
			String mech = line[4];
			if (c1 != null && c2 != null) {
				Sinergias.cardsSynergies.add(new Sinergy(c1, c2, freq, val, mech));
			}
		}
		sc.close();
		System.out.println(Sinergias.cardsSynergies.size() + " pre calculated sinergies loaded.");
	}

	public static void imprimSins() {
		Collections.sort((List<Sinergy<Card>>) Sinergias.cardsSynergies);
		StringBuilder sb = new StringBuilder();
		for (Sinergy s : Sinergias.cardsSynergies) {
			String line = s.getE1() + "\t" + s.getE2() + "\t" + s.getFreq() + "\t" + s.getValor() + "\t" + s.getMechs();
			sb.append(line + "\r\n");
			System.out.println(line);
		}
		//EscreveArquivo.escreveArquivo("res/output/sinergias.csv", sb.toString());
		PrintWriter out = null;
		try {
			out = new PrintWriter(new File(Universo.cl.getResource("output/sinergias.csv").getFile()));
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
	 * Partindo da base de jogos, gera sinergias dos pares de cartas jogadas.
	 */
	private static void geraSinergias() {
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
						myprev = Universo.getCard(id);
						continue;
					}
					myatual = Universo.getCard(id);
					if (myatual != null) {
						Sinergy s = Sinergias.getCardSinergy(myprev, myatual);
						if (s == null) {
							s = new Sinergy(myprev, myatual, 1);
							Sinergias.cardsSynergies.add(s);
						}
						s.setValor(s.getValor() + 1);
					}
				} else if ("opponent".equals(player)) {
					if (opoprev == null) {
						opoprev = Universo.getCard(id);
						continue;
					}
					opoatual = Universo.getCard(id);
					if (opoatual != null) {
						Sinergy s = Sinergias.getCardSinergy(opoprev, opoatual);
						if (s == null) {
							s = new Sinergy(opoprev, opoatual, 1);
							Sinergias.cardsSynergies.add(s);
						}
						s.setValor(s.getValor() + 1);
					}
				}
			}
		}
	}
}
