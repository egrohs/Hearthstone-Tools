package hstools.domain.components;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

//import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import hstools.domain.entities.Card;
import hstools.domain.entities.Expansion;
import hstools.domain.entities.Tag;
import hstools.util.GoogleSheets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * For load and build all hs cards, stores all synergies between two cards.
 * Expansions, classes, etc... here??
 * 
 * @author EGrohs
 */
@Slf4j
@Service("Cards")
//TODO retrieve hearthstonetopdecksDecks cards rankings to salve in local file?
//TODO https://hearthstoneapi.com/ retrieve GETInfo types, classes, patch, sets, std, wild, factions, rarity, races...
public class CardComponent {
	static ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
	@Getter
	private List<Expansion> expansions = new ArrayList<>();
	@Getter
	private List<Card> cards = new ArrayList<>();
	@Getter
	private Map<String, Tag> tags = new HashMap<>();

//	@Autowired
//	private CardRepository cRepo;

	@Autowired
	private SynergyBuilder synn;

	@Autowired
	private FilesComponent files;

	// TODO armazenar localmente as tags evitando buscar se mesma versao ou sem
	// internet.
	/** Import tags from google spreadsheet. */
	public Map<String, Tag> loadTags() {
		// Map<String, Tag> tags = new HashMap<>();
		// if (tags.size() == 0)
		{
			// Map<String, Tag> tags = new HashMap<String, Tag>();
			List<List<Object>> values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14",
					"TAGS!A2:C");
			// TODO remover linha vazias null
			if (values == null || values.isEmpty()) {
				System.out.println("No data found.");
			} else {
				for (List<Object> row : values) {
					String name = (String) row.get(0);
					String regex = row.size() > 1 ? (String) row.get(1) : "";
					String expr = row.size() > 2 ? (String) row.get(2) : "";
					String desc = row.size() > 3 ? (String) row.get(3) : "";
					Tag t = tags.get(name);
					if (t == null) {
						tags.put(name, new Tag(name, regex, expr, desc));
					}
				}
			}
		}
		System.out.println(tags.size() + " tags imported.");
		return tags;
	}

	public void importTags() {
		if (tags.size() == 0) {
			JSONObject jo = (JSONObject) files.file2JSONObject("src/main/resources/synergy/tag-synergies.json");
			JSONArray nodes = (JSONArray) jo.get("nodes"), links = (JSONArray) jo.get("links");
			if (nodes == null || nodes.isEmpty()) {
				System.out.println("No data found.");
			} else {
				Iterator<JSONObject> iterator = nodes.iterator();
				while (iterator.hasNext()) {
					JSONObject o = iterator.next();
					try {
						String name = (String) o.get("id");
						String regex = (String) o.get("regex");
						String expr = (String) o.get("expr");
						// "group":1
						// (Integer) o.get("group");
						tags.put(name, new Tag(name, regex, expr, ""));
					} catch (RuntimeException e) {
						// TODO: handle exception
					}
				}
			}
			synn.loadTagsSynergies(links);
			System.out.println(tags.size() + " tags imported.");
		}
	}

	public void printNodesJson() {
		System.out.println("{\r\n" + "  \"nodes\":  [");
		for (Tag t : tags.values()) {
			System.out.println("{ \"name\": \"" + t.getName() + "\",     \"group\":  1 },");
		}
		System.out.println(" ],");
	}

//	@PostConstruct
//	public void init() {
////		Iterable<Card> iterable = () -> cRepo.findAll().iterator();
////		cards = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
////		System.out.println(cards.size() + " cards loaded.");
//
//		// expansions = web.wikipediaExpansions();
//		// importCards();
//		buildCards();
//		WebScrap.importTags(tags);
//		buildAllCardTags();
//		// importCardRanks();
//
////		for (Card card : cards) {
////			cRepo.save(card);
////		}
//	}

	/**
	 * Import all card ranks form google sheet
	 */
	public void importCardRanks() {
		List<List<Object>> values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14", "CARDS!A2:F");
		int count = 0;
		// TODO buscar pelo nome do header da coluna
		for (List<Object> row : values) {
			String cardName = (String) row.get(0);
			Float rank = Float.valueOf((String)row.get(2));
			Card c1 = getCard(cardName);
			if (c1 != null) {
				c1.getStats().setRank(rank);
				count++;
			}
		}
		System.out.println(count + " card ranks imported.");
	}

