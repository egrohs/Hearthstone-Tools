package hstools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import hstools.domain.components.CardService;
import hstools.domain.components.DataScienceService;
import hstools.domain.components.DeckService;
import hstools.domain.components.ScrapService;
import hstools.domain.components.TagBuilder;

@SpringBootApplication // (scanBasePackages = { "hstools.components" })
@ComponentScan("hstools.domain.components")
public class HearthstoneToolsApplication implements CommandLineRunner {
	// @Autowired
	// private EntityService neo4j;
	// private Session neo4j;
	// private SessionFactory neo4j;

	@Autowired
	private CardService cb;

	@Autowired
	private DeckService db;

	@Autowired
	private TagBuilder tb;
	
	@Autowired
	private DataScienceService ss;
	
	@Autowired
	private ScrapService scrap;

	public static void main(String[] args) {
		SpringApplication.run(HearthstoneToolsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		cb.buildCards();
//		cb.hearthstonetopdecksCardRank();
//scrap.wikipediaExpansions();
//
//		ClassLoader cl = this.getClass().getClassLoader();
//		db.loadDecks(new File(cl.getResource("decks").getFile()));
//
//		tb.importTags2();
//		tb.buildCardTags();
//		for (Deck dk : db.getDecks()) {
//			dk.calcTags();
//			// System.out.print(dk.getName() + "\t");
//			dk.stats();
//		}

//		neo4j.createOrUpdate(new Card("carta teste"));

//		db.loadProDecks();
//		ss.matrix();
	}
}