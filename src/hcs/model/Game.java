package hcs.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import hcs.App;
import hcs.DeckFinder;
import hcs.PowerLogReader;
import hcs.Sinergias;
import hcs.SinergyFromText;
import hcs.ZoneLogReader;

public class Game extends Thread {
	public static Player player1, opponent = new Player();
	int lastSize;
	Map<Mecanica, Integer> mechs = new LinkedHashMap<Mecanica, Integer>();

	@Override
	public void run() {
		while (true) {
			// Iterator it = data.iterator();
			// while (it.hasNext()) it.next();
			// synchronized (ZoneLogReader.playMap)
			if (ZoneLogReader.done && PowerLogReader.done && lastSize != ZoneLogReader.playMap.size()) {
				trim();
				for (Carta card : ZoneLogReader.playMap.values()) {
					cmechs(card);
				}
				StringBuilder sbb = simi();
				App.decks(sbb.toString());
				// game over
				// System.out.println("GAME OVER");
				// ZoneLogReader.playMap = new TreeMap<LocalTime, Carta>();
				// opponent = new Player();
				lastSize = ZoneLogReader.playMap.size();
				if (lastSize > 0) {
					LocalTime lastCardTime = (LocalTime) ZoneLogReader.playMap.keySet()
							.toArray()[ZoneLogReader.playMap.size() - 1];
					if (PowerLogReader.lastManaTime.isBefore(lastCardTime)) {
						// TODO deveria remover? Onde manter as cartas jogadas?
						// Carta card =
						// ZoneLogReader.playMap.remove(lastCardTime);
						Carta card = ZoneLogReader.playMap.get(lastCardTime);
						System.out.println(lastCardTime + " PLAYED: " + card + " MANA: " + PowerLogReader.lastMana);
						acerto(card);
						// TODO usar todas mecanicas mais jogadas? só as com
						// mais ocorrencias?
						Map<Carta, String> temp = possiveis();
						App.provaveis(temp);
					}
				}
			}
		}
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private void cmechs(Carta c) {
		for (Mecanica mecanica : c.mechanics) {
			if (mechs.containsKey(mecanica)) {
				Integer v = mechs.get(mecanica);
				mechs.put(mecanica, (v + 1));
			} else {
				mechs.put(mecanica, 1);
			}
		}
		mechs = sortByValue(mechs);
		System.out.println("//////////////");
		for (Mecanica m : mechs.keySet()) {
			System.out.println(m + ": " + mechs.get(m));
		}
		System.out.println("//////////////");
	}

	/**
	 * Calcula o % de acerto da jogada anterior
	 * 
	 * @param card
	 */
	private void acerto(Carta card) {
		// calcula o acerto.
		double acerto = 0;
		for (int i = 0; i < App.cartas.getChildren().size(); i++) {
			Carta c = (Carta) App.cartas.getChildren().get(i);
			if (c == card) {
				acerto = 100.0 / i;
				break;
			}
		}
		System.out.println("ACERTO: " + card.name + " " + acerto + "%");
	}

	/**
	 * Calcula as futuras possiveis jogadas pela sinergia da carta jogada...
	 * 
	 * @param card
	 * @return
	 */
	private Map<Carta, String> possiveis2(Carta card) {
		// calcula possiveis jogadas.
		// TODO deve considerar todas cartas ja jogadas
		SinergyFromText.generateCardSynergies(card);
		// TODO tem que ser o mana que ele terminou o turno
		Set<Sinergia> sub = Sinergias.getCardSinergies(card, PowerLogReader.lastMana + 1, opponent.classe);
		List<Sinergia> exibe = new ArrayList<Sinergia>(sub);
		Collections.sort(exibe);
		Map<Carta, String> temp = new LinkedHashMap<Carta, String>();
		for (Sinergia sinergia : exibe) {
			Carta c = (Carta) sinergia.e2;
			if (card == c) {
				c = (Carta) sinergia.e1;
			}
			if (!temp.containsKey(c)) {
				String t = c.name + " f:" + sinergia.freq + " v:" + sinergia.valor;
				temp.put(c, t);
			}
		}
		return temp;
	}

	private Map<Carta, String> possiveis() {
		return Sinergias.getMechsCards(mechs, PowerLogReader.lastMana + 1, opponent.classe);
	}

	/**
	 * Calcula a similariade de deck partindo de todas cartas jogadas ate agora.
	 * 
	 * @return
	 */
	private StringBuilder simi() {
		// calcula similaridade de deck.
		Map<Deck, Double> probs = DeckFinder.similaridade(ZoneLogReader.playMap.values());
		StringBuilder sbb = new StringBuilder();
		for (Deck k : probs.keySet()) {
			// TODO buscar a classe do opo no log?
			if (k.classe == opponent.classe) {
				sbb.append(k.name + " = " + probs.get(k) + "%\n");
			}
		}
		return sbb;
	}

	/**
	 * remove cartas de partidas antigas.
	 * 
	 */
	private void trim() {
		if (PowerLogReader.lastOverTime != null) {
			for (LocalTime cTime : new TreeMap<LocalTime, Carta>(ZoneLogReader.playMap).keySet()) {
				if (cTime.isBefore(PowerLogReader.lastOverTime)) {
					ZoneLogReader.playMap.remove(cTime);
				}
			}
		}
	}
}
