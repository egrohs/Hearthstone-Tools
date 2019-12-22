package hstools.model;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.neo4j.ogm.annotation.NodeEntity;

import lombok.Data;

/**
 * Objeto carta.
 */
@Data
@NodeEntity
//@EqualsAndHashCode(callSuper=true)
public class Card extends Node {
	private boolean calculada;
	private CLASS classe;
	private StringBuilder text = new StringBuilder();
	private String numid, set, race, function, type, rarity, mechs, refTags;
	private Integer cost, attack, health, dur, popularity, combats, wins, draws, loses;
	private boolean aggro/* , visited */;
	// Map<Card, Float> synergies = new LinkedHashMap<Card, Float>();
	//@Relationship(type = "TAG")
	private Set<Tag> tags = new HashSet<Tag>();
	private float rank;

	public enum CLASS {
		WARRIOR, DRUID, HUNTER, PRIEST, MAGE, SHAMAN, ROGUE, PALADIN, WARLOCK, JADE_LOTUS, KABAL, GRIMY_GOONS, NEUTRAL;
		public static boolean contem(CLASS c1, CLASS c2) {
			switch (c1) {
			case NEUTRAL:
				return c2 == NEUTRAL;
			case JADE_LOTUS:
				if (c2 == DRUID || c2 == ROGUE || c2 == SHAMAN || c2 == NEUTRAL) {
					return true;
				}
				break;
			case KABAL:
				if (c2 == MAGE || c2 == PRIEST || c2 == WARLOCK || c2 == NEUTRAL) {
					return true;
				}
				break;
			case GRIMY_GOONS:
				if (c2 == HUNTER || c2 == PALADIN || c2 == WARRIOR || c2 == NEUTRAL) {
					return true;
				}
				break;
			default:
				if (c1 == c2 || c2 == NEUTRAL) {
					return true;
				}
				break;
			}
			return false;
		}
		// @Override
		// public String toString() {
		// return this.getName()().toLowerCase().replaceAll("_", " ");
		// }
	}

	public Card(String name) {
		this.name = name;
	}

	public Card(String cod, String name, String set, String faction, CLASS classe, String type, String text, Long cos,
			Long atta, Long health, Long dur, String rarity, String refTags, String mechs) {
		super();
		this.cod = cod.toLowerCase();
//		this.getChildren().add(new ImageView(new Image("file:res/cards/" + this.id + ".png")));
//		StackPane.setAlignment(this, Pos.CENTER_LEFT);
		this.name = name.toLowerCase();
		this.set = set;
		this.race = faction == null ? "" : faction;
		this.classe = classe;
		this.type = type.toLowerCase();
		if (text != null) {
			this.getText().append(Jsoup.parse(text).text().replaceAll(String.valueOf((char) 160), " "));
		}
		this.cost = (cos == null ? -1 : Integer.parseInt(cos.toString()));
		this.attack = (atta == null ? -1 : Integer.parseInt(atta.toString()));
		this.health = (health == null ? null : Integer.parseInt(health.toString()));
		this.dur = (dur == null ? null : Integer.parseInt(dur.toString()));
		this.rarity = rarity;
		if (this.getCost() > 0 && this.getAttack() != null && this.getAttack() > 2) {
			float agg = ((float) this.getAttack() / this.getCost());
			if (agg > 1.5f) {
				aggro = true;
			}
		}
		this.refTags = refTags;
		this.mechs = mechs;
		trim();
	}

	/*
	 * TODO talvez os textos não devem ir pra lowercase, pois palavras curtas como
	 * "all" podem ser dificeis de identificar.
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
					.replaceAll("\\#", "").replaceAll("�", " ").trim());
		}
	}

	public void setNumid(String numid) {
		this.numid = numid;
	}

	@Override
	public boolean equals(Object obj) {
		// boolean r = super.equals(obj);
		// if (r && !id.equals(((Carta) obj).id))
		if (obj instanceof Card && id.equals(((Card) obj).id)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	public void incDraws() {
		this.draws++;
	}

	public void incCombats() {
		this.combats++;
	}

	public void incWins() {
		this.wins++;
	}

	public void incLoses() {
		this.loses++;
	}

	/** Modify the expression replacing variables. */
	// TODO
	public String replaceVars(String expr) {
		String[] tokens = expr.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			String t = tokens[i];
			try {
				Object r = this.getClass().getDeclaredField(t).get(this);
				String o = r == null ? "1000" : r.toString();
				if (o.matches("\\d+")) {
					expr = expr.replaceFirst(t, o);
				} else {
					expr = expr.replaceFirst(t, "\"" + o + "\"");
				}
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		return expr;
	}

	public static void main(String[] args) {
		Card c = new Card("My card", "My card", null, null, null, "spell", null, null, null, null, null, null, null,
				null);
		System.out.println(c.replaceVars("type == \"spell\""));
	}
}