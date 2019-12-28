package hstools.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hstools.model.Card.CLASS;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Deck extends Node {
	private Card.CLASS classe = CLASS.NEUTRAL;
	private Map<Card, Integer> cartas = new HashMap<Card, Integer>();
	private Map<Tag, Integer> freq = new HashMap<>();

	private int hard_remove;// hard 1 e 2 control|destroy|shuffle|transform
	private int soft_remove;// soft 4 e 8 deal \d+ damage|silence|return to
	private int ones = 0, twos = 0, threes = 0, fours = 0, fives = 0, sixes = 0;
	private float avg_mana;// avgmana 2,10 aggro < 3; midrange; control > 4; =SUMPRODUCT(MANA;QNT)/SUM(QNT)
	private float card_adv;// card =-2,4*avgmana+12 draw|GENERATE
	private int qnt_minions;// Minions 12 e 18
	private int low_rank;// Low Rank 0 cards with rank < 3,3
	private int finishers;// Finishers 0+ attack > 3 && windfury
	private int utils;// Utilities 2 e 8
	// Techs ?
	// Mana Qnt
	// 0-1 2 e 10 (12)
	// 2 2 e 8 (12)
	// 3 4 e 10 (13)
	// 4 4 e 6
	// 5 1 e 6 (8)
	// 6+ 0 e 4 (6)

	public void stats() {
		for (Tag t : freq.keySet()) {
			if (t.getName().equals("HARD_REMOVE")) {
				hard_remove += freq.get(t);
			} else if (t.getName().equals("SOFT_REMOVE")) {
				soft_remove += freq.get(t);
			} else if (t.getName().equals("DRAW") || t.getName().equals("GENERATE")) {
				card_adv += freq.get(t);
			}
		}
		for (Card c : cartas.keySet()) {
			if (c.getCost() < 2) {
				ones += cartas.get(c);
			} else if (c.getCost() == 2) {
				twos += cartas.get(c);
			} else if (c.getCost() == 3) {
				threes += cartas.get(c);
			} else if (c.getCost() == 4) {
				fours += cartas.get(c);
			} else if (c.getCost() == 5) {
				fives += cartas.get(c);
			} else if (c.getCost() >= 6) {
				sixes += cartas.get(c);
			}
			if (c.getType().equals("minion")) {
				qnt_minions += cartas.get(c);
			}
			if (c.getRank() < 3.3) {
				low_rank += cartas.get(c);
			}
			if (c.getTags().toString().contains("WINDFURY") && c.getAttack() > 3) {
				finishers += cartas.get(c);
			}
		}
		avg_mana = (ones + twos * 2 + threes * 3 + fours * 4 + fives * 5 + sixes * 7) / 30;
		System.out.println("-----------" + this.getName() + "-----------");
		if (hard_remove >= 1 && hard_remove <= 2) {
			System.out.println("hard_remove = " + hard_remove + " ref = 1 a 2 control|destroy|shuffle|transform");
		} else {
			System.err.println("hard_remove = " + hard_remove + " ref = 1 a 2 control|destroy|shuffle|transform");
		}
		if (soft_remove >= 4 && soft_remove <= 8) {
			System.out.println("soft_remove = " + soft_remove + " ref = 4 a 8 deal \\d+ damage|silence|return to");
		} else {
			System.err.println("soft_remove = " + soft_remove + " ref = 4 a 8 deal \\d+ damage|silence|return to");
		}
		System.out.println("avg_mana = " + avg_mana);
		System.out.println("card_adv = " + card_adv + " ref = " + -2.4 * avg_mana + 12);
		if (qnt_minions >= 12 && qnt_minions <= 18) {
			System.out.println("qnt_minions = " + qnt_minions + " ref = 12 a 18");
		} else {
			System.err.println("qnt_minions = " + qnt_minions + " ref = 12 a 18");
		}
		System.out.println("low_rank = " + low_rank);
		if (finishers >= 1) {
			System.out.println("finishers = " + finishers + " ref = > 1");
		} else {
			System.err.println("finishers = " + finishers + " ref = > 1");
		}
	}

	public Deck(String nome, Map<Card, Integer> cartas) {
		this.name = nome;
		this.cartas = cartas;
		this.classe = whichClass(new ArrayList<>(cartas.keySet()));
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
		for (Card c : cartas.keySet()) {
			sb.append(c.getName() + "\t" + cartas.get(c) + "\r\n");
		}
		return sb.toString();
	}

	public void calcTags() {
		for (Card c : cartas.keySet()) {
			for (Tag t : c.getTags()) {
				freq.compute(t, (tokenKey, oldValue) -> oldValue == null ? cartas.get(c) : oldValue + cartas.get(c));
			}
		}
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
