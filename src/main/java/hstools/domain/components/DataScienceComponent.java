package hstools.domain.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import hstools.domain.entities.Card;
import hstools.domain.entities.Deck;
import hstools.domain.entities.Player;
import hstools.playaid.ZoneLogReader;

/**
 * Does any calculations on hs data.
 * 
 * @author EGrohs
 *
 */
@Service
@DependsOn(value = { "Cards" })
public class DataScienceComponent {
	@Autowired
	private CardComponent cardComp;
	@Autowired
	private DeckComponent deckComp;
	@Autowired
	private SynergyBuilder synComp;

	public void tagsAffin() {
//		synComp.loadCombos();
//		Map<String, SynergyEdge<Tag>> tagSins = new LinkedHashMap<String, SynergyEdge<Tag>>();
//		for (SynergyEdge<Card> s : synComp.getCardsSynergies()) {
//			Card c1 = (Card) s.getSource();
//			Card c2 = (Card) s.getTarget();
//			for (Tag t1 : c1.getTags()) {
//				for (Tag t2 : c2.getTags()) {
//					String key = "" + t1.getId() + t2.getId();
//					if (!tagSins.containsKey(key))
//						tagSins.put(key, new SynergyEdge<Tag>(t1, t2, 1));
//					else
//						tagSins.get(key).setFreq(tagSins.get(key).getFreq() + 1);
//				}
//			}
//		}
//		for (String key : tagSins.keySet()) {
//			SynergyEdge<Tag> s = tagSins.get(key);
//			System.out.println(s.getE1().getName() + "\t" + s.getE2().getName() + "\t" + s.getFreq());
//		}
	}

	/**
	 * Calcula a similariade de deck partindo de todas cartas jogadas ate agora.
	 * 
	 * @return
	 */
	private StringBuilder simi(Player opponent) {
		// calcula similaridade de deck.
		Map<Deck, Double> probs = similaridade(ZoneLogReader.playMap.values());
		StringBuilder sbb = new StringBuilder();
		for (Deck k : probs.keySet()) {
			// TODO buscar a classe do opo no log?
			if (k.getClasse() == opponent.getClasse()) {
				sbb.append(k.getName() + " = " + probs.get(k) + "%\n");
			}
		}
		return sbb;
	}

	private Map<Deck, Double> similaridade(Collection<Card> searched) {
		List<String> nomes = new ArrayList<String>();
		for (Card carta : searched) {
			// System.out.println("similaridade: " +carta.getName());
			nomes.add(carta.getName());
		}
		return similaridade(nomes.toArray(new String[searched.size()]));
	}

	private Map<Deck, Double> similaridade(String[] cartas) {
		Map<Deck, Double> prob = new HashMap<Deck, Double>();
		System.out.println("----------------------");
		for (Deck deck : deckComp.getDecks()) {
			int cont = 0;
			for (String c : cartas) {
				Integer qnt = deck.getQnt(c);
				if (qnt != null && qnt > 0) {
					cont += 1;
					double p = Math.round(10000.0 * cont / 30.0) / 100.0;
					prob.put(deck, p);
				}
			}
			if (prob.get(deck) != null) {
				System.out.println(deck.getName() + ": " + prob.get(deck));
			}
		}
		return prob;
	}
//
//	public Set<SynergyEdge<Card>> getCardSinergies(Card card, int manaRestante, CLASS classe) {
//		return cb.getCardSinergies(card, manaRestante, classe);
//	}

	/**
	 * Calcula as futuras possiveis jogadas pela sinergia da carta jogada...
	 * 
	 * @param card
	 * @return
	 */
	private Map<Card, String> possiveis(Card card, String opoClass) {
		// calcula possiveis jogadas.
		// TODO deve considerar todas cartas ja jogadas
		// CardBuilder.generateCardSynergies(card);
		// TODO tem que ser o mana que ele terminou o turno
//		Set<SynergyEdge<Card>> sub = synComp.opponentPlays(card, PowerLogReader.lastMana + 1, opo);
//		List<SynergyEdge<Card>> exibe = new ArrayList<SynergyEdge<Card>>(sub);
//		Collections.sort(exibe);
//		Map<Card, String> temp = new LinkedHashMap<Card, String>();
//		for (SynergyEdge<Card> sinergia : exibe) {
//			Card c = (Card) sinergia.getE2();
//			if (card == c) {
//				c = (Card) sinergia.getE1();
//			}
//			if (!temp.containsKey(c)) {
//				String t = c.getName() + " f:" + sinergia.getFreq() + " v:" + sinergia.getWeight();
//				temp.put(c, t);
//			}
//		}
//		return temp;
		return null;
	}
}