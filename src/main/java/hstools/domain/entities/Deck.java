package hstools.domain.entities;

import java.util.HashMap;
import java.util.Map;

import hstools.Constants.Format;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
	private DeckStats stats = new DeckStats(this.name + "-deckstats");
	// @Relationship
	// private Set<SynergyEdge<Deck, Card>> cards = new HashSet<>();
	private Map<Card, Integer> cards = new HashMap<>();
	// @Relationship
	// private Set<SynergyEdge<Deck, Tag>> tags = new HashSet<>();

	public Deck(String nome, String classe) {
		this.name = nome;
		this.classe = classe;
	}
	
	public Deck(String nome, String classe, Map<Card, Integer> cards) {
		this(nome, classe);
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

	public Deck(String nome, String classe, String encodedString) {
		this(nome, classe);
		this.deckstring = encodedString;
	}

//	public void addCard(Card c) {
//		for (SynergyEdge<Deck, Card> synergyEdge : cards) {
//			if (synergyEdge.getTarget().equals(c)) {
//				synergyEdge.setFreq(synergyEdge.getFreq() + 1);
//				return;
//			}
//		}
//		cards.add(new SynergyEdge<>(this, c, 1));
//	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + "\r\n");
		sb.append(classe + "\r\n");
		for (Card s : cards.keySet()) {
			sb.append(s.getName() + "\t" + cards.get(s) + "\r\n");
		}
		return sb.toString();
	}
}