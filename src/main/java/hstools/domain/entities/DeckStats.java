package hstools.domain.entities;

import java.util.HashMap;
import java.util.Map;

import hstools.Constants.Archtype;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
//@NodeEntity
@EqualsAndHashCode(of = "name")
public class DeckStats {
	private String name;
	private Archtype archtype;
	private int hard_removals;// hard 1 e 2 control|destroy|shuffle|transform
	private int soft_removals;// soft 4 e 8 deal \d+ damage|silence|return to
	private int removals;
	// private int ones = 0, twos = 0, threes = 0, fours = 0, fives = 0, sixes = 0;
	private double avg_mana, stats_cost;// avgmana 2,10 aggro < 3; midrange; control > 4; =SUMPRODUCT(MANA;QNT)/SUM(QNT)
	// TODO private archtype usar NN reconhecemento padroes, mana curve, finishers,
	// removals...
	private int card_adv;// card =-2,4*avgmana+12 draw|GENERATE
	private int qnt_minions;// Minions 12 e 18
	private int low_ranks;// Low Rank 0 cards with rank < 3,3
	// TODO rename win conditions, dano, fadiga e mill
	private int finishers;// Finishers 0+ attack > 3 && windfury
	private int utils;// Utilities 2 e 8
	private int low_cost_minions;
	private int survs;
	private int board_control;
	private int med_cost_minions;
	
	//private Set<SynergyEdge<Deck, Card>> cards = new HashSet<>();
	private Map<Tag, Integer> suggests = new HashMap<>();
	//private Set<SynergyEdge<Tag, Card>> suggests = new HashSet<>();
	
	// TODO Techs depende do meta
	private int high_cost;// high cost cards > 5

	public void incHard_remove(int integer) {
		hard_removals += integer;
	}

	public void incSoft_remove(int integer) {
		soft_removals += integer;
	}

	public void incRemoval(int integer) {
		removals += integer;
	}

	public void incCard_adv(int integer) {
		card_adv += integer;
	}

	public void incLow_cost_minions(int integer) {
		low_cost_minions += integer;
	}

	public void incSurv(int integer) {
		survs += integer;
	}

	public void incHigh_cost(int integer) {
		high_cost += integer;
	}

	public void incBoard_control(int integer) {
		board_control += integer;
	}

	public void incMed_cost_minions(int integer) {
		med_cost_minions += integer;
	}

	public void incStats_cost(double d) {
		stats_cost += d;
	}

	@Override
	public String toString() {
		return card_adv + "\t" + low_cost_minions + "\t" + med_cost_minions + "\t" + high_cost + "\t" + survs + "\t"
				+ board_control + "\t" + stats_cost + "\t" + archtype;
	}

	public DeckStats(String string) {
		this.name = string;
	}

//	public void incCardSyn(Card card) {
//		for (Tag t : suggests.keySet()) {
//			
//		}
//	}
}