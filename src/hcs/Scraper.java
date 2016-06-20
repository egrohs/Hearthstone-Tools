package hcs;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//function depth-limited-crawl(page p, int d)
//if d == 0
//  return
///* do something with p, store it or so */
//foreach (page l in links(p))
//  depth-limited-crawl(linked, d-1)

//Currently supports:
//- arenavalue.com
//- arenamastery.com
//- elitedecks.net
//- gosugamers.net
//- heartharena.com
//- hearthbuilder.com
//- hearthhead.com (including deckbuilder)
//- hearthnews.fr
//- hearthpwn.com (including deckbuilder)
//- hearthstats.net / hss.io
//- hearthstone-decks.com
//- hearthstonechampion.com
//- hearthstoneheroes.com
//- hearthstoneplayers.com
//- hearthstonetopdeck.com
//- hearthstonetopdecks.com
//- hearthstone.judgehype.com
//- hs.inven.co.kr
//- icy-veins.com
//- ls.duowan.com
//- netdeck.n4ru.it
//- playhs.es
//- powned.it
//- pro.eslgaming.com
//- teamarchon.com
//- tempostorm.com
public class Scraper {
	public static void main(String[] args) {
		try {
			String page = "1";
			String url = "http://www.hearthstonetopdecks.com/deck-category/type/page/" + page;
			// login(loginUrl, url);
			Document docDecks = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
					.timeout(3000).post();
			Elements decks = docDecks.select("tbody tr a");
			for (Element deck : decks) {
				String deckLink = deck.attr("href");
				// score : 'td:first-child',
				// deck : 'a@href',
				// classe : 'td:nth-child(3) span',
				// type : 'td:nth-child(5) a',
				Document docDeck = Jsoup.connect(deckLink).data("query", "Java").userAgent("Mozilla")
						.cookie("auth", "token").timeout(3000).post();
				Elements cards = docDeck.select("#deck-master a[data-tooltip-img]");
				for (Element card : cards) {
					String cost = card.select("span.card-cost").text();
					// cost : 'span.card-cost',
					// card : 'span.card-name',
					// count : 'span.card-count',
					System.out.println(cost);
				}
				System.out.println(deckLink);
				// TODO remove
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void login(String loginUrl, String url) throws IOException {
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