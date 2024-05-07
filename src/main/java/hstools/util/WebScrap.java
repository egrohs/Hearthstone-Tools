package hstools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import hstools.Constants.Format;
import hstools.domain.entities.Deck;
import hstools.domain.entities.Expansion;
import hstools.domain.entities.Tag;

/**
 * Scrap all online info about hs.
 * 
 * @author EGrohs
 *
 */
//@Component("WebScrap")
public class WebScrap {
	// TODO download do meta atual wild
	// https://tempostorm.com/hearthstone/meta-snapshot/wild
	// https://www.vicioussyndicate.com/wild-drr
	private LocalDate date;
	private Format format;
	private Map<Integer, Deck> decks = new LinkedHashMap<>();

	// @PostConstruct
	public void init() {

	}

	private static Document getDocument(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(10000)
					.post();
		} catch (IOException e) {
//			try {
//				Thread.sleep(2000);
//				return getDocument(url);
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}
			System.err.println("ERRO " + url);
		}
		return doc;
	}

	public List<Expansion> wikipediaExpansions() {
		List<Expansion> exps = new ArrayList<Expansion>();
		String url = "https://en.wikipedia.org/wiki/Hearthstone";
		Document docRanks = getDocument(url);
		Elements names = docRanks.select("table.wikitable.plainrowheaders tbody tr th[scope]");
		Elements releases = docRanks.select("table.wikitable.plainrowheaders tbody tr td:nth-child(3)");
		Elements endStd = docRanks.select("table.wikitable.plainrowheaders tbody tr td:nth-child(4)");
		int i = 0;
		for (Element n : names) {
			if (n.attributes().size() == 1) {
				System.out.println(n.text() + "\t" + releases.get(i).text() + "\t" + endStd.get(i).text());
				exps.add(new Expansion(n.text(), releases.get(i).text(), endStd.get(i).text()));
				i++;
			}
		}
		return exps;
	}

	public static void main(String[] args) {
		WebScrap.hearthstonefandomArchetypes();
	}

	public static void hearthstonefandomArchetypes() {
		try {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(WebScrap.class.getClassLoader().getResourceAsStream("urls.txt")))) {
				String line;
				while ((line = br.readLine()) != null) {
					Document doc = getDocument(line);
					if (doc == null)
						continue;
					Elements cards = doc.select("a");
					for (Element c : cards) {
						String title = c.select("a").attr("title");
						System.out.println(line.substring(line.lastIndexOf("/") + 1) + "\t" + title);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void hearthstonefandomStrategy() {
		try {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(WebScrap.class.getClassLoader().getResourceAsStream("urls.txt")))) {
				String line;
				// System.out.println("graph graphname {");
				while ((line = br.readLine()) != null) {
					Document doc = getDocument(line);
					if (doc == null)
						continue;
					Elements cards = doc.getElementsByClass("to_hasTooltip");
					for (Element c : cards) {
						String title = c.select("a").attr("title");
						System.out.println(line.substring(line.lastIndexOf("/") + 1) + " -- " + title);
					}
				}
				System.out.println("}");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Float> hearthstonetopdecksCardRank() {
		Map<String, Float> ranks = new HashMap<String, Float>();
		try {
			String url = "https://www.hearthstonetopdecks.com/cards/page/";
			int page = 1;
			int pages = 50;
			do {
				Document docRanks = getDocument(url + page);
				// pages =
				// Integer.parseInt(docDecks.select("span.page-link.pages").text().split("
				// ")[2]);
				if (docRanks != null) {
					Elements cards = docRanks.select("div.card-item");
					for (Element c : cards) {
						String key = c.select("a").attr("href");
						Float rank = Float.parseFloat(c.select("strong").text());
						ranks.put(key, rank);
						System.out.print(key + "\t");
						System.out.println(rank);
					}
				}
				page++;
			} while (page <= pages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ranks;
	}

	/**
	 * Download web hearthstone decks.
	 */
	// function depth-limited-crawl(page p, int d)
	// if d == 0
	// return
	/// * do something with p, store it or so */
	// foreach (page l in links(p))
	// depth-limited-crawl(linked, d-1)

	// Currently supports:
	// - arenavalue.com
	// - arenamastery.com
	// - elitedecks.net
	// - gosugamers.net
	// - heartharena.com
	// - hearthbuilder.com
	// - hearthhead.com (including deckbuilder)
	// - hearthnews.fr
	// - hearthpwn.com (including deckbuilder)
	// - hearthstats.net / hss.io
	// - hearthstone-decks.com
	// - hearthstonechampion.com
	// - hearthstoneheroes.com
	// - hearthstoneplayers.com
	// - hearthstonetopdeck.com
	// - hearthstonetopdecks.com
	// - hearthstone.judgehype.com
	// - hs.inven.co.kr
	// - icy-veins.com
	// - ls.duowan.com
	// - netdeck.n4ru.it
	// - playhs.es
	// - powned.it
	// - pro.eslgaming.com
	// - teamarchon.com
	// - tempostorm.com
	public void tempostormMeta() {
		String url = "https://tempostorm.com/hearthstone/meta-snapshot/wild/2019-12-17";
		Document docMeta = getDocument(url);
		Elements tempostormDecks = docMeta.select("hs-snapshot-body div div div div div div div div a");
	}

	public static void hearthstonetopdecksDecks() {
		for (int i = 1; i < 3; i++) {
			try {
//				String url = "https://www.hearthstonetopdecks.com/decks/page/" + i + "/?st&class_id&style_id&t_id&f_id=716&pt_id=1&sort=top-all";
				String url = "https://www.hearthstonetopdecks.com/decks/page/" + i
						+ "/?st=&class_id=&style_id=&t_id=270&f_id=716&pt_id=1&sort=top-all";
				Document docDecks = getDocument(url);
				Elements decks = docDecks.select("tbody tr td h4 a");
				System.out.println("-------------PAGE " + i + " -----------");
				for (Element deck : decks) {
					try {
						String deckLink = deck.attr("href");
						Document docDeck = Jsoup.connect(deckLink).data("query", "Java").userAgent("Mozilla")
								.cookie("auth", "token").timeout(3000).post();
						Element deckType = docDeck.select("strong:contains(type)").first();
						Elements deckstring = docDeck.select("input[type=text]");
						System.out.println(deckstring.val() + "\t"
								+ (deckType != null ? deckType.nextElementSibling().text().toUpperCase() : ""));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void login(String loginUrl, String url) throws IOException {
		// First login. Take the cookies
		Connection.Response res = Jsoup.connect(loginUrl).data("eid", "i110013").data("pw", "001")
				.referrer("http://www.google.com")
				.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				.method(Method.POST).timeout(0).execute();

		Map<String, String> loginCookies = res.cookies();

		// Now you can parse any page you want, as long as you pass the
		// cookies
		Document doc = Jsoup.connect(url).timeout(0).cookies(loginCookies).referrer("http://www.google.com")
				.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				.get();

		System.out.println("Title : " + doc.title());
	}

	// TODO armazenar localmente as tags evitando buscar se mesma versao ou sem
	// internet.
	/** Import tags from google spreadsheet. */
	public static void importTags(Map<String, Tag> tags) {
		if (tags.size() == 0) {
			// Map<String, Tag> tags = new HashMap<String, Tag>();
			List<List<Object>> values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14",
					"TAGS!A2:C");
//TODO remover linha vazias vazias null
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
	}
}