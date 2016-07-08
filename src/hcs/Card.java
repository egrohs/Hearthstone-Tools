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
public class Card extends Entidade {
	StringBuilder text = new StringBuilder();
	String id, numid, set, race, playerClass, function, type, rarity;
	Integer cost, attack, health, dur, popularity, combats, wins, draws, loses;
	boolean aggro/*, visited*/;
	Set<Mechanic> mechanics = new HashSet<Mechanic>();
//	Map<Card, Float> synergies = new LinkedHashMap<Card, Float>();

	public Card(String id, /* Long numid, */ String name, String set, String faction, String playerClass, String type,
			String text, Long cos, Long atta, Long health, Long dur, String rarity) {
		super();
		this.id = id;
		this.name = name;
		this.set = set;
		this.race = faction;
		this.playerClass = playerClass;
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