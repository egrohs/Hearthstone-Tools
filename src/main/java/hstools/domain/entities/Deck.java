package hstools.domain.entities;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import hstools.Constants.CLASS;
import hstools.Constants.Format;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@NodeEntity
@EqualsAndHashCode(callSuper = true)
public class Deck extends Node {
	private String deckstring;
	private Format format;
	private CLASS classe = CLASS.NEUTRAL;
	@Relationship
	private Expansion expansion;
	@Relationship
	private DeckStats stats;
	@Relationship
	// private Map<Card, Integer> cards = new HashMap<>();
	private Set<SynergyEdge<Deck, Card>> cards = new HashSet<SynergyEdge<Deck, Card>>();
	@Relationship
	// private Map<Tag, Integer> tags = new HashMap<>();
	private Set<SynergyEdge<Deck, Tag>> tags = new HashSet<SynergyEdge<Deck, Tag>>();

	public Deck() {
		this.stats = new DeckStats();
	}

	public Deck(String nome, Set<SynergyEdge<Deck, Card>> cards) {
		this.name = nome;
		this.cards = cards;
		for (SynergyEdge<Deck, Card> s : cards) {
			this.classe = s.getTarget().getClasse();
			if (this.classe != CLASS.NEUTRAL) {
				break;
			}
		}
		this.stats = new DeckStats();
		calcSet();
	}

	public void incTagSynergy(Tag tag) {
		SynergyEdge<Deck, Tag> syn = null;
		for (SynergyEdge<Deck, Tag> s : tags) {
			if (s.getTarget().equals(tag)) {
				syn = s;
				break;
			}
		}
		if (syn == null) {
			syn = new SynergyEdge<>(this, tag, 1);
		}
		syn.setFreq(syn.getFreq() + 1);
		tags.add(syn);
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