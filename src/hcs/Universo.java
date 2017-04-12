package hcs;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hcs.Carta.CLASS;

public class Universo {
	static List<Carta> cards = new ArrayList<Carta>();

	/**
	 * Carrega o db json de cartas em memória.
	 */
	public static List<Carta> leCards() {
		// TODO ler da web
		// https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json
		JSONParser parser = new JSONParser();
		try {
			JSONArray sets = (JSONArray) parser.parse(new FileReader("input/cards.collectible.json"));
			Universo.generateCards(sets);
			System.out.println(cards.size() + " cards imported");
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cards;
	}

	/**
	 * Instancia os objetos cards.
	 * 
	 * @param array
	 *            JSONObject contendo o db de cartas.
	 */
	private static void generateCards(JSONArray array) {
		Iterator<JSONObject> iterator = array.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			Boolean col = (Boolean) o.get("collectible");
			if (col != null && col == true && !"HERO".equals((String) o.get("type"))) {
				String c = (String) o.get("multiClassGroup");
				Carta.CLASS classe;
				if (c != null) {
					classe = Carta.CLASS.valueOf(c);
				} else {
					classe = Carta.CLASS.valueOf((String) o.get("playerClass"));
				}
				cards.add(new Carta((String) o.get("id"), (String) o.get("name"), (String) o.get("set"),
						(String) o.get("race"), classe, (String) o.get("type"), (String) o.get("text"),
						(Long) o.get("cost"), (Long) o.get("attack"), (Long) o.get("health"),
						(Long) o.get("durability"), (String) o.get("rarity")));
			}
		}
		if (getCard("The Coin") == null) {
			// TODO adiciona a moeda
			cards.add(new Carta("game_005", "The Coin", "CORE", "ALLIANCE", CLASS.NEUTRAL, "SPELL",
					"Add 1 mana this turn...", 0L, null, null, null, "COMMON"));
		}
	}

	/**
	 * Busca uma carta por nome ou id.
	 * 
	 * @param idORname
	 * @return Card, null se não achar
	 */
	public static Carta getCard(String idORname) {
		idORname = idORname.trim().toLowerCase().replaceAll("’", "'");
		for (Carta c : cards) {
			if (c.name.equals(idORname)) {
				return c;
			}
			if (c.id.equals(idORname)) {
				return c;
			}
			if (idORname.equals(c.numid)) {
				return c;
			}
		}
		// throw new RuntimeException("Carta não encontrada: " + idORname);
		return null;
	}
}