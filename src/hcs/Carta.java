package hcs;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;

/**
 * Objeto carta.
 * 
 * @author 99689650068
 *
 */
public class Carta extends Entidade {
	CLASS classe;
	StringBuilder text = new StringBuilder();
	String id, numid, set, race, function, type, rarity;
	Integer cost, attack, health, dur, popularity, combats, wins, draws, loses;
	boolean aggro/* , visited */;
	Set<Mecanica> mechanics = new HashSet<Mecanica>();
	// Map<Card, Float> synergies = new LinkedHashMap<Card, Float>();

	public enum CLASS {
		NEUTRAL, WARRIOR, DRUID, HUNTER, PRIEST, MAGE, SHAMAN, ROGUE, PALADIN, WARLOCK, JADE_LOTUS, KABAL, GRIMY_GOONS;
		public static boolean contem(CLASS c1, CLASS c2) {
			switch (c1) {
			case NEUTRAL:
				return true;
			case JADE_LOTUS:
				if (c2 == DRUID || c2 == ROGUE || c2 == SHAMAN) {
					return true;
				}
				break;
			case KABAL:
				if (c2 == MAGE || c2 == PRIEST || c2 == WARLOCK) {
					return true;
				}
				break;
			case GRIMY_GOONS:
				if (c2 == HUNTER || c2 == PALADIN || c2 == WARRIOR) {
					return true;
				}
				break;
			default:
				if (c1 == c2) {
					return true;
				}
				break;
			}
			return false;
		}
		// @Override
		// public String toString() {
		// return this.name().toLowerCase().replaceAll("_", " ");
		// }
	}

	public Carta(String id, /* Long numid, */ String name, String set, String faction, CLASS classe, String type,
			String text, Long cos, Long atta, Long health, Long dur, String rarity) {
		super();
		this.id = id.toLowerCase();
		this.name = name.toLowerCase();
		this.set = set;
		this.race = faction;
		this.classe = classe;
		this.type = type;
		this.text.append(text);
		this.cost = (cos == null ? null : Integer.parseInt(cos.toString()));
		this.attack = (atta == null ? null : Integer.parseInt(atta.toString()));
		this.health = (health == null ? null : Integer.parseInt(health.toString()));
		this.dur = (dur == null ? null : Integer.parseInt(dur.toString()));
		this.rarity = rarity;
		if (this.cost > 0 && this.attack != null && this.attack > 2) {
			float agg = ((float) this.attack / this.cost);
			if (agg > 1.5f) {
				aggro = true;
			}
		}
		trim();
	}

	/*
	 * TODO talvez os textos não devem ir pra lowercase, pois palavras curtas
	 * como "all" podem ser dificeis de identificar.
	 */
	private void trim() {
		if (text != null) {
			// text =
			// StringEscapeUtils.escapeHtml4(text).toLowerCase().replaceAll("\\$",
			// "").replaceAll("\\#", "").trim();
			text.append(" - " + race);
			if ("WEAPON".equals(type))
				text.append(" - " + type);
			text = new StringBuilder(Jsoup.parse(text.toString()).text().toLowerCase().replaceAll("\\$", "")
					.replaceAll("\\#", "").trim());
		}
	}

	public void setNumid(String numid) {
		this.numid = numid;
	}

	@Override
	public String toString() {
		return name;
	}
}