package hstools.domain.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import hstools.Constants.CLASS;
import hstools.Constants.Format;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@NodeEntity
@EqualsAndHashCode(callSuper = true)
public class Deck extends Node {
	private String deckstring;
	private Format format;
	private CLASS classe = CLASS.NEUTRAL;
	@Relationship
	private Expansion expansion;
	@Relationship
	private DeckStats stats;
	@Relationship
	private Map<Card, Integer> cards = new HashMap<>();
	@Relationship
	private Map<Tag, Integer> tags = new HashMap<>();

	public Deck(String nome, Map<Card, Integer> cartas) {
		this.name = nome;
		this.cards = cartas;
		this.classe = whichClass(new ArrayList<>(cartas.keySet()));
		this.stats = new DeckStats();
		calcSet();
	}

	private void calcSet() {
		for (Card c : cards.keySet()) {

		}
	}

	public Integer getQnt(String nome) {
		// System.out.println("getQnt "+nome);
//		Card c = cb.getCard(nome);
//		if (cartas.containsKey(c)) {
//			return cartas.get(c);
//		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + "\r\n");
		sb.append(classe + "\r\n");
		for (Card c : cards.keySet()) {
			sb.append(c.getName() + "\t" + cards.get(c) + "\r\n");
		}
		return sb.toString();
	}

	private CLASS whichClass(List<Card> cartas) {
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
}