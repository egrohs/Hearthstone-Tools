package hcs.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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
				// calcula similaridade de deck.
				Map<Deck, Double> probs = DeckFinder.similaridade(ZoneLogReader.playMap.values());
				StringBuilder sbb = new StringBuilder();
				for (Deck k : probs.keySet()) {
					// TODO buscar a classe do opo no log?
					if (k.classe == opponent.classe) {
						sbb.append(k.name + " = " + probs.get(k) + "%\n");
					}
				}
				App.decks(sbb.toString());
				// remove cartas antigas (trim).
				if (PowerLogReader.lastOverTime != null) {
					for (LocalTime cTime : new TreeMap<LocalTime, Carta>(ZoneLogReader.playMap).keySet()) {
						if (cTime.isBefore(PowerLogReader.lastOverTime)) {
							ZoneLogReader.playMap.remove(cTime);
						}
					}
				}
				// game over
				// System.out.println("GAME OVER");
				// ZoneLogReader.playMap = new TreeMap<LocalTime, Carta>();
				// opponent = new Player();
				lastSize = ZoneLogReader.playMap.size();
				if (lastSize > 0) {
					LocalTime lastCardTime = (LocalTime) ZoneLogReader.playMap.keySet()
							.toArray()[ZoneLogReader.playMap.size() - 1];
					if (PowerLogReader.lastManaTime.isBefore(lastCardTime)) {
						Carta card = ZoneLogReader.playMap.remove(lastCardTime);
						System.out.println(lastCardTime + " PLAYED: " + card + " MANA: " + PowerLogReader.lastMana);
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
						// https://hsreplay.net/replay/iYAi8ufm5ivRJ4Rqfm7X4E
						App.provaveis(temp);
					}
				}
			}
		}
	}
}
