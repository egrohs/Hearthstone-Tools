package hstools.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hstools.PowerLogReader;
import hstools.ZoneLogReader;
import hstools.model.Card;
import hstools.model.Card.CLASS;
import hstools.model.Deck;
import hstools.model.Player;
import hstools.model.SynergyEdge;

@Service
public class DataScienceService {
	@Autowired
	private CardService cs;
	@Autowired
	private DeckService ds;

	public void matrix() {
		// TODO abordagem 2, por matrix de afinidade.
		int[][] affinity = new int[cs.getCards().size()][cs.getCards().size()];// iniciada com zeros

		for (Deck deck : ds.getDecks()) {
			for (Card c1 : deck.getCartas().keySet()) {
				for (Card c2 : deck.getCartas().keySet()) {
					affinity[c1.getId().intValue()][c2.getId().intValue()]++;
				}
			}
		}
		Map<String, Integer> sorted = new LinkedHashMap<>();
		for (int i = 0; i < affinity.length; i++) {
			for (int j = 0; j < affinity.length; j++) {
				sorted.put("" + i + "," + j, affinity[i][j]);
			}
		}
		sorted.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> {
					throw new AssertionError();
				}, LinkedHashMap::new));
		for (String k : sorted.keySet()) {
			int val = sorted.get(k);
			if (val > 9)
				System.out.println(cs.getCard(k.split(",")[0]).getName() + " & " + cs.getCard(k.split(",")[1]).getName()
						+ " = " + sorted.get(k));
		}
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
		for (Deck deck : ds.getDecks()) {
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
	private Map<Card, String> possiveis2(Card card, CLASS opo) {
		// calcula possiveis jogadas.
		// TODO deve considerar todas cartas ja jogadas
		// CardBuilder.generateCardSynergies(card);
		// TODO tem que ser o mana que ele terminou o turno
		Set<SynergyEdge<Card>> sub = cs.getCardSinergies(card, PowerLogReader.lastMana + 1, opo);
		List<SynergyEdge<Card>> exibe = new ArrayList<SynergyEdge<Card>>(sub);
		Collections.sort(exibe);
		Map<Card, String> temp = new LinkedHashMap<Card, String>();
		for (SynergyEdge<Card> sinergia : exibe) {
			Card c = (Card) sinergia.getE2();
			if (card == c) {
				c = (Card) sinergia.getE1();
			}
			if (!temp.containsKey(c)) {
				String t = c.getName() + " f:" + sinergia.getFreq() + " v:" + sinergia.getWeight();
				temp.put(c, t);
			}
		}
		return temp;
	}

	public Set<Card> possiveis() {
		// TODO return TagBuilder.getMechsCards(mechs, PowerLogReader.lastMana + 1,
		// opponent.getClasse());
		return null;
	}
}
