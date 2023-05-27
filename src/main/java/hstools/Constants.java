package hstools;

public class Constants {
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