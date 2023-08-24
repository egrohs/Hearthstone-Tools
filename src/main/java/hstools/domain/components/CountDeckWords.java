package hstools.domain.components;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import hstools.domain.entities.Card;
import hstools.domain.entities.Deck;
import hstools.domain.entities.SynergyEdge;

@Service
//@DependsOn(value = { "Cards" })
public class CountDeckWords {
	public static List<String> keyWords = List.of("summon", "deal", "hand", "taunt", "spell", "all", "deck", "draw",
			"health", "add", "rush", "costs", "less", "cast", "cop(y|ies)", "discover", "destroy", "secret", "attacks",
			"divine", "play", "mana", "when", "lifesteal", "armor", "shuffle", "weapon", "control", "while", "beast",
			"stealth", "dragon", "transform", "overload", "elemental", "return", "discard", "mech", "demon", "freeze",
			"charge", "poisonous", "murloc", "windfury", "battlefield", "outcast", "immune", "adjacent", "infuse",
			"silence", "reborn", "quest", "pirate", "inspire", "die", "frenzy", "recruit", "adapt", "lackey",
			"overkill", "reveal", "treant", "totem", "twinspell", "odd", "revive", "overheal", "corpse",
			"even", "silver hand");

	public static void main(String[] args) {
		
	}

	public static Map<String, Integer> wordsMap(Deck deck, Map<String, Integer> map) {
		// System.out.println(deck);
		for (Card card : deck.getCards().keySet()) {
			String lower = (card.getText().toString() + card.getType() + (card.getRace() != null ? card.getRace() : ""))
					.toLowerCase();

			for (String regex : keyWords) {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(lower);
				if (matcher.find()) {
					map.put(regex, map.containsKey(regex) ? map.get(regex) + 1 : 1);
				}
			}

//			List<String> filteredWords = keyWords.stream().filter(p -> lower.matches(p)).collect(Collectors.toList());
//			filteredWords.forEach(w -> map.put(w, map.containsKey(w) ? map.get(w) + 1 : 1));

			// System.out.println(card.getName()+" "+ filteredWords);
		}
		// map.forEach((word, qnt) -> System.out.println(word + " " + qnt));
		return map;
	}
}
