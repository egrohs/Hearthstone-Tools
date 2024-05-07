package hstools.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
//@NodeEntity
public class CardStats extends Node {
	private float rank, stats_cost = 0;
	private Integer popularity, combats, wins, draws, loses;
	private boolean aggro/* , visited */;
	private String function;
	
	private Integer tempDeckFreq;

	public void calcAggro(Card c) {
		if (c.getCost() > 0 && c.getAttack() != null && c.getAttack() > 2) {
			float agg = ((float) c.getAttack() / c.getCost());
			if (agg > 1.5f) {
				aggro = true;
			}
		}
	}

	public void incDraws() {
		this.draws++;
	}

	public void incCombats() {
		this.combats++;
	}

	public void incWins() {
		this.wins++;
	}

	public void incLoses() {
		this.loses++;
	}
}