//	private int containsTag(Tag tag) {
//		int i = 0;
//		for (Card card : cards) {
//			if (card.getTags().contains(tag)) {
//				i++;
//				System.out.println(card.getName());
//			}
//		}
//		System.out.println(i);
//		return i;
//	}

	/**
	 * Load json card db api in memory. Using hearthstonejson cause it has a
	 * separated db file of only colletionable cards.
	 */
	public List<Card> buildCards() {
		// TODO nao usar mais essa api e sim
		// https://rapidapi.com/omgvamp/api/hearthstone
		// final String api =
		// "https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json";
		if (cards.isEmpty()) {
			try {
				ObjectMapper om = new ObjectMapper();
				String jsonCards = Files.readString(Path.of("cards.collectible.json"), Charset.defaultCharset());
				JsonNode root = om.readTree(jsonCards);
				// Iterate over the nodes.
				Set<String> cardNames = new HashSet<>();
				for (JsonNode sets : root) {
					for (JsonNode n : sets) {
						Card card = om.readValue(n.toString(), Card.class);
						card.setName(card.getName().trim());
						addNonRepeatedCard(cardNames, card);
					}
				}

				cards.add(new Card("GAME_005", 1746, "the coin", "CORE", "ALLIANCE", "Neutral", "SPELL",
						"Add 1 mana this turn...", 0L, null, null, null, "COMMON", "", ""));

				cards.forEach(c -> {
					if ("Minion".equalsIgnoreCase(c.getType()) && c.getAttack() != null && c.getHealth() != null
							&& c.getCost() != null) {
						c.getStats().setStats_cost((float) (c.getAttack() + c.getHealth()) / (c.getCost() + 1));
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(cards.size() + " cards created.");
		}
		return cards;
	}

	private void addNonRepeatedCard(Set<String> cardNames, Card card) {
		card.trimText(card.getText().toString());
		if ("enUS".equals(card.getLocale())) {
//						locale enUS, set wild, 
//						"cardSet":"Basic"
			String cn = card.getName().toLowerCase();
			if (!cardNames.contains(cn)) {
				cards.add(card);
				cardNames.add(cn);
			} else {
				// System.out.println(card.getName());
				card.getDbfIds().add(card.getDbfId());
			}
		}
	}

	/**
	 * Find a card by name or id.
	 * 
	 * @param idsORname
	 * @return Card.
	 */
	public Card getCard(String idsORname) {
		idsORname = idsORname.trim().replace("’", "'");
		if (idsORname != null && !"".equals(idsORname)) {
			for (Card c : cards) {
				if (c.getName().equalsIgnoreCase(idsORname)
						|| c.getName().replace("'|\\.|\\,", "").equalsIgnoreCase(idsORname)
						|| c.getName().replace("'|\\.|\\,", "").equalsIgnoreCase(idsORname.replace("-", " "))) {
					return c;
				}
				try {
					if (c.getDbfIds().contains(Integer.parseInt(idsORname))) {
						return c;
					}
				} catch (NumberFormatException e) {
					// deve ser busca por id
				}
				if (c.getId() != null && c.getId().toString().equalsIgnoreCase(idsORname)) {
					return c;
				}
				if (c.getCardId().equalsIgnoreCase(idsORname)) {
					return c;
				}
			}
		}
		// TODO CS2_013t excess mana not found..
		throw new RuntimeException("Card not found: " + idsORname);
		//System.err.println("Card not found: " + idsORname);
		//return getCard("idsORname");
	}

	/**
	 * Generate all cards Tags.
	 */
	public void buildAllCardTags() {
		long time = System.currentTimeMillis();
		int acum = 0;
		for (Card c : getCards()) {
			acum += buildCardTags(c);
		}
		System.out.println(acum + " tags built in " + (System.currentTimeMillis() - time) + " milisecs.");
	}

	public int buildCardTags(Card c) {
		int acum = 0;
		if (c.getTags().isEmpty()) {
			for (Tag tag : tags.values()) {
				String expr = c.replaceVars(tag.getExpr());// .replaceAll("\\'", "\"");
				try {
					if (
					// (expr == null || expr.equals("") || (boolean) jsEngine.eval(expr) == true)&&
					tag.getRegex() != null && !tag.getRegex().equals("")
							&& Pattern.compile(tag.getRegex()).matcher(c.getText()).find()) {
						c.addTag(tag);
						acum++;
						// System.out.println(c + "\thas\t" + tag);
					}
				} catch (Exception e) {
					// e.printStackTrace();
					System.err.println("FAIL: " + c + "\thas?\t" + tag + " EXPR: " + expr);
				}
			}
		}
		return acum;
	}
}