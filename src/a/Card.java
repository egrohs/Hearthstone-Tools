package a;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

public class Card {
	String name, set, faction, playerClass, function, type, rarity, text;
	Long cost, attack, health, popularity, combats, wins, draws, loses;
	List<Mechanic> mechanics = new ArrayList<Mechanic>();
//	List<String> triggers = new ArrayList<String>();
//	List<String> actions = new ArrayList<String>();
//	List<String> entities = new ArrayList<String>();
//	List<String> targets = new ArrayList<String>();

	public Card(String name, String set, String faction, String playerClass, String type, String text, Long cost,
			Long attack, Long health) {
		super();
		this.name = name;
		this.set = set;
		this.faction = faction;
		this.playerClass = playerClass;
		this.type = type;
		this.text = text;
		this.cost = cost;
		this.attack = attack;
		this.health = health;
		trim();
	}

	private void trim() {
		if (text != null) {
			// text =
			// StringEscapeUtils.escapeHtml4(text).toLowerCase().replaceAll("\\$",
			// "").replaceAll("\\#", "").trim();
			text = Jsoup.parse(text).text().toLowerCase().replaceAll("\\$", "").replaceAll("\\#", "").trim();
		}
	}
	
	@Override
	public String toString() {
		return name;
	}

//	public void parse(List<String> l) {
//		if (text != null) {
//			String t = " "
//					+ text.toLowerCase().replaceAll("\\)", "").replaceAll("\\(", "").replaceAll("\"", "")
//							.replaceAll("\\’", "").replaceAll("\\'", "").replaceAll("\\;", "").replaceAll("\\:", "")
//							.replaceAll("\\.", "").replaceAll(",", "") + " ";
//			String s = t;
//			for (String k : l) {
//				// adiciona espaço e plural no inicio e final das tags.
//				s = s.replaceAll("\\s" + k + "(s)?\\s", "  ");
//				if (!s.equals(t)) {
//					triggers.add(k);
//				}
//				t = s;
//			}
//		}
//	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRace() {
		return faction;
	}

	public void setRace(String race) {
		this.faction = race;
	}

	public String getClazz() {
		return playerClass;
	}

	public void setClazz(String class1) {
		playerClass = class1;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getCost() {
		return cost;
	}

	public void setCost(Long cost) {
		this.cost = cost;
	}

	public Long getAttack() {
		return attack;
	}

	public void setAttack(Long attack) {
		this.attack = attack;
	}

	public Long getHealth() {
		return health;
	}

	public void setHealth(Long health) {
		this.health = health;
	}
}
