package hstools.domain.entities;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import hstools.Constants.CLASS;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Objeto carta.
 */
@Data
@NodeEntity
@EqualsAndHashCode(callSuper=true)
public class Card extends Node {
	private boolean calculada;
	private CLASS classe;
	private StringBuilder text = new StringBuilder();
	private String cardId, race, type, rarity, mechs, refTags;
	private Expansion expansion;
	private Integer dbfId, cost, attack, health, dur;
	
	@Relationship
	private CardStats stats;
	@Relationship(type = "TAG")
	private Set<Tag> tags = new HashSet<Tag>();
	public Card() {}
	public Card(String cardId, Integer dbfId, String name, String set, String faction, CLASS classe,
			String type, String text, Long cos, Long atta, Long health, Long dur, String rarity, String refTags,
			String mechs) {
		this.cardId = cardId;
		this.dbfId = dbfId;
//		this.getChildren().add(new ImageView(new Image("file:res/cards/" + this.id + ".png")));
//		StackPane.setAlignment(this, Pos.CENTER_LEFT);
		this.name = name.toLowerCase();
		this.expansion = null;
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
		
		this.refTags = refTags;
		this.mechs = mechs;
		trim();
		this.stats = new CardStats();
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
}