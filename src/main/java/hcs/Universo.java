package hcs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hcs.model.Card;
import hcs.model.Card.CLASS;

public class Universo {
    static List<Card> cards = new ArrayList<Card>();
    public static ClassLoader cl = Universo.class.getClassLoader();

    public static CLASS whichClass(List<Card> cartas) {
	Map<CLASS, Integer> qnts = new HashMap<CLASS, Integer>();
	CLASS most = CLASS.NEUTRAL;
	for (Card c : cartas) {
	    if (qnts.get(c.getClasse()) == null)
		qnts.put(c.getClasse(), 1);
	    else
		qnts.put(c.getClasse(), qnts.get(c.getClasse()) + 1);
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
    public static List<Card> leCards() {
	if (cards.size() == 0) {
	    // TODO ler da web
	    // https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json
	    // https://api.hearthstonejson.com/v1/20022/enUS/
	    JSONParser parser = new JSONParser();
	    try {
		File file = new File(cl.getResource("cards.collectible.json").getFile());
		JSONArray sets = (JSONArray) parser.parse(new FileReader(file));
		Universo.generateCards(sets);
		System.out.println(cards.size() + " cards imported");
	    } catch (ParseException e1) {
		e1.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return cards;
    }

    /**
     * Instancia os objetos cards.
     * 
     * @param array JSONObject contendo o db de cartas.
     */
    private static void generateCards(JSONArray array) {
	Iterator<JSONObject> iterator = array.iterator();
	while (iterator.hasNext()) {
	    JSONObject o = iterator.next();
	    Boolean col = (Boolean) o.get("collectible");
	    if (col != null && col == true /*
					    * && !"HERO".equals((String) o.get("type"))
					    */) {
		Card.CLASS classe;
		String c = (String) o.get("multiClassGroup");
		if (c == null) {
		    c = (String) o.get("cardClass");
		}
		if (c == null) {
		    c = (String) o.get("playerClass");
		}
		classe = Card.CLASS.valueOf(c);

		// TODO card mechanics??
		// List<String> mechs = (List<String>) o.get("mechanics");

		String text = (String) o.get("text");
		try {
		    if (text != null) {
			text = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		    }
		} catch (UnsupportedEncodingException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		cards.add(new Card((String) o.get("id"), (String) o.get("name"), (String) o.get("set"),
			(String) o.get("race"), classe, (String) o.get("type"), text, (Long) o.get("cost"),
			(Long) o.get("attack"), (Long) o.get("health"), (Long) o.get("durability"),
			(String) o.get("rarity")));
	    }
	}
	// if (getCard("The Coin") == null)
	{
	    // TODO adiciona a moeda
	    cards.add(new Card("game_005", "the coin", "CORE", "ALLIANCE", CLASS.NEUTRAL, "SPELL",
		    "Add 1 mana this turn...", 0L, null, null, null, "COMMON"));
	}
    }

    /**
     * Busca uma carta por nome ou id.
     * 
     * @param idORname
     * @return Card.
     */
    public static Card getCard(String idORname) {
	if (idORname != null && !"".equals(idORname)) {
	    for (Card c : cards) {
		if (c.getName().equalsIgnoreCase(idORname.trim().replaceAll("’", "'"))) {
		    return c;
		}
		if (c.getId().equalsIgnoreCase(idORname)) {
		    return c;
		}
		if (idORname.equalsIgnoreCase(c.getNumid())) {
		    return c;
		}
	    }
	}
	// TODO CS2_013t excess mana not found..
	throw new RuntimeException("Carta não encontrada: " + idORname);
	// return null;
    }

    public static void main(String[] args) {
	Universo.leCards();
//		for (Carta c : cards) {			
//			System.out.println(c.m);
//		}
    }
}