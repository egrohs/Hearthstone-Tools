package hcs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tentativa de calcular sinergia das cartas usando db de decks. Pares de cartas
 * com alta sinergia devem aparecer em varios decks.
 * 
 * @author 99689650068
 *
 */
public class Probs {
	// matrix de afinidade de cartas dos decks hunter
	// 288 neutral + 45 warrior
	double[][] affinity = new double[40][40];// iniciada com zeros
	String deck1[] = { "Inner Rage", "Execute", "Whirlwind", "Battle Rage", "Cruel Taskmaster", "Fiery War Axe",
			"Frothing Berserker", "Warsong Commander", "Death's Bite", "Grommash Hellscream", "Unstable Ghoul",
			"Acolyte Of Pain", "Raging Worgen", "Grim Patron", "Sludge Belcher", "Emperor Thaurissan", "Dr. Boom" };
	String deck2[] = { "Upgrade!", "Fiery War Axe", "Heroic Strike", "Cruel Taskmaster", "Death's Bite",
			"Mortal Strike", "Kor'kron Elite", "Arcanite Reaper", "Abusive Sergeant", "Leper Gnome",
			"Worgen Infiltrator", "Bloodsail Raider", "Faerie Dragon", "Ironbeak Owl", "Loot Hoarder", "Arcane Golem",
			"Wolfrider", "Dread Corsair" };
	String deck3[] = { "Execute", "Warbot", "Fiery War Axe", "Battle Rage", "Revenge", "Cruel Taskmaster",
			"Frothing Berserker", "Death's Bite", "Arcanite Reaper", "Grommash Hellscream", "Acidic Swamp Ooze",
			"Amani Berserker", "Acolyte of Pain", "Dread Corsair", "Piloted Shredder", "Spellbreaker", "Loatheb",
			"Sludge Belcher", "Dr. Boom", "Ragnaros the Firelord" };

	public Probs() {
		Set<String> list = new HashSet<String>();
		list.addAll(Arrays.asList(deck1));
		list.addAll(Arrays.asList(deck2));
		list.addAll(Arrays.asList(deck3));
		all.addAll(list);
	}

	public static void main(String[] args) {
		Probs s = new Probs();
		s.matrix();
		// s.printMatrix();
		s.probs("Fiery War Axe");
	}

	// in ordem alfabetica?
	List<String> all = new ArrayList<String>();

	List<String[]> decks = new ArrayList<String[]>();

	private void matrix() {
		decks.add(deck1);
		decks.add(deck2);
		decks.add(deck3);
		// int qnt, numDecks = 3;
		String card = null;
		// int ind = hunter_neutral.indexOf(card);
		for (String[] deck : decks) {
			for (int i = 0; i < affinity.length; i++) {
				for (int j = 0; j < i && j < affinity[i].length; j++) {
					String origem = all.get(i);
					String destino = all.get(j);
					if (Arrays.asList(deck).contains(origem) && Arrays.asList(deck).contains(destino)) {
						affinity[i][j]++;
						// affinity[j][i]++;
						// TODO ainda nao leva em conta a qnt das cartas
						// TODO ainda é apenas uma matrix triangular
					}
				}
			}
		}
	}

	private void printMatrix() {
		for (int i = 0; i < affinity.length; i++) {
			for (int j = 0; j < i && j < affinity[i].length; j++) {
				System.out.println("PAIR: " + all.get(i) + " & " + all.get(j) + " = " + affinity[i][j] / decks.size());
			}
		}
	}

	private void probs(String card) {
		int p = all.indexOf(card);
		int pi = 0, pj = 0;
		double maxi = 0, maxj = 0;
		for (int i = p; i < affinity.length; i++) {
			if (affinity[i][p] > maxi) {
				maxi = affinity[i][p];
				pi = i;
			}
		}
		for (int j = 0; j < affinity[p].length; j++) {
			if (affinity[p][j] > maxj) {
				maxj = affinity[p][j];
				pj = j;
			}
		}
		if (maxi > maxj) {
			System.out.println(all.get(pi) + ": " + maxi / decks.size());
		} else {
			System.out.println(all.get(pj) + ": " + maxj / decks.size());
		}
	}

}
