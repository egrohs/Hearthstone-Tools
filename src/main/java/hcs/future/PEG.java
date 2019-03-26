package hcs.future;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import hcs.model.Carta;

/**
 * Tentativa de parser heartstone usando gramaticas peg identificando os tokens
 * semânticos.
 * 
 * @author 99689650068
 *
 */
public class PEG {
	static List<Carta> cards = new ArrayList<Carta>();
	static Scanner file = null;
	static Map<String, String> tags = new TreeMap<String, String>();
	static List<String> l = null;
	// static List<String> tokens = new ArrayList<String>();
	// +, the, ed?, if, that, the damage, healing, is full," to a "," an ",
	// " at "," to "," to the "," for "," of "," in "," it "," has ",
	// " on ", SOURCES, TIME
	// ":","both","chance","choose","(",")","at","damage","deal","for","full","him","that","the","to","is","it","its","lost","take","of","on",
	static String[] remove = { " always([\\s\\.\\,])", " remaining([\\s\\.\\,])", " their([\\s\\.\\,])",
			" apprentice([\\s\\.\\,])", " ashbringer([\\s\\.\\,])", " awesome([\\s\\.\\,])",
			" flame of azzinoth([\\s\\.\\,])", " baine([\\s\\.\\,])", " bananas([\\s\\.\\,])", " bandit([\\s\\.\\,])",
			" bloodhoof([\\s\\.\\,])", " boar([\\s\\.\\,])", " chord([\\s\\.\\,])", " defias([\\s\\.\\,])",
			" devilsaur([\\s\\.\\,])", " dream([\\s\\.\\,])", " einhorn([\\s\\.\\,])", " extra([\\s\\.\\,])",
			" finkle([\\s\\.\\,])", " 'fireball'([\\s\\.\\,])", " gnoll([\\s\\.\\,])", " golem([\\s\\.\\,])",
			" hyenas([\\s\\.\\,])", " imp([\\s\\.\\,])", " instead([\\s\\.\\,])", " invention([\\s\\.\\,])",
			" jaraxxus([\\s\\.\\,])", " dragonling([\\s\\.\\,])", " lord([\\s\\.\\,])", " mechanical([\\s\\.\\,])",
			" now([\\s\\.\\,])", " only([\\s\\.\\,])", " per([\\s\\.\\,])", " rock!([\\s\\.\\,])",
			" scout([\\s\\.\\,])", " seconds([\\s\\.\\,])", " side([\\s\\.\\,])", " squire([\\s\\.\\,])",
			" squirrel([\\s\\.\\,])", " taken([\\s\\.\\,])", " treants([\\s\\.\\,])", " violet([\\s\\.\\,])",
			" whelps([\\s\\.\\,])" };

