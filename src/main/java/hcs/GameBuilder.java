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
public class GameBuilder {
	// TODO LinkedHashSet???
	// static Set<Sinergia> sinergias = new HashSet<Sinergia>();
	// static Sinergias sinergias = new Sinergias();
	static JSONArray games = new JSONArray();

	public static void main(String[] args) {
		CardBuilder.leCards();
		leJogos();
		// leSinergias();
		CardBuilder.provaveis(CardBuilder.getCard("entomb"), 1, CLASS.PRIEST);
	}

	public GameBuilder() {
		// leJogos(); //muito lento...
	}

	

	/**
	 * Le jogos (usar -Xmx1300m).
	 */
	private static void leJogos() {
		// TODO ler do site http://www.hearthscry.com/CollectOBot
		JSONParser parser = new JSONParser();
		File folder = new File(CardBuilder.cl.getResource("jogos").getFile());
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
		System.out.println(CardBuilder.cardsSynergies.size() + " sinergias");
		// imprimSins();
	}

	/**
	 * Le sinergias precalculadas do arquivo cache.
	 */
	public static void leSinergias() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(CardBuilder.cl.getResource("output/sinergias.csv").getFile()));
		} catch (FileNotFoundException e) {
			// TODO deve gera-lo...
			e.printStackTrace();
		}
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split("\t");
			Card c1 = CardBuilder.getCard(line[0]);
			Card c2 = CardBuilder.getCard(line[1]);
			int freq = Integer.parseInt(line[2]);
			float val = Float.parseFloat(line[3]);
			String mech = line[4];
			if (c1 != null && c2 != null) {
				CardBuilder.cardsSynergies.add(new Sinergy(c1, c2, freq, val, mech));
			}
		}
		sc.close();
		System.out.println(CardBuilder.cardsSynergies.size() + " pre calculated sinergies loaded.");
	}

	public static void imprimSins() {
		Collections.sort((List<Sinergy<Card>>) CardBuilder.cardsSynergies);
		StringBuilder sb = new StringBuilder();
		for (Sinergy s : CardBuilder.cardsSynergies) {
			String line = s.getE1() + "\t" + s.getE2() + "\t" + s.getFreq() + "\t" + s.getValor() + "\t" + s.getMechs();
			sb.append(line + "\r\n");
			System.out.println(line);
		}
		//EscreveArquivo.escreveArquivo("res/output/sinergias.csv", sb.toString());
		PrintWriter out = null;
		try {
			out = new PrintWriter(new File(CardBuilder.cl.getResource("output/sinergias.csv").getFile()));
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
						myprev = CardBuilder.getCard(id);
						continue;
					}
					myatual = CardBuilder.getCard(id);
					if (myatual != null) {
						Sinergy s = CardBuilder.getCardSinergy(myprev, myatual);
						if (s == null) {
							s = new Sinergy(myprev, myatual, 1);
							CardBuilder.cardsSynergies.add(s);
						}
						s.setValor(s.getValor() + 1);
					}
				} else if ("opponent".equals(player)) {
					if (opoprev == null) {
						opoprev = CardBuilder.getCard(id);
						continue;
					}
					opoatual = CardBuilder.getCard(id);
					if (opoatual != null) {
						Sinergy s = CardBuilder.getCardSinergy(opoprev, opoatual);
						if (s == null) {
							s = new Sinergy(opoprev, opoatual, 1);
							CardBuilder.cardsSynergies.add(s);
						}
						s.setValor(s.getValor() + 1);
					}
				}
			}
		}
	}
}
