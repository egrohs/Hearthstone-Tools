package hstools.components;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hstools.domain.components.CardService;
import hstools.domain.components.DeckService;
import hstools.domain.entities.Card;
import hstools.domain.entities.Deck;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeckServiceTest {
	@Autowired
	private CardService cb;

	@Autowired
	private DeckService db;

	@Test
	public void testDecode() {
		Deck d = null;
		try {
			d = db.decode("AAEBAR8CjQGiAg7yAagCtQPSA8kEkgWxCNsJ/gz1DfcN2w/UEboTAA==");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Card c : d.getCartas().keySet()) {
			System.out.print("(" + c.getName() + ", " + d.getCartas().get(c) + ") ");
		}
	}
}
