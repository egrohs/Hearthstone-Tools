package hstools.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;

import hstools.Constants.Archtype;
import lombok.Data;

@Data
@NodeEntity
public class DeckStats {
	private Archtype archtype;
	private int hard_remove;// hard 1 e 2 control|destroy|shuffle|transform
	private int soft_remove;// soft 4 e 8 deal \d+ damage|silence|return to
	// private int ones = 0, twos = 0, threes = 0, fours = 0, fives = 0, sixes = 0;
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