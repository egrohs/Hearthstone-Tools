package hcs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcs.CardBuilder;
import hcs.model.Card.CLASS;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Deck extends Entity {
	private Card.CLASS classe = CLASS.NEUTRAL;
	private Map<Card, Integer> cartas = new HashMap<Card, Integer>();

	public Deck(String nome, Map<Card, Integer> cartas) {
		this.name = nome;
		this.cartas = cartas;
		this.classe = whichClass(new ArrayList<>(cartas.keySet()));
	}

	public Integer getQnt(String nome) {
		// System.out.println("getQnt "+nome);
		Card c = CardBuilder.getCard(nome);
		if (cartas.containsKey(c)) {
			return cartas.get(c);
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + "\r\n");
		sb.append(classe + "\r\n");
		for (Card c : cartas.keySet()) {
			sb.append(c.getName() + "\t" + cartas.get(c) + "\r\n");
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
