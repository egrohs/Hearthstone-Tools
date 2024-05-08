package hstools;

public class Constants {
	public static final String ACTION = "(lose|repeat|costs|unlock|take|spend|add|destroy|copy|deal|gain|draw|give|cost|summon|die|double|shuffle|discard|invoke|transform|reduce|swap|return|put|set|upgrade|resurrect|resummon|reveal|replace|fill|refresh|equip)";
	public static final String MECHANIC = "(divine shield|lifesteal|poisonous|tradeable|twinspell|magnetic|windfury|stealth|charge|dredge|freeze|immune|reborn|adapt|taunt|echo|rush)";
	public static final String QUANTIF = "(all|an|a|\\(?\\d+\\)?|one|two|three|four|five|six|seven)";
	public static final String OPERATOR = "(\\+|not|equal)";
	public static final String MECHANICX = "(dormant|recruit|silence|choose|spell damage " + OPERATOR + QUANTIF
			+ "|restore|colossal|discover)";
	public static final String MX = "battlecry|combo|inspire|outcast|overkill|deathrattle|frenzy|spellburst|honorable kill|finale|manathirst "
			+ QUANTIF + "|infuse " + QUANTIF + "|corrupt|overload";
	public static final String TRIGGER = "MX:|whenever|when|destroyed|revealed|played|died|damaged|kill|drawn|attacked";
	public static final String RACE = "silver hand recruit|beast|demon|dragon|elemental|mech|mechs|naga|orc|pirate|quilboar|totem|undead|murloc|murlocs|lackey|treant|treants|imp|imps|jade golem|elemental|wolf|spider|skeleton|spirit|zombie|shadow";
	public static final String TYPE = "weapon|spell|enchantment|hero power|power|location|minion|card|secret|concoction|class|relic fragment|hero";
	public static final String TARGET = "to|at";// TODO ...
//	static final String TARGET = "opponent|character|enemy|minion";
	public static final String OWNER = "your|friendly|their|opponent";// owner == active player?
	public static final String RANDOM = "random(ly)?";// choose a random one from a candidades list.
	public static final String OTHER = "(an)?other";// other but this
	public static final String ADJACENT = "adjacent";// active player board list+-1
	public static final String DAMAGED = "(un)?damaged";// curHealth != health
	public static final String TOKEN = QUANTIF + "\\/" + QUANTIF;
	public static final String BONUS = "(" + OPERATOR + QUANTIF  + OPERATOR + QUANTIF +")";// "|" + OPERATOR + QUANTIF
	//		+ ")";
	// String overloaded;//mana crystals

	public enum Operator {
		// lowest = cost, health...
		LOWEST("lowest");

		private String regex;

		private Operator(String string) {
			this.regex = string;
		}
	}

	public enum Archtype {
		AGGRO, MIDRANGE, CONTROL, COMBO, FACE, TRIBAL, TEMPO, MILL, FATIGUE, TOKEN, RAMP,;
	}

	public enum Format {
		STANDARD(1), WILD(2);

		private Format(int i) {
			this.valor = i;
		}

		private int valor;

		public static Format getByValor(int v) {
			for (Format f : Format.values()) {
				if (f.valor == v) {
					return f;
				}
			}
			return null;
		}
	}

//	public enum CLASS {
//		NEUTRAL, DRUID, HUNTER, PRIEST, MAGE, SHAMAN, ROGUE, PALADIN, WARLOCK, DEMONHUNTER, WARRIOR, JADE_LOTUS, KABAL,
//		GRIMY_GOONS, PRIEST_WARLOCK, DRUID_HUNTER, HUNTER_DEMONHUNTER, WARLOCK_DEMONHUNTER, PALADIN_PRIEST,
//		PALADIN_WARRIOR, ROGUE_WARRIOR, MAGE_ROGUE, MAGE_SHAMAN, DRUID_SHAMAN,;
//
//		public static boolean contem(CLASS c1, CLASS c2) {
//			switch (c1) {
//			case NEUTRAL:
//				return c2 == NEUTRAL;
//			case JADE_LOTUS:
//				if (c2 == DRUID || c2 == ROGUE || c2 == SHAMAN || c2 == NEUTRAL) {
//					return true;
//				}
//				break;
//			case KABAL:
//				if (c2 == MAGE || c2 == PRIEST || c2 == WARLOCK || c2 == NEUTRAL) {
//					return true;
//				}
//				break;
//			case GRIMY_GOONS:
//				if (c2 == HUNTER || c2 == PALADIN || c2 == WARRIOR || c2 == NEUTRAL) {
//					return true;
//				}
//				break;
//			default:
//				if (c1 == c2 || c2 == NEUTRAL) {
//					return true;
//				}
//				break;
//			}
//			return false;
//		}
//		// @Override
//		// public String toString() {
//		// return this.getName()().toLowerCase().replaceAll("_", " ,
//		// }
//	}
}