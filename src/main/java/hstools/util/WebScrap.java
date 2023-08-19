package hstools.util;

import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hstools.Constants.Format;
import hstools.domain.components.CardComponent;
import hstools.domain.entities.Deck;
import hstools.domain.entities.Expansion;

/**
 * Scrap all online info about hs.
 * 
 * @author EGrohs
 *
 */
@Component("WebScrap")
public class WebScrap {
	// TODO download do meta atual wild
	// https://tempostorm.com/hearthstone/meta-snapshot/wild
	// https://www.vicioussyndicate.com/wild-drr
	private LocalDate date;
	private Format format;
	private Map<Integer, Deck> decks = new LinkedHashMap<>();
	
	@Autowired
	private CardComponent ccomp;

	// @PostConstruct
	public void init() {

	}
	
	public void scrapCardRanks() {
		Map<String, Float> ranks = WebScrap.hearthstonetopdecksCardRank();
		for (String url : ranks.keySet()) {
			String[] tks = url.split("/");
			String cname = tks[tks.length - 1];
			System.out.println(ccomp.getCard(cname));
		}
	}

	private static Document getDocument(String url) {
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

	public static void main(String[] args) {
		// WebScrap.hearthstonetopdecksCardRank();
		WebScrap.hearthstonefandomcom();
	}
	
	public static void hearthstonefandomcom() {
		try {
			String url = "https://hearthstone.fandom.com/wiki/Kirin_Tor_Tricaster";
			//do {
				Document docRanks = getDocument(url);
				//class="to_hasTooltip"
				Elements cards = docRanks.getElementsByClass("to_hasTooltip");
				for (Element c : cards) {
					String title = c.select("a").attr("title");
					System.out.println(title);
				}
			//} while (page <= pages);
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
String url = "https://www.hearthstonetopdecks.com/decks/page/" + i + "/?st=&class_id=&style_id=&t_id=270&f_id=716&pt_id=1&sort=top-all";
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
}