package hstools.domain.components;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import hstools.domain.entities.Card;
import hstools.domain.entities.Expansion;
import hstools.domain.entities.Tag;
import hstools.util.GoogleSheets;
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
	static ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
	@Getter
	private List<Expansion> expansions = new ArrayList<Expansion>();
	@Getter
	private List<Card> cards = new ArrayList<Card>();
	@Getter
	private static Map<String, Tag> tags = new HashMap<String, Tag>();

//	@Autowired
//	private CardRepository cRepo;

	@Autowired
	private SynergyBuilder synn;

	@Autowired
	private FilesComponent files;

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
		List<List<Object>> values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14", "CARDS!A2:E");
		int count = 0;
		// TODO buscar pelo nome do header da coluna
		for (List<Object> row : values) {
			String cardName = (String) row.get(0);
			Float rank = (Float) row.get(3);
			Card c1 = getCard(cardName);
			if (c1 != null) {
				c1.getStats().setRank(rank);
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
		// TODO nao usar mais essa api e sim
		// https://rapidapi.com/omgvamp/api/hearthstone
		// final String api =
		// "https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json";
		if (cards.size() == 0) {
			try {
				ObjectMapper om = new ObjectMapper();
				String jsonCards = Files.readString(Path.of("cards.collectible.json"), Charset.defaultCharset());
				JsonNode rootNode = om.readTree(jsonCards);
				Iterator<JsonNode> iter = rootNode.elements();
				while (iter.hasNext()) {
					ArrayNode ja = (ArrayNode) iter.next();
					List<Card> pojos = om.readValue(ja.toString(), new TypeReference<List<Card>>() {
					});
					cards.addAll(pojos);
				}
				cards.add(new Card("GAME_005", 1746, "the coin", "CORE", "ALLIANCE", "Neutral", "SPELL",
						"Add 1 mana this turn...", 0L, null, null, null, "COMMON", "", ""));

				cards.forEach(c -> {
					if ("Minion".equalsIgnoreCase(c.getType())) {
						c.getStats().setStats_cost((float) (c.getAttack() + c.getHealth()) / (c.getCost() + 1));
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
//			try {
//				File file = new File("cards.collectible.json");
//				if (!file.exists()) {
//					// file.delete();
//					Files.copy(new URL(api).openStream(), Paths.get("cards.collectible.json"));
//				}
			// JSONArray sets = (JSONArray) Util.file2JSONObject(file.getName());
			// generateCards(sets);
//				List<String> w = HearthstoneToolsApplication.rapidApiInfo.getWild();

//				ObjectMapper objectMapper = new ObjectMapper();
//for (String set : w) 
			{
//	JsonNode rootNode = objectMapper.readTree("jsonString");
//	JsonNode nset = rootNode.get(set);
				// HearthstoneToolsApplication.cs.
//				CardSets cs = Util.file2Cards("cards.collectible.json");
				// ModelMapper mm = new ModelMapper();
				// mm.i
				// Card[] cass = mm.map(cs, Card[].class);
			}
			// for... info.getSets()){
			//
			// TODO juntar todas listas e jogar em cards
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			System.out.println(cards.size() + " cards created.");
		}
		return cards;
	}

	/**
	 * Find a card by name or id.
	 * 
	 * @param idsORname
	 * @return Card.
	 */
	public Card getCard(String idsORname) {
		idsORname = idsORname.trim().replaceAll("’", "'");
		if (idsORname != null && !"".equals(idsORname)) {
			for (Card c : cards) {
				if (c.getName().equalsIgnoreCase(idsORname)
						|| c.getName().replaceAll("'|\\.|\\,", "").equalsIgnoreCase(idsORname)
						|| c.getName().replaceAll("'|\\.|\\,", "").equalsIgnoreCase(idsORname.replaceAll("-", " "))) {
					return c;
				}
				if (c.getDbfId().toString().equalsIgnoreCase(idsORname)) {
					return c;
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
		// System.err.println("Card not found: " + idsORname);
		// return getCard("The Coin");
	}

	/**
	 * Generate all cards Tags.
	 */
	public void buildAllCardTags() {
		int acum = 0;
		for (Card c : getCards()) {
			acum += buildCardTags(c);
		}
		System.out.println(acum + " tags created.");
	}

	public static int buildCardTags(Card c) {
		int acum = 0;
		if (c.getTags().size() == 0) {
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

	public void buildCardRanks() {
		Map<String, Float> ranks = WebScrap.hearthstonetopdecksCardRank();
		for (String url : ranks.keySet()) {
			String[] tks = url.split("/");
			String cname = tks[tks.length - 1];
			System.out.println(getCard(cname));
		}
	}
}