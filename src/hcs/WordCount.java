package hcs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilitario para contagem de tokens nos textos das cartas.
 * 
 * @author 99689650068
 *
 */
public class WordCount {
	static Map<String, Integer> tags = new HashMap<String, Integer>();

	static {
		tags.put("(\\+|\\-)?\\(?\\d+\\)?\\%?", 0);
		tags.put("(gain|take) control of", 0);
		tags.put("(more|less)", 0);
		tags.put("\\+?\\d+\\/\\+?\\d+", 0);
		tags.put("\\d\\-\\d", 0);
		tags.put("a", 0);
		tags.put("add", 0);
		tags.put("adjacent", 0);
		tags.put("after", 0);
		tags.put("all", 0);
		tags.put("an", 0);
		tags.put("and", 0);
		tags.put("another", 0);
		tags.put("any", 0);
		tags.put("armor", 0);
		tags.put("at the end of", 0);
		tags.put("at the start of", 0);
		tags.put("attack the wrong enemy", 0);
		tags.put("attack", 0);
		tags.put("battlecry", 0);
		tags.put("beast", 0);
		tags.put("become a copy", 0);
		tags.put("but not less than", 0);
		tags.put("cant attack", 0);
		tags.put("cant be targeted by", 0);
		tags.put("card", 0);
		tags.put("cast(s)? a spell", 0);
		tags.put("chance to", 0);
		tags.put("change", 0);
		tags.put("character", 0);
		tags.put("charge", 0);
		tags.put("choose one (\\-|\\–)", 0);
		tags.put("combo", 0);
		tags.put("control", 0);
		tags.put("copy", 0);
		tags.put("cost", 0);
		tags.put("damage to", 0);
		tags.put("damage", 0);
		tags.put("damaged by", 0);
		tags.put("deal", 0);
		tags.put("deathrattle", 0);
		tags.put("deck", 0);
		tags.put("demon", 0);
		tags.put("destroy", 0);
		tags.put("die", 0);
		tags.put("died", 0);
		tags.put("discard", 0);
		tags.put("divine shield", 0);
		tags.put("double", 0);
		tags.put("dragon", 0);
		tags.put("draw", 0);
		tags.put("drawn", 0);
		tags.put("durability", 0);
		tags.put("each", 0);
		tags.put("enem(y|ies)", 0);
		tags.put("enrage", 0);
		tags.put("equal to", 0);
		tags.put("equip", 0);
		tags.put("first", 0);
		tags.put("for each", 0);
		tags.put("four", 0);
		tags.put("freeze", 0);
		tags.put("friend(ly)?", 0);
		tags.put("from", 0);
		tags.put("frozen", 0);
		tags.put("gain", 0);
		tags.put("give", 0);
		tags.put("gnome", 0);
		tags.put("hand", 0);
		tags.put("have", 0);
		tags.put("heal", 0);
		tags.put("health", 0);
		tags.put("hero", 0);
		tags.put("himself", 0);
		tags.put("if", 0);
		tags.put("immune", 0);
		tags.put("into", 0);
		tags.put("is healed", 0);
		tags.put("is played", 0);
		tags.put("is summoned", 0);
		tags.put("look", 0);
		tags.put("lose", 0);
		tags.put("mana crystal", 0);
		tags.put("mech", 0);
		tags.put("mega-windfury", 0);
		tags.put("minion", 0);
		tags.put("murloc", 0);
		tags.put("next", 0);
		tags.put("on either side", 0);
		tags.put("one", 0);
		tags.put("opponent", 0);
		tags.put("or (more|less)", 0);
		tags.put("or", 0);
		tags.put("other", 0);
		tags.put("overload", 0);
		tags.put("owner", 0);
		tags.put("pirate", 0);
		tags.put("play", 0);
		tags.put("played earlier", 0);
		tags.put("player", 0);
		tags.put("power", 0);
		tags.put("put", 0);
		tags.put("random(ly)?", 0);
		tags.put("remove", 0);
		tags.put("replace", 0);
		tags.put("restore", 0);
		tags.put("return", 0);
		tags.put("secret", 0);
		tags.put("set", 0);
		tags.put("shuffle", 0);
		tags.put("silence", 0);
		tags.put("spare part", 0);
		tags.put("spell damage", 0);
		tags.put("spell", 0);
		tags.put("split (among|between)", 0);
		tags.put("stealth", 0);
		tags.put("summon", 0);
		tags.put("swap", 0);
		tags.put("takes damage", 0);
		tags.put("taunt", 0);
		tags.put("the battlefield", 0);
		tags.put("this", 0);
		tags.put("three", 0);
		tags.put("totem", 0);
		tags.put("transform", 0);
		tags.put("turn", 0);
		tags.put("twice", 0);
		tags.put("two", 0);
		tags.put("until", 0);
		tags.put("weapon equipped", 0);
		tags.put("weapon", 0);
		tags.put("when", 0);
		tags.put("whenever", 0);
		tags.put("while", 0);
		tags.put("(mega\\-)?windfury", 0);
		tags.put("with", 0);
		tags.put("you", 0);
		tags.put("your", 0);
	}

	public static void main(String[] args) throws IOException {
		List<String> l = new ArrayList<String>(tags.keySet());
		Collections.sort(l, new Comparator<String>() {
			public int compare(String o1, String o2) {
				if (o1.length() > o2.length()) {
					return -1;
				} else if (o1.length() < o2.length()) {
					return 1;
				}
				return 0;
			}
		});
		String line = "";
		while (line != null) {
			line = App.readLine();
			String str = line;
			for (String tag : l) {
				String k = " " + tag + "(s)?\\s";
				while (str != null) {
					String r = str.replaceFirst(k, "  ");
					if (str.equals(r)) {
						break;
					}
					str = r;
					tags.put(tag, tags.get(tag) + 1);
					// str = str.replaceAll("\\s" + tag + "(s)?\\s", " ");
				}
				// System.out.println(tag + ";" + i);
				// System.out.println(count(str, " all "));
			}
		}
		for (String tag : l) {
			System.out.println(tag + ";" + tags.get(tag));
		}
	}
}