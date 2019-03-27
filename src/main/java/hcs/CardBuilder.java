package hcs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hcs.model.Card;
import hcs.model.Card.CLASS;
import hcs.model.Mechanic;
import hcs.model.Sinergy;
import hcs.model.Tag;

public class CardBuilder {
    static List<Card> cards = new ArrayList<Card>();
    static List<Sinergy<Card>> cardsSynergies = new ArrayList<Sinergy<Card>>();
    public static ClassLoader cl = CardBuilder.class.getClassLoader();

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
		CardBuilder.generateCards(sets);
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

    /**
     * Gera as sinergias de todas as cartas.
     */
    @Deprecated
    private static void generateCardsSynergies() {
	long ini = System.currentTimeMillis();
	for (Sinergy s : TagBuilder.mechanicsSynergies) {
	    Mechanic m1 = (Mechanic) s.getE1();
	    Mechanic m2 = (Mechanic) s.getE2();
	    for (Card c : CardBuilder.cards) {
		if (c.getMechanics().contains(m1)) {
		    for (Card c2 : CardBuilder.cards) {
			if (c2.getMechanics().contains(m2)) {
			    Sinergy ss = getCardSinergy(c, c2);
			    if (ss == null) {
				ss = new Sinergy(c, c2, s.getValor(), m1.regex + "+" + m2.regex);
				cardsSynergies.add(ss);
			    } else {
				ss.setValor(ss.getValor() + s.getValor());
			    }
			}
		    }
		}
	    }
	}
	System.out.println(System.currentTimeMillis() - ini);
	// Collections.sort(Sinergias.cardsSynergies);
	System.out.println(cardsSynergies.size() + " sinergies calculated from parsed card texts.");
    }

    /**
     * Calcula as provaveis jogadas.
     * 
     * @param c
     * @param manaRestante Mana restante no turno atual.
     * @return
     */
    public static Set<Sinergy<Card>> getCardSinergies(Card c, int manaRestante, CLASS opo) {
	Set<Sinergy<Card>> sub = new LinkedHashSet<Sinergy<Card>>();
	// Set<Carta> sub = new LinkedHashSet<Carta>();
	if (c != null) {
	    for (Sinergy s : cardsSynergies) {
		if (s.getE1() == c || s.getE2() == c) {
		    Card c2 = (Card) s.getE2();
		    if (c == c2) {
			c = (Card) s.getE1();
		    }
		    // cartas com sinergia com custo provavel no turno
		    if (CLASS.contem(opo, c2.getClasse()) && c2.getCost() <= manaRestante) {
			sub.add(s);
			System.out.println(c2 + "\t" + s.getValor() + "\t" + s.getMechs());
		    }
		}
	    }
	}
	return sub;
    }

    public static void generateCardSynergies(Card c) {
	if (!c.isCalculada()) {
	    for (Sinergy<Tag> s : TagBuilder.tagsSynergies) {
		Tag m1 = (Tag) s.getE1();
		Tag m2 = (Tag) s.getE2();
		if (c.getTags().contains(m1)) {
		    for (Card c2 : CardBuilder.cards) {
			if (c2.getTags().contains(m2)) {
			    Sinergy<Card> ss = getCardSinergy(c, c2);
			    if (ss == null) {
				ss = new Sinergy<Card>(c, c2, s.getValor(), m1.getRegex() + "+" + m2.getRegex());
				cardsSynergies.add(ss);
			    } else {
				ss.setValor(ss.getValor() + s.getValor());
			    }
			}
		    }
		}
	    }
	    // Collections.sort(Sinergias.cardsSynergies);
	    c.setCalculada(true);
	}
    }

    /**
     * return the sinergy with those Cards.
     * 
     * @param e1
     * @param e2
     * @return the Sinergy object
     */
    public static Sinergy<Card> getCardSinergy(Card e1, Card e2) {
	for (Sinergy<Card> s : cardsSynergies) {
	    if ((e1 == s.getE1() && e2 == s.getE2()) || (e1 == s.getE2() && e2 == s.getE1())) {
		return s;
	    }
	}
	return null;
    }

    /**
     * Calcula as provaveis jogadas.
     * 
     * @param c
     * @param manaRestante Mana restante no turno atual.
     * @return
     */
    public static Set<Card> provaveis(Card c, int manaRestante, CLASS opo) {
	// Set<Sinergia> sub = new LinkedHashSet<Sinergia>();
	Set<Card> sub = new LinkedHashSet<Card>();
	if (c != null) {
	    for (Sinergy s : cardsSynergies) {
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
     * Le arquivo de sinergias da web.
     */
    private static void readSynergies() {
	JSONParser parser = new JSONParser();
	try {
	    JSONArray sets = (JSONArray) parser
		    .parse(new FileReader(new File(CardBuilder.cl.getResource("sinergy/synergy.json").getFile())));
	    System.out.println(sets.size() + " synergies imported");
	    generateNumIds(sets);
	} catch (ParseException e1) {
	    e1.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Apos arquivo de sinergias lido, gera a lista de sinergias.
     * 
     * @param sets
     */
    private static void generateNumIds(JSONArray sets) {
	Iterator<JSONObject> iterator = sets.iterator();
	while (iterator.hasNext()) {
	    JSONObject o = iterator.next();
	    String id = (String) o.get("id");
	    String numid = (String) o.get("numid");
	    Card c = CardBuilder.getCard(id);
	    c.setNumid(numid);
	}
	iterator = sets.iterator();
	while (iterator.hasNext()) {
	    JSONObject o = iterator.next();
	    String id = (String) o.get("id");
	    Card c = CardBuilder.getCard(id);
	    JSONArray sin = (JSONArray) o.get("synergies");
	    if (sin != null) {
		Iterator<JSONArray> iterator2 = sin.iterator();
		while (iterator2.hasNext()) {
		    JSONArray o2 = iterator2.next();
		    Card c2 = CardBuilder.getCard((String) o2.get(0));
		    Float value = Float.parseFloat(o2.get(1).toString());
		    // TODO remover esse if
		    if (value > 4.0) {
			CardBuilder.cardsSynergies.add(new Sinergy(c, c2, value, ""));
		    }
		}
	    }
	}
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
    private static Set<Card> buildDeck(Card.CLASS classe, String[] initialCards, Set<Card> deck, int depth) {
	System.out.println("Sinergias para " + initialCards[0]);
	for (String cardname : initialCards) {
	    Card c = CardBuilder.getCard(cardname);
	    for (Sinergy s : CardBuilder.cardsSynergies) {
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

    private static void printCard(String n) {
	Card card = CardBuilder.getCard(n);
	for (Mechanic m : card.getMechanics()) {
	    System.out.println(m.regex);
	}
    }

    /**
     * Imprime todas cartas que tem sinergia com a informada.
     * 
     * @param card Carta consultada.
     */
    private static void printCardSynergies(Card card) {
	Set<Sinergy<Card>> minhaS = CardBuilder.getCardSinergies(card, 10, card.getClasse());
	// Collections.sort(minhaS);
	for (Sinergy s : minhaS) {
	    System.out.println(s.getE1().getName() + "\t");
	}
    }

    public static void main(String[] args) {
	CardBuilder.leCards();
//		for (Carta c : cards) {			
//			System.out.println(c.m);
//		}
    }
}