package hstools.domain.components;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
//import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hstools.domain.entities.Card;
import hstools.domain.entities.Card.CLASS;
import hstools.domain.entities.Expansion;
import hstools.domain.entities.Tag;
import hstools.repositories.CardRepository;
import hstools.util.GoogleSheets;
import hstools.util.Util;
import hstools.util.WebScrap;
import lombok.Getter;

/**
 * For load and build all hs cards, stores all synergies between two cards.
 * Expansions, classes, etc... here??
 * 
 * @author EGrohs
 */
@Service("Cards")
//TODO retrieve hearthstonetopdecksDecks cards rankings to salve in local file?
//TODO https://hearthstoneapi.com/ retrieve GETInfo types, classes, patch, sets, std, wild, factions, rarity, races...
public class CardComponent {
	@Getter
	private List<Expansion> expansions = new ArrayList<Expansion>();
	@Getter
	private List<Card> cards = new ArrayList<Card>();
	@Getter
	private Map<String, Tag> tags = new HashMap<String, Tag>();
	
	@Autowired
	private CardRepository cRepo;

	@PostConstruct
	public void init() {
		// expansions = web.wikipediaExpansions();
		// importCards();
		buildCards();
		tags = WebScrap.importTags();
		buildCardTags();
		// importCardRanks();
	}

	/**
	 * Import all card ranks form google sheet
	 */
	public void importCardRanks() {
		List<List<Object>> values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14", "CARDS!A2:E");
		int count = 0;
		// TODO buscar pelo nome do header da coluna
		for (List<Object> row : values) {
			String cardName = (String) row.get(0);
			Float rank = (Float) row.get(3);
			Card c1 = getCard(cardName);
			if (c1 != null) {
				c1.setRank(rank);
				count++;
			}
		}
		System.out.println(count + " card ranks imported.");
	}

	private int containsTag(Tag tag) {
		int i = 0;
		for (Card card : cards) {
			if (card.getTags().contains(tag)) {
				i++;
				System.out.println(card.getName());
			}
		}
		System.out.println(i);
		return i;
	}

	/**
	 * Load json card db api in memory. Using hearthstonejson cause it has a
	 * separated db file of only colletionable cards.
	 */
	public List<Card> buildCards() {
		final String api = "https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json";
		if (cards.size() == 0) {
			try {
				File file = new File("cards.collectible.json");
				if (!file.exists()) {
					// file.delete();
					Files.copy(new URL(api).openStream(), Paths.get("cards.collectible.json"));
				}
				JSONArray sets = (JSONArray) Util.file2JSONObject(file.getName());
				generateCards(sets);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(cards.size() + " cards created.");
		}
		return cards;
	}

	/**
	 * Parse json array and create card objects.
	 * 
	 * @param array JSONObject with cards data.
	 */
	private final void generateCards(JSONArray array) {
		Iterator<JSONObject> iterator = array.iterator();
		while (iterator.hasNext()) {
			JSONObject o = iterator.next();
			Boolean col = (Boolean) o.get("collectible");
			if (col != null && col == true /*
											 * && !"HERO".equals((String) o.get("type"))
											 */) {
				Card.CLASS classe;
				String c = (String) o.get("multiClassGroup");
				if (c == null) {
					c = (String) o.get("cardClass");
				}
				if (c == null) {
					c = (String) o.get("playerClass");
				}
				classe = Card.CLASS.valueOf(c);

				// TODO card mechanics??
				// List<String> mechs = (List<String>) o.get("mechanics");

				String text = (String) o.get("text");
				try {
					if (text != null) {
						text = new String(text.getBytes("ISO-8859-1"), "UTF-8");
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JSONArray reftags = (JSONArray) o.get("referencedTags");
				JSONArray mechanics = (JSONArray) o.get("mechanics");

				cards.add(new Card((String) o.get("id"), ((Long) o.get("dbfId")).intValue(), (String) o.get("name"),
						(String) o.get("set"), (String) o.get("race"), classe, (String) o.get("type"), text,
						(Long) o.get("cost"), (Long) o.get("attack"), (Long) o.get("health"),
						(Long) o.get("durability"), (String) o.get("rarity"), reftags == null ? "" : reftags.toString(),
						mechanics == null ? "" : mechanics.toString()));
			}
		}
		// if (getCard("The Coin") == null)
		{
			// TODO adiciona a moeda
			cards.add(new Card("GAME_005", 1746, "the coin", "CORE", "ALLIANCE", CLASS.NEUTRAL, "SPELL",
					"Add 1 mana this turn...", 0L, null, null, null, "COMMON", "", ""));
		}
		Collections.sort(cards);
	}

	/**
	 * Find a card by name or id.
	 * 
	 * @param idsORname
	 * @return Card.
	 */
	public Card getCard(String idsORname) {
		if (idsORname != null && !"".equals(idsORname)) {
			for (Card c : cards) {
				if (c.getName().equalsIgnoreCase(idsORname.trim().replaceAll("’", "'"))) {
					return c;
				}
				if (c.getDbfId().toString().equalsIgnoreCase(idsORname)) {
					return c;
				}
				if (c.getId().toString().equalsIgnoreCase(idsORname)) {
					return c;
				}
				if (c.getCardId().equalsIgnoreCase(idsORname)) {
					return c;
				}
			}
		}
		// TODO CS2_013t excess mana not found..
		throw new RuntimeException("Card not found: " + idsORname);
		// return null;
	}

	/**
	 * Generate all cards Tags.
	 */
	public void buildCardTags() {
		ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
		try {
			for (Card c : getCards()) {
				for (Tag tag : tags.values()) {
					String expr = c.replaceVars(tag.getExpr());
					if ((expr == null || expr.equals("") || (boolean) jsEngine.eval(expr) == true)
							&& Pattern.compile(tag.getRegex()).matcher(c.getText()).find()) {
						c.getTags().add(tag);
					}
				}
			}
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(tags.keySet().size() + " tags created.");
	}
}