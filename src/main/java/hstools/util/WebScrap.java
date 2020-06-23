package hstools.util;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import hstools.domain.entities.Deck;
import hstools.domain.entities.Deck.Formato;
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
	private Formato formato;
	private Map<Integer, Deck> decks = new LinkedHashMap<>();

	@PostConstruct
	public void init() {

	}

	private Document getDocument(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(30000)
					.post();
		} catch (IOException e) {
			try {
				Thread.sleep(2000);
				return getDocument(url);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

	public void hearthstonetopdecksCardRank() {
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
						System.out.print(c.select("a").attr("href") + "\t");
						System.out.println(c.select("strong").text());
					}
				}
				page++;
			} while (page <= pages);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void hearthstonetopdecksDecks() {
		try {
			int page = 1;
			for (int i = page;; i++) {
				String url = "https://www.hearthstonetopdecks.com/decks/page/" + i
						+ "?st&class_id&style_id&t_id&f_id=716&pt_id=1&sort=top-all";
				Document docDecks = getDocument(url);
				Elements decks = docDecks.select("tbody tr td h4 a");
				System.out.println("-------------PAGE" + i + "-----------");
				for (Element deck : decks) {
					String deckLink = deck.attr("href");
					Document docDeck = Jsoup.connect(deckLink).data("query", "Java").userAgent("Mozilla")
							.cookie("auth", "token").timeout(3000).post();
					Elements ideckstring = docDeck.select("input[type=text]");
					System.out.println(ideckstring.val());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	public static Map<String, Tag> importTags() {
		Map<String, Tag> tags = new HashMap<String, Tag>();
		List<List<Object>> values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14", "TAGS!A2:C");
//TODO remover linha vazias vazias null
		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
		} else {
			Long id = 0L;
			for (List<Object> row : values) {
				String name = (String) row.get(0);
				String regex = row.size() > 1 ? (String) row.get(1) : "";
				String expr = row.size() > 2 ? (String) row.get(2) : "";
				String desc = row.size() > 3 ? (String) row.get(3) : "";
				Tag t = tags.get(name);
				if (t == null) {
					tags.put(name, new Tag(id, name, regex, expr, desc));
					id++;
				}
			}
		}
		System.out.println(tags.size() + " tags imported.");
		return tags;
	}
}