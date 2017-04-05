package hcs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Jogos {
	//static List<Carta> cards = new ArrayList<Carta>();
	static List<Sinergia> sinergias = new ArrayList<Sinergia>();
	static JSONArray games = new JSONArray();

	public static void main(String[] args) {
		Universo.leCards();
		Jogos.leJogos();
	}

	private static void leJogos() {
		//TODO ler do site http://www.hearthscry.com/CollectOBot
		JSONParser parser = new JSONParser();
		File folder = new File("jogos");
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			try {
				JSONObject jo = (JSONObject) parser.parse(new FileReader(file));
				games.addAll((JSONArray) jo.get("games"));
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(games.size() + " games");
		geraSinergias();
		System.out.println(sinergias.size() + " sinergias");
		imprimSins();
	}

	private static void imprimSins() {
		Collections.sort(sinergias);
		StringBuilder sb = new StringBuilder();
		for (Sinergia s : sinergias) {
			String line = s.e1 + "\t" + s.e2 + "\t" + s.valor;
			sb.append(line + "\r\n");
			System.out.println(line);
		}
		EscreveArquivo.escreveArquivo("output/sinergias.csv", sb.toString());
	}

	/**
	 * Gera sinergias dos pares de cartas jogadas.
	 */
	private static void geraSinergias() {
		Iterator<JSONObject> iterator = games.iterator();
		while (iterator.hasNext()) {
			JSONObject game = iterator.next();
			Iterator<JSONObject> card_history = ((JSONArray) game.get("card_history")).iterator();
			Carta myprev = null, opoprev = null, myatual = null, opoatual = null;
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
						Sinergia s = Sinergia.getSinergy(sinergias, myprev, myatual);
						if (s == null) {
							s = new Sinergia(myprev, myatual, 0f);
							sinergias.add(s);
						}
						s.valor += 1;
					}
				} else if ("opponent".equals(player)) {
					if (opoprev == null) {
						opoprev = Universo.getCard(id);
						continue;
					}
					opoatual = Universo.getCard(id);
					if (opoatual != null) {
						Sinergia s = Sinergia.getSinergy(sinergias, opoprev, opoatual);
						if (s == null) {
							s = new Sinergia(opoprev, opoatual, 0f);
							sinergias.add(s);
						}
						s.valor += 1;
					}
				}
			}
		}
	}
}
