package hstools.domain.entities;

import java.util.HashSet;
import java.util.Set;

import hstools.Constants.Format;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
//@NodeEntity
@EqualsAndHashCode(callSuper = true)
public class Deck extends Node {
	private String deckstring;
	private Format format;
	private String classe = "Neutral";
	//@Relationship
	private Expansion expansion;
	//@Relationship
	private DeckStats stats = new DeckStats();
	//@Relationship
	private Set<SynergyEdge<Deck, Card>> cards = new HashSet<SynergyEdge<Deck, Card>>();
	//@Relationship
	private Set<SynergyEdge<Deck, Tag>> tags = new HashSet<SynergyEdge<Deck, Tag>>();

	public Deck() {}

	public Deck(String nome, Set<SynergyEdge<Deck, Card>> cards) {
		this.name = nome;
		this.cards = cards;
		//TODO pq isso?
//		for (SynergyEdge<Deck, Card> s : cards) {
//			this.classe = s.getTarget().getClasse();
//			if (this.classe != CLASS.NEUTRAL) {
//				break;
//			}
//		}
		calcSet();
	}

	public Deck(String encodedString) {
		this.deckstring = encodedString;
	}

	public void acumTagSynergy(Tag tag, int f) {
		SynergyEdge<Deck, Tag> syn = null;
		for (SynergyEdge<Deck, Tag> s : tags) {
			if (s.getTarget().equals(tag)) {
				syn = s;
				break;
			}
		}
		if (syn == null) {
			syn = new SynergyEdge<>(this, tag, 0);
		}
		syn.setFreq(syn.getFreq() + f);
		tags.add(syn);
		System.out.println("taagg " + syn.getTarget() + ": " + syn.getFreq());
	}

	private void calcSet() {
		// for (Card c : cards.keySet())
		{

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
		for (SynergyEdge<Deck, Card> s : cards) {
			sb.append(s.getTarget().toString() + "\t" + s.getFreq() + "\r\n");
		}
		return sb.toString();
	}
}