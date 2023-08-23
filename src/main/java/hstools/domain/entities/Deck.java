package hstools.domain.entities;

import java.util.HashSet;
import java.util.Set;

import hstools.Constants.Format;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
//@NodeEntity
//@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Deck extends Node {
	private String deckstring;
	private Format format;
	private String classe = "Neutral";
	// @Relationship
	private Expansion expansion;
	// @Relationship
	private DeckStats stats = new DeckStats(this.name+"-deckstats");
	// @Relationship
	private Set<SynergyEdge<Deck, Card>> cards = new HashSet<>();
	// @Relationship
	// private Set<SynergyEdge<Deck, Tag>> tags = new HashSet<>();

	public Deck(String nome, Set<SynergyEdge<Deck, Card>> cards) {
		this.name = nome;
		this.cards = cards;
		// TODO pq isso?
//		for (SynergyEdge<Deck, Card> s : cards) {
//			this.classe = s.getTarget().getClasse();
//			if (this.classe != CLASS.NEUTRAL) {
//				break;
//			}
//		}
//		calcSet();
	}

	public Deck(String nome, String encodedString) {
		this.name = nome;
		this.deckstring = encodedString;
	}

	public void addCard(Card c) {
		for (SynergyEdge<Deck, Card> synergyEdge : cards) {
			if (synergyEdge.getTarget().equals(c)) {
				synergyEdge.setFreq(synergyEdge.getFreq() + 1);
				return;
			}
		}
		cards.add(new SynergyEdge<>(this, c, 1));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + "\r\n");
		sb.append(classe + "\r\n");
		for (SynergyEdge<Deck, Card> s : cards) {
			sb.append(s.getTarget().getName() + "\t" + s.getFreq() + "\r\n");
		}
		return sb.toString();
	}
}