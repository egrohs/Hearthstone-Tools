package hstools.domain.entities;

import org.neo4j.ogm.annotation.NodeEntity;

import lombok.Data;

@Data
@NodeEntity
public class CardStats {
	private float rank;
	private Integer popularity, combats, wins, draws, loses;
	private boolean aggro/* , visited */;
	private String function;

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