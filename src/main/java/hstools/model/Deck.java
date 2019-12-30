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
	public enum Archtype {

	}

	// TODO enum above
	private String archtype;
	private Card.CLASS classe = CLASS.NEUTRAL;
	private Map<Card, Integer> cartas = new HashMap<>();
	private Map<Tag, Integer> tags = new HashMap<>();

	private int hard_remove;// hard 1 e 2 control|destroy|shuffle|transform
	private int soft_remove;// soft 4 e 8 deal \d+ damage|silence|return to
	private int ones = 0, twos = 0, threes = 0, fours = 0, fives = 0, sixes = 0;
	private double avg_mana;// avgmana 2,10 aggro < 3; midrange; control > 4; =SUMPRODUCT(MANA;QNT)/SUM(QNT)
	// TODO private archtype usar NN reconhecemento padroes, mana curve, finishers,
	// removals...
	private int card_adv;// card =-2,4*avgmana+12 draw|GENERATE
	private int qnt_minions;// Minions 12 e 18
	private int low_rank;// Low Rank 0 cards with rank < 3,3
	// TODO rename win conditions, dano, fadiga e mill
	private int finishers;// Finishers 0+ attack > 3 && windfury
	private int utils;// Utilities 2 e 8
	private int low_cost_minions;
	private int surv;
	// TODO Techs depende do meta

	// TODO isso vai no deckbuilder?
	public void stats() {
		for (Tag t : tags.keySet()) {
			if (t.getName().equals("HARD_REMOVE")) {
				hard_remove += tags.get(t);
			} else if (t.getName().equals("SOFT_REMOVE")) {// TODO rever, reduce attack...
				soft_remove += tags.get(t);
			} else if (t.getName().equals("DRAW") || t.getName().equals("GENERATE")) {
				card_adv += tags.get(t);
			} else if (t.getName().equals("LOW_COST_MINION")) {
				low_cost_minions += tags.get(t);
			} else if (t.getName().equals("TAUNT") || t.getName().equals("LIFESTEAL") || t.getName().equals("ARMOR")
					|| t.getName().equals("HEALTH_RESTORE")) {
				surv += tags.get(t);
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
//			if (c.getRank() < 3.3) {
//				low_rank += cartas.get(c);
//			}
			// TODO spells
			if (c.getAttack() >= 8 || (c.getTags().toString().contains("WINDFURY") && c.getAttack() >= 3)
					|| (c.getTags().toString().contains("CHARGE") && c.getAttack() >= 5)) {
				finishers += cartas.get(c);
			}
		}
		avg_mana = (ones + twos * 2 + threes * 3 + fours * 4 + fives * 5 + sixes * 7) / 30.0;
		System.out.print(this.getName() + "\t");
		System.out.print(low_cost_minions + ",");
//		if (hard_remove >= 1 && hard_remove <= 2) {
//			System.out.println("hard_remove = " + hard_remove + " ref = 1 a 2 control|destroy|shuffle|transform");
//		}
//		if (soft_remove >= 4 && soft_remove <= 8) {
//			System.out.println("soft_remove = " + soft_remove + " ref = 4 a 8 deal \\d+ damage|silence|return to");
//		}
//		if (ones >= 2 && ones <= 10) {
//			System.out.println("ones = " + ones + " ref = 2 a 10");
//		}
//		if (twos >= 2 && twos <= 8) {
//			System.out.println("twos = " + twos + " ref = 2 a 8");
//		}
//		if (threes >= 4 && threes <= 10) {
//			System.out.println("threes = " + threes + " ref = 4 a 10");
//		}
//		if (fours >= 4 && fours <= 6) {
//			System.out.println("fours = " + fours + " ref = 4 a 6");
//		}
//		if (fives >= 1 && fives <= 6) {
//			System.out.println("fives = " + fives + " ref = 1 a 6");
//		}
//		if (sixes >= 0 && sixes <= 4) {
//			System.out.println("sixes = " + sixes + " ref = 0 a 4");
//		}
		System.out.print(avg_mana + ",");
		// System.out.print(hard_remove + soft_remove + ",");
//		System.out.println("card_adv = " + card_adv + " ref = " + (-2.4 * avg_mana + 12));
//		if (qnt_minions >= 12 && qnt_minions <= 18) {
//			System.out.println("qnt_minions = " + qnt_minions + " ref = 12 a 18");
//		}
//		// System.out.println("low_rank = " + low_rank);
//		if (finishers >= 1) {
//			System.out.println("finishers = " + finishers + " ref = >1");
//		}
		System.out.print(card_adv + ",");
		System.out.print(surv + ",");
		System.out.println(archtype);
//		if (avg_mana < 3) {// tem control warrior e priest com avg_mana = 3
//			System.out.println("AGGRO");
//		} else if (avg_mana >= 4) {
//			// System.out.print(hard_remove + soft_remove + ",");
//			System.out.println("CONTROL");
//		} else {
//			// System.out.print(hard_remove + soft_remove + ",");
//			System.out.println("MIDRANGE");
//		}
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
				tags.compute(t, (tokenKey, oldValue) -> oldValue == null ? cartas.get(c) : oldValue + cartas.get(c));
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
