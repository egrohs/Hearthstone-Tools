package hcs;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hcs.model.Carta;
import hcs.model.Carta.CLASS;

public class Universo {
	static List<Carta> cards = new ArrayList<Carta>();
	
	public static CLASS whichClass(Set<Carta> cartas) {
		Map<CLASS, Integer> qnts = new HashMap<CLASS, Integer>();
		CLASS most = CLASS.NEUTRAL;
		for (Carta c : cartas) {
			if (qnts.get(c.classe) == null)
				qnts.put(c.classe, 1);
			else
				qnts.put(c.classe, qnts.get(c.classe) + 1);
		}
		for (CLASS cls : qnts.keySet()) {
			if (most == CLASS.NEUTRAL || qnts.get(most) < qnts.get(cls)) {
				most = cls;
			}
		}
		return most;
	}

	/**
	 * Carrega o db json de cartas em memória.
	 */
	public static List<Carta> leCards() {
		// TODO ler da web
		// https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json
		// https://api.hearthstonejson.com/v1/20022/enUS/
		JSONParser parser = new JSONParser();
		try {
			JSONArray sets = (JSONArray) parser.parse(new FileReader("res/cards.collectible.json"));
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
		//if (getCard("The Coin") == null)
		{
			// TODO adiciona a moeda
			cards.add(new Carta("game_005", "the coin", "CORE", "ALLIANCE", CLASS.NEUTRAL, "SPELL",
					"Add 1 mana this turn...", 0L, null, null, null, "COMMON"));
		}
	}

	/**
	 * Busca uma carta por nome ou id.
	 * 
	 * @param idORname
	 * @return Card.
	 */
	public static Carta getCard(String idORname) {
		if (idORname != null && !"".equals(idORname)) {
			for (Carta c : cards) {
				if (c.name.equalsIgnoreCase(idORname.trim().replaceAll("’", "'"))) {
					return c;
				}
				if (c.id.equalsIgnoreCase(idORname)) {
					return c;
				}
				if (idORname.equalsIgnoreCase(c.numid)) {
					return c;
				}
			}
		}
		// TODO CS2_013t excess mana not found..
		throw new RuntimeException("Card not found: " + idORname);
		//return null;
	}
}