	static {
		try {
			file = new Scanner(new File("textos.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tags.put("chance to", "[PROB]");// essa categoria?
		tags.put("\\d\\-\\d", "[PROB]");// essa categoria?
		tags.put("can't attack", "[ABILITIES]");
		tags.put("can't be targeted by", "[ABILITIES]");
		tags.put("charge", "[ABILITIES]");
		tags.put("divine shield", "[ABILITIES]");
		tags.put("freeze", "[ABILITIES]");
		tags.put("overload", "[ABILITIES]");
		tags.put("spell damage", "[ABILITIES]");
		tags.put("stealth", "[ABILITIES]");
		tags.put("taunt", "[ABILITIES]");
		tags.put("(mega\\-)?windfury", "[ABILITIES]");
		// tags.put("draw( \\d| a| an extra)? card[s]?","[ACTIONS]");
		tags.put("attack the wrong enemy", "[ACTIONS]");// dessa forma?
		tags.put("become a copy", "[ACTIONS]");
		tags.put("change", "[ACTIONS]");
		tags.put("look", "[ACTIONS]");
		tags.put("copy", "[ACTIONS]");
		// tags.put("cost[s]? (\\(?\\d\\)?)( more| less)?","[ACTIONS]");
		tags.put("cost", "[ACTIONS]");
		tags.put("immune", "[ACTIONS]");
		tags.put("add", "[ACTIONS]");
		tags.put("shuffle", "[ACTIONS]");
		tags.put("deal", "[ACTIONS]");
		tags.put("destroy", "[ACTIONS]");
		tags.put("discard", "[ACTIONS]");
		tags.put("double", "[ACTIONS]");
		tags.put("draw", "[ACTIONS]");
		tags.put("equip", "[ACTIONS]");
		tags.put("gain", "[ACTIONS]");
		tags.put("give", "[ACTIONS]");
		tags.put("have", "[ACTIONS]");
		tags.put("heal", "[ACTIONS]");
		tags.put("lose", "[ACTIONS]");
		tags.put("put", "[ACTIONS]");
		tags.put("remove", "[ACTIONS]");
		tags.put("replace", "[ACTIONS]");
		tags.put("restore", "[ACTIONS]");
		tags.put("return", "[ACTIONS]");
		tags.put("set", "[ACTIONS]");
		tags.put("silence", "[ACTIONS]");
		tags.put("split (among|between)", "[ACTIONS]");
		tags.put("summon", "[ACTIONS]");
		tags.put("swap", "[ACTIONS]");
		tags.put("(gain|take) control of", "[ACTIONS]");
		tags.put("transform", "[ACTIONS]");
		tags.put("damage", "[ACTIONS]");
		tags.put("armor", "[ATRIB]");
		tags.put("attack", "[ATRIB]");
		tags.put("durability", "[ATRIB]");
		tags.put("health", "[ATRIB]");
		tags.put("mana crystal", "[ATRIB]");
		// tags.put("damage (\\.\\s)+has taken","[CONDS]");
		tags.put("control", "[CONDS]");
		tags.put("for each", "[CONDS]");
		tags.put("if", "[CONDS]");
		tags.put("played earlier", "[CONDS]");
		tags.put("until", "[CONDS]");
		tags.put("weapon equipped", "[CONDS]");// ?
		tags.put("with", "[CONDS]");// with(conds) ABILITY, replace ... with(?)
		tags.put("cast(s)? a spell", "[EVENT]");
		tags.put("frozen", "[EVENT]");
		tags.put("drawn", "[EVENT]");
		tags.put("die", "[EVENT]");
		tags.put("died", "[EVENT]");
		tags.put("is healed", "[EVENT]");
		tags.put("is played", "[EVENT]");
		tags.put("is summoned", "[EVENT]");
		tags.put("play", "[EVENT]");
		tags.put("takes damage", "[EVENT]");
		// tags.put("healing","[EFFECT]");
		tags.put("and", "[OPERATOR]");// com espaço no inicio?
		tags.put("or", "[OPERATOR]");// com espaço no inicio?
		tags.put("but not less than", "[OPERATOR]");
		tags.put("equal to", "[OPERATOR]");
		tags.put("or (more|less)", "[OPERATOR]");// operators <= >=
		tags.put("\\+?\\d+\\/\\+?\\d+", "[STAT]");
		tags.put("(\\+|\\-)?\\(?\\d+\\)?\\%?", "[QUANTIF]");
		// tags.put("a","[QUANTIF]");// com espaços?
		tags.put("twice", "[QUANTIF]");
		tags.put("all", "[QUANTIF]");// com espaços?
		tags.put("any", "[QUANTIF]");
		tags.put("each", "[QUANTIF]");
		tags.put("first", "[QUANTIF]");// ? TIME, EVENT?
		tags.put("(more|less)", "[QUANTIF]");// ?multiply?+-
		tags.put("next", "[QUANTIF]");// ? TIME, EVENT?
		tags.put("a", "[QUANTIF]");
		tags.put("an", "[QUANTIF]");
		tags.put("one", "[QUANTIF]");
		tags.put("two", "[QUANTIF]");
		tags.put("three", "[QUANTIF]");
		tags.put("four", "[QUANTIF]");
		tags.put("from", "[SOURCES]");
		// tags.put("their","[SOURCES]");
		tags.put("another", "[TARGETS]");
		tags.put("damage to", "[TARGETS]");
		tags.put("other", "[TARGETS]");
		// tags.put("(a|other|all|another)( random)? friendly","[TARGETS]");
		tags.put("adjacent", "[TARGETS]");
		tags.put("gnome", "[TARGETS]");
		tags.put("beast", "[TARGETS]");
		tags.put("spare part", "[TARGETS]");
		tags.put("mech", "[TARGETS]");
		tags.put("card", "[TARGETS]");
		tags.put("character", "[TARGETS]");
		tags.put("demon", "[TARGETS]");
		tags.put("dragon", "[TARGETS]");
		tags.put("enem(y|ies)", "[TARGETS]");
		tags.put("friend(ly)?", "[TARGETS]");
		tags.put("hero", "[TARGETS]");
		tags.put("himself", "[TARGETS]");
		tags.put("into", "[TARGETS]");// ?
		tags.put("minion", "[TARGETS]");
		tags.put("murloc", "[TARGETS]");
		tags.put("on either side", "[TARGETS]");
		tags.put("opponent", "[TARGETS]");
		tags.put("owner", "[TARGETS]");
		tags.put("pirate", "[TARGETS]");
		tags.put("player", "[TARGETS]");
		tags.put("power", "[TARGETS]");
		tags.put("random(ly)?", "[TARGETS]");
		tags.put("secret", "[TARGETS]");
		tags.put("spell", "[TARGETS]");
		tags.put("this", "[TARGETS]");
		tags.put("totem", "[TARGETS]");
		tags.put("weapon", "[TARGETS]");
		tags.put("you", "[TARGETS]");
		tags.put("your", "[TARGETS]");// ?
		tags.put("after", "[TRIGGERS]");
		tags.put("at the end of", "[TRIGGERS]");
		tags.put("at the start of", "[TRIGGERS]");
		tags.put("battlecry", "[TRIGGERS]");
		tags.put("choose one (\\-|\\–)", "[TRIGGERS]");
		tags.put("combo", "[TRIGGERS]");
		tags.put("damaged by", "[TRIGGERS]");// ?EVENT?
		tags.put("deathrattle", "[TRIGGERS]");
		tags.put("enrage", "[TRIGGERS]");
		tags.put("whenever", "[TRIGGERS]");
		tags.put("when", "[TRIGGERS]");
		tags.put("while", "[TRIGGERS]");
		tags.put("deck", "[ZONE]");
		tags.put("hand", "[ZONE]");
		tags.put("the battlefield", "[ZONE]");
		tags.put("turn", "[TIME]");
		// System.out.println(tags);

		l = new ArrayList<String>(tags.keySet());
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
	}

	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] {
				// new UniqueHashCode(),
				new NotNull(), new Optional(), new Optional(), new Optional(), new Optional(), new ParseInt(),
				new ParseInt(), new ParseInt(), new NotNull(), new Optional(), new Optional() };

		return processors;
	}

	private static void calcCombats() {
		for (Carta attacker : cards) {
			if (!attacker.getType().equals("Spell")) {// && Wearpon!!!
				for (Carta defender : cards) {
					if (!defender.getType().equals("Spell")) {// && Wearpon!!!
						if (attacker.getCost() == defender.getCost()) {
							attacker.incCombats();
							// wins
							if (attacker.getAttack() >= defender.getHealth() && attacker.getHealth() > defender.getAttack()) {
								attacker.incWins();
							} else if (attacker.getAttack() < defender.getHealth() && attacker.getHealth() <= defender.getAttack()) {
								attacker.incLoses();
							} else {
								attacker.incDraws();
							}
						}
					}
				}
			}
		}
	}

	private static void readExcel() {
		ICsvBeanReader beanReader = null;
		try {
			beanReader = new CsvBeanReader(new FileReader("hs.csv"), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
			// the header elements are used to map the values to the bean (names
			// must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getProcessors();
			Carta card;
			while (true) {
				card = beanReader.read(Carta.class, header, processors);
				if (card == null) {
					break;
				}
				cards.add(card);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (beanReader != null) {
				try {
					beanReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// private static void synergy() {
	// for (Card card : cards) {
	// if (card.playerClass == null || card.playerClass.equals("Warlock")) {
	// if (card.triggers.contains("deathrattle")) {
	// System.out.println("DEATH: " + card.getName() + " = " + card.getText());
	// }
	// if (card.triggers.contains("deal") && card.triggers.contains("all")
	// && (card.triggers.contains("minion") ||
	// card.triggers.contains("character"))) {
	// System.out.println("SELF DAMAGE: " + card.getName() + " = " + card.getText());
	// }
	// }
	// }
	// }

	// public static void main(String[] args) {
	// readExcel();
	// for (Card card : cards) {
	// card.parse(l);
	// // System.out.println(card.Name);
	// // for (String t : card.tags) {
	// // System.out.print(t + ", ");
	// // }
	// // System.out.println();
	// }
	// synergy();
	// }

	public static void main2(String[] args) {
		// System.out.println(tags.keySet().size());
		String line = readLine();
		for (String k : l) {
			// adiciona espaço e plural no inicio e final das tags.
			line = line.replaceAll("\\s" + k + "(s)?\\s", "  ");
		}
		System.out.println(line);
	}

	public static String readLine() {
		String line = null;
		if (file.hasNextLine()) {
			// add espaço no inicio e final da linha para contemplar " tag ".
			line = " " + file.nextLine().toLowerCase().replaceAll("\\)", "").replaceAll("\\(", "").replaceAll("\"", "")
					.replaceAll("\\’", "").replaceAll("\\'", "").replaceAll("\\;", "").replaceAll("\\:", "")
					.replaceAll("\\.", "").replaceAll(",", "") + " ";
			// .replaceAll("'s", "");
		}
		return line;
	}
}