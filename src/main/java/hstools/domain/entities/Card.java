package hstools.domain.entities;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Objeto carta.
 */
@Data
//@NodeEntity
@EqualsAndHashCode(of = { "cardId" }, callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class Card extends Node {
	private boolean calculada;
//	"playerClass": "Demon Hunter",
//	"multiClassGroup": "Hunter Demon Hunter",
//    "classes": [
//      "Hunter",
//      "Demon Hunter"
//    ],
	private String playerClass;

	// @Override
	public void setPlayerClass(String playerClass) {
		this.playerClass = playerClass;
		classes.add(playerClass);
	}

	private Set<String> classes = new HashSet<>();
	private StringBuilder text = new StringBuilder();
	private String cardId, race, type, rarity, mechs, refTags, locale;
	@JsonProperty("cardSet")
	private Expansion expansion;
	private Set<Integer> dbfIds = new HashSet<>();
	@JsonProperty("durability")
	private Integer dur;
	private Integer dbfId, cost, attack, health;
	// @Relationship
	private CardStats stats = new CardStats();
	// @Relationship(type = "TAG")
	private Set<Tag> tags = new HashSet<Tag>();
	// @JsonProperty("mechanics.name")
	// private Set<String> mechanics;
	private Set<Mechanic> mechanics = new HashSet<>();
	// private Set<SynergyEdge<Card, Tag>> tags = new HashSet<SynergyEdge<Card,
	// Tag>>();

//	public Set<Tag> getTags() {
//		if (text.toString() != null && !"".equals(text.toString())) {
//			CardComponent.buildCardTags(this);
//		}
//		return tags;
//	}

	public void addTag(Tag t) {
		tags.add(t);
	}

	public Card(String cardId, Integer dbfId, String name, String set, String faction, String classe, String type,
			String text, Long cos, Long atta, Long health, Long dur, String rarity, String refTags, String mechs) {
		this.cardId = cardId;
		this.dbfIds.add(dbfId);
//		this.getChildren().add(new ImageView(new Image("file:res/cards/" + this.id + ".png")));
//		StackPane.setAlignment(this, Pos.CENTER_LEFT);
		this.name = name.toLowerCase();
		this.expansion = null;
		this.race = faction == null ? "" : faction;
		// this.classe = classe;
		this.type = type.toLowerCase();
		this.cost = (cos == null ? -1 : Integer.parseInt(cos.toString()));
		this.attack = (atta == null ? -1 : Integer.parseInt(atta.toString()));
		this.health = (health == null ? null : Integer.parseInt(health.toString()));
		this.dur = (dur == null ? null : Integer.parseInt(dur.toString()));
		this.rarity = rarity;

		this.refTags = refTags;
		this.mechs = mechs;
		trimText(text);
		this.stats = new CardStats();
		if ("MINION".equalsIgnoreCase(this.type)) {
			this.stats.setStats_cost((float) (this.attack + this.health) / (this.cost + 1));
		}
	}

	/*
	 * TODO talvez os textos não devem ir pra lowercase, pois palavras curtas como
	 * "all" podem ser dificeis de identificar.
	 */
	public void trimText(String t) {
		if (t != null) {
			t = Jsoup.parse(t).text().replaceAll(String.valueOf((char) 160), " ");
			// text =
			// StringEscapeUtils.escapeHtml4(text).toLowerCase().replaceAll("\\$",
			// "").replaceAll("\\#", "").trim();
			t += " - " + race;
			// if ("WEAPON".equals(type))
			t += " - " + type;
			this.text = new StringBuilder(Jsoup.parse(t).text().toLowerCase().replace("\\$", "")
					.replace("\\#", "").replace("�", " ").replace("_", " ").trim());
		}
	}

	/** Modify the expression replacing variables. */
	// TODO
	public String replaceVars(String expr) {
		if (expr != null) {
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
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}
		return expr;
	}

//	public String toString() {
//		return name;
//	}
}