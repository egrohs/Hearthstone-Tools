package hcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hcs.model.Card;
import hcs.model.Mecanica;
import hcs.model.Sinergy;
import hcs.model.Tag;

//TODO guardar as sinergias calculadas em arquivo, para evitar lentid�o ao rodar.
public class SinergyFromText {
    // static List<Carta> cards = new ArrayList<Carta>();
    // private static int cont = 0;
    // private static TGFParser tgfp;
    // private static List<Card> deck = new ArrayList<Card>();

    public static void main(String[] args) {
	new SinergyFromText();
	// String card = "Frothing Berserker";
	// System.out.println("Sinergy for " + card);
	// generateTextSynergies(card);
	// printCardMechanics();
	// TGFToMatrix();
	// generateTextSynergies("Azure Drake");
	// countMAffinities();
	// printCard("Acidic Swamp Ooze");
	//printDeck(buildDeck(Carta.CLASS.SHAMAN, new String[] { "Tunnel Trogg" }, new HashSet<Carta>(), 0));
	// printCard("Dread Corsair");
	// printM2M();
	// for (Sinergia s : sins) {
	// System.out.println(s.getE1() + "\t" + s.getE2() + "\t" + s.getValor());
	// }
	// System.out.println(sins.size());
    }

    private static void printDeck(Collection<Card> deck) {
	for (int i = 0; i < deck.size(); i++) {
	    Card c1 = (Card) deck.toArray()[i];
	    int cont = 0;
	    Float acum = 0f;
	    for (int j = i; j < deck.size(); j++) {
		Card c2 = (Card) deck.toArray()[j];
		Sinergy s = Sinergias.getCardSinergy(c1, c2);
		acum += s != null ? s.getValor() : 0f;
		cont++;
	    }
	    System.out.println(acum / cont + "\t" + c1.getName() + "\t" + c1.getClasse() + "\t" + c1.getText());
	}
    }

    public SinergyFromText() {
	// buildMechanics();
	//Universo.leCards();
	// printCards();
	//readMechanics();
	// buildCards();
	//parseCardsText2Mechanics();
	// generateCardSynergies();
//	calcTags();
//	printTags();
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
	    Card c = Universo.getCard(cardname);
	    for (Sinergy s : Sinergias.cardsSynergies) {
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
	Card card = Universo.getCard(n);
	for (Mecanica m : card.getMechanics()) {
	    System.out.println(m.regex);
	}
    }

    /**
     * Imprime todas cartas que tem sinergia com a informada.
     * 
     * @param card Carta consultada.
     */
    private static void printCardSynergies(Card card) {
	Set<Sinergy<Card>> minhaS = Sinergias.getCardSinergies(card, 10, card.getClasse());
	// Collections.sort(minhaS);
	for (Sinergy s : minhaS) {
	    System.out.println(s.getE1().getName() + "\t");
	}
    }

    /**
     * Le arquivo de sinergias da web.
     */
    private static void readSynergies() {
	JSONParser parser = new JSONParser();
	try {
	    JSONArray sets = (JSONArray) parser
		    .parse(new FileReader(new File(Universo.cl.getResource("sinergy/synergy.json").getFile())));
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
	    Card c = Universo.getCard(id);
	    c.setNumid(numid);
	}
	iterator = sets.iterator();
	while (iterator.hasNext()) {
	    JSONObject o = iterator.next();
	    String id = (String) o.get("id");
	    Card c = Universo.getCard(id);
	    JSONArray sin = (JSONArray) o.get("synergies");
	    if (sin != null) {
		Iterator<JSONArray> iterator2 = sin.iterator();
		while (iterator2.hasNext()) {
		    JSONArray o2 = iterator2.next();
		    Card c2 = Universo.getCard((String) o2.get(0));
		    Float value = Float.parseFloat(o2.get(1).toString());
		    // TODO remover esse if
		    if (value > 4.0) {
			Sinergias.cardsSynergies.add(new Sinergy(c, c2, value, ""));
		    }
		}
	    }
	}
    }

    
}