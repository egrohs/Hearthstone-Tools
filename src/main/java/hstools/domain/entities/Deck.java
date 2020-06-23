package hstools.domain.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hstools.domain.entities.Card.CLASS;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Deck extends Node {
	public enum Archtype {

	}

	public enum Formato {
		STANDARD(1), WILD(2);

		private Formato(int i) {
			this.valor = i;
		}

		private int valor;

		public static Formato getByValor(int v) {
			for (Formato f : Formato.values()) {
				if (f.valor == v) {
					return f;
				}
			}
			return null;
		}
	}

	// TODO enum above
	private String archtype, deckstring;
	private Formato formato;
	private Card.CLASS classe = CLASS.NEUTRAL;
	private Map<Card, Integer> cards = new HashMap<>();
	private Map<Tag, Integer> tags = new HashMap<>();
	private Expansion set;

	private int hard_remove;// hard 1 e 2 control|destroy|shuffle|transform
	private int soft_remove;// soft 4 e 8 deal \d+ damage|silence|return to
	//private int ones = 0, twos = 0, threes = 0, fours = 0, fives = 0, sixes = 0;
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

	public Deck(Long id, String nome, Map<Card, Integer> cartas) {
		super(id);
		this.name = nome;
		this.cards = cartas;
		this.classe = whichClass(new ArrayList<>(cartas.keySet()));
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

	public void incHard_remove(int integer) {
		hard_remove += integer;
	}

	public void incSoft_remove(int integer) {
		soft_remove += integer;
	}

	public void incCard_adv(Integer integer) {
		card_adv += integer;
	}

	public void incLow_cost_minions(Integer integer) {
		low_cost_minions += integer;
	}

	public void incSurv(Integer integer) {
		surv += integer;
	}
}
