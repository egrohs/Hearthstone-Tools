package hcs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hcs.model.Card;
import hcs.model.Card.CLASS;
import hcs.model.Mechanic;
import hcs.model.Player;
import hcs.model.Sinergy;

/**
 * Base de dados de jogos em json HS, para analise estatistica.
 * 
 * @author egrohs
 *
 */
public class GameBuilder extends Thread {
    Player player1, opponent = new Player();
    //CardBuilder cb;
    int lastSize;
    Map<Mechanic, Integer> mechs = new LinkedHashMap<Mechanic, Integer>();
    private DeckBuilder db;
    
    public GameBuilder() {
	//cb = new CardBuilder();
	db = new DeckBuilder();
    }	

    @Override
    public void run() {
	while (true) {
	    // Iterator it = data.iterator();
	    // while (it.hasNext()) it.next();
	    // synchronized (ZoneLogReader.playMap)
	    if (ZoneLogReader.done && PowerLogReader.done && lastSize != ZoneLogReader.playMap.size()) {
		trim();
		for (Card card : ZoneLogReader.playMap.values()) {
		    cmechs(card);
		}
		// TODO
		//StringBuilder sbb = simi();
		// App.decks(sbb.toString());
		// game over
		// System.out.println("GAME OVER");
		// ZoneLogReader.playMap = new TreeMap<LocalTime, Carta>();
		// opponent = new Player();
		lastSize = ZoneLogReader.playMap.size();
		if (lastSize > 0) {
		    LocalTime lastCardTime = (LocalTime) ZoneLogReader.playMap.keySet()
			    .toArray()[ZoneLogReader.playMap.size() - 1];
		    if (PowerLogReader.lastManaTime.isBefore(lastCardTime)) {
			// TODO deveria remover? Onde manter as cartas jogadas?
			// Carta card =
			// ZoneLogReader.playMap.remove(lastCardTime);
			Card card = ZoneLogReader.playMap.get(lastCardTime);
			System.out.println(lastCardTime + " PLAYED: " + card + " MANA: " + PowerLogReader.lastMana);
			acerto(card);
			// TODO usar todas mecanicas mais jogadas? s� as com
			// mais ocorrencias?
			Set<Card> temp = possiveis();
			// TODO
			// App.provaveis(temp);
		    }
		}
	    }
	}
    }

    public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap) {
	List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());
	Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
	    public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
		return (o1.getValue()).compareTo(o2.getValue());
	    }
	});
	Map<K, V> result = new LinkedHashMap<K, V>();
	for (Map.Entry<K, V> entry : list) {
	    result.put(entry.getKey(), entry.getValue());
	}
	return result;
    }

    private void cmechs(Card c) {
	for (Mechanic mecanica : c.getMechanics()) {
	    if (mechs.containsKey(mecanica)) {
		Integer v = mechs.get(mecanica);
		mechs.put(mecanica, (v + 1));
	    } else {
		mechs.put(mecanica, 1);
	    }
	}
	mechs = sortByValue(mechs);
	System.out.println("//////////////");
	for (Mechanic m : mechs.keySet()) {
	    System.out.println(m + ": " + mechs.get(m));
	}
	System.out.println("//////////////");
    }

    /**
     * Calcula o % de acerto da jogada anterior
     * 
     * @param card
     */
    private void acerto(Card card) {
	// calcula o acerto.
	double acerto = 0;
//		for (int i = 0; i < App.cartas.getChildren().size(); i++) {
//			Carta c = (Carta) App.cartas.getChildren().get(i);
//			if (c == card) {
//				acerto = 100.0 / i;
//				break;
//			}
//		}
	System.out.println("ACERTO: " + card.getName() + " " + acerto + "%");
    }

    /**
     * Calcula as futuras possiveis jogadas pela sinergia da carta jogada...
     * 
     * @param card
     * @return
     */
    private Map<Card, String> possiveis2(Card card) {
	// calcula possiveis jogadas.
	// TODO deve considerar todas cartas ja jogadas
	//CardBuilder.generateCardSynergies(card);
	// TODO tem que ser o mana que ele terminou o turno
	Set<Sinergy<Card>> sub = db.getCardSinergies(card, PowerLogReader.lastMana + 1, opponent.getClasse());
	List<Sinergy<Card>> exibe = new ArrayList<Sinergy<Card>>(sub);
	Collections.sort(exibe);
	Map<Card, String> temp = new LinkedHashMap<Card, String>();
	for (Sinergy<Card> sinergia : exibe) {
	    Card c = (Card) sinergia.getE2();
	    if (card == c) {
		c = (Card) sinergia.getE1();
	    }
	    if (!temp.containsKey(c)) {
		String t = c.getName() + " f:" + sinergia.getFreq() + " v:" + sinergia.getValor();
		temp.put(c, t);
	    }
	}
	return temp;
    }

    private Set<Card> possiveis() {
	//TODO return TagBuilder.getMechsCards(mechs, PowerLogReader.lastMana + 1, opponent.getClasse());
	return null;
    }

    /**
     * remove cartas de partidas antigas.
     * 
     */
    private void trim() {
	if (PowerLogReader.lastOverTime != null) {
	    for (LocalTime cTime : new TreeMap<LocalTime, Card>(ZoneLogReader.playMap).keySet()) {
		if (cTime.isBefore(PowerLogReader.lastOverTime)) {
		    ZoneLogReader.playMap.remove(cTime);
		}
	    }
	}
    }
    
 // TODO LinkedHashSet???
    // static Set<Sinergia> sinergias = new HashSet<Sinergia>();
    // static Sinergias sinergias = new Sinergias();
    JSONArray games = new JSONArray();
    private ClassLoader cl = this.getClass().getClassLoader();

    public static void main(String[] args) {
	CardBuilder cb = new CardBuilder();
	cb.buildCards();
	GameBuilder gb = new GameBuilder();
	gb.leJogos();
	// leSinergias();
	cb.provaveis(CardBuilder.getCard("entomb"), 1, CLASS.PRIEST);
    }

    /**
     * Le jogos (usar -Xmx1300m).
     */
    private void leJogos() {
	// TODO ler do site http://www.hearthscry.com/CollectOBot
	JSONParser parser = new JSONParser();
	File folder = new File(cl.getResource("jogos").getFile());
	// File folder = new File("res/jogos");
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
//	geraSinergias();
//	System.out.println(CardBuilder.cardsSynergies.size() + " sinergias");
	// imprimSins();
    }
    
    private Map<Pattern, Integer> calc(int manaRestante, CLASS opo) {
	Map<Pattern, Integer> res = new HashMap<Pattern, Integer>();
	for (Card card : CardBuilder.cards) {
	    // System.out.println(card.getText());
	    if (CLASS.contem(opo, card.getClasse()) && card.getCost() <= manaRestante) {
		for (Pattern p : TagBuilder.pts) {
		    Matcher matcher = p.matcher(card.getText());
		    Integer i = res.get(p);
		    if (matcher.find()) {
			res.put(p, i == null ? 1 : (i + 1));
		    }
		}
	    }
	}
	return res;
    }
}
