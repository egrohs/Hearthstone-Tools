package ht;

import java.util.HashMap;
import java.util.Map;

import ht.model.Card;
import ht.model.Token;

public class SemanticParser {
	public static void main(String[] args) {
		Token state = null;
		CardBuilder cb = new CardBuilder();
		for (Card c : CardBuilder.cards) {
			String tokens[] = c.getText().toString().replaceAll("\\:", " \\:").replaceAll("\\.", " \\.")
					.replaceAll("\\-(\\w)", " $1")// "6-cost minion"
					.replaceAll("\\[x\\]", "").split("\\s");// [x] deathrattle
			for (int i = 0; i < tokens.length; i++) {
				String t = tokens[i];
				if (".".equals(t) || ":".equals(t)) {
					// terminou a expressao semantica
					state = null;
					System.out.print(tokens[i] + " ");
					continue;
				}
				for (String k : m.keySet()) {
					if (t.matches(k)) {
						if (state == null || state.nextStates().contains(state.valueOf(m.get(k)))) {
							tokens[i] = t.replaceFirst(k, m.get(k));
							state = state.valueOf(m.get(k));
							break;
						}
					}
				}
				// TODO se nao encontrou deve zerar o estado ou abortar a linha?
				System.out.print(tokens[i] + " ");
			}
			System.out.println(c.getText());
			state = null;
		}
	}

	static Map<String, String> m = new HashMap<>();
	static {
		m.put("hand", "ZONE");
		m.put("deck", "ZONE");
		m.put("deal", "VERB");
		m.put("give", "VERB");
		m.put("gain", "VERB");
		m.put("add", "VERB");
		m.put("has", "VERB");
		m.put("cast", "VERB");
		m.put("have", "VERB");
		m.put("attacks", "VERB");
		m.put("shuffle", "VERB");
		m.put("turn", "TIME");
		m.put("whenever", "TIME");
		m.put("after", "TIME");
		m.put("end", "TIME");
		m.put("when", "TIME");
		m.put("minion(s)?", "TARGET");
		m.put("you", "TARGET");
		m.put("hero", "TARGET");
		m.put("card", "TARGET");
		m.put("spell", "TARGET");
		m.put("cards", "TARGET");
		m.put("opponent", "TARGET");
		m.put("opponent's", "TARGET");
		m.put("weapon", "TARGET");
		m.put("spells", "TARGET");
		m.put("beast", "RACA");
		m.put("all", "QNT");
		m.put("one", "QNT");
		m.put("two", "QNT");
		m.put("your", "PRONOME");
		m.put("this", "PRONOME");
		//m.put("friendly", "PRONOME");
		m.put("friendly", "TARGET");//??
		m.put("each", "PRONOME");
		m.put("enemy", "PRONOME");
		m.put("that", "PRONOME");
		m.put("other", "PRONOME");
		m.put("with", "PREPOSITION");
		m.put("from", "PREPOSITION");
		m.put("into", "PREPOSITION");
		m.put("and", "OPERATOR");
		m.put("less", "OPERATOR");
		m.put("more", "OPERATOR");
		m.put("battlecry", "MECANICA");
		m.put("summon", "MECANICA");
		m.put("deathrattle", "MECANICA");
		m.put("taunt", "MECANICA");
		m.put("draw", "MECANICA");
		m.put("destroy", "MECANICA");
		m.put("copy", "MECANICA");
		m.put("restore", "MECANICA");
		m.put("secret", "MECANICA");
		m.put("discover", "MECANICA");
		m.put("rush", "MECANICA");
		m.put("choose", "MECANICA");
		// TODO SHIELD
		m.put("divine", "MECANICA");
		m.put("stealth", "MECANICA");
		m.put("control", "MECANICA");
		m.put("lifesteal", "MECANICA");
		m.put("attack", "ATTRIB");
		m.put("health", "ATTRIB");
		m.put("mana", "ATTRIB");
		m.put("armor", "ATTRIB");

		m.put("(\\+|\\-)?\\d+(\\/(\\+|\\-)\\d+)?", "QNT");
		m.put("a", "QNT");
		m.put("while", "TIME");
		m.put("it", "QNT");
		m.put("cost", "ATTRIB");
		m.put("random", "PRONOME");//??
		m.put("beast", "TARGET");
		m.put("damage", "ATTRIB");
		m.put("to","TARGET");
		m.put("other(s)?","TARGET");
		m.put("character(s)?","TARGET");
	}
}