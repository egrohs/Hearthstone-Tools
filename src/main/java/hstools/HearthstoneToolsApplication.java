package hstools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hstools.domain.components.ArtificialNeuralNetwork;
import hstools.domain.components.CardComponent;
import hstools.domain.components.DeckComponent;
import hstools.domain.entities.Deck;
import hstools.repositories.CardRepository;

@SpringBootApplication // (scanBasePackages = { "hstools.components" })
//@ComponentScan("hstools.domain.components")
//@EnableNeo4jRepositories("hstools.repositories")
public class HearthstoneToolsApplication implements CommandLineRunner {
	@Autowired
	private CardComponent cardComp;

	@Autowired
	private DeckComponent deckComp;
//
//	@Autowired
//	private SynergyBuilder synComp;
//
//	@Autowired
//	private DataScienceComponent scienceComp;

	@Autowired
	private ArtificialNeuralNetwork annComp;
	
	@Autowired
	private CardRepository cRepo;

	public static void main(String[] args) {
		SpringApplication.run(HearthstoneToolsApplication.class, args);
	}

//	@Bean
//	public Configuration getConfiguration() {
//		String uri = getDatabaseUri();
//		Configuration config = new Configuration();
//		config.set("dbms.allow_format_migration", "true"); // Allow upgrade of neo4j version
//		config.driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
//				.setURI(uri);
//		return config;
//	}

	@Override
	public void run(String... args) throws Exception
	{
		Deck deck = deckComp.decodeDeckString(
		"AAEBAR8CxQiG0wIOqAK1A+sH2wntCf4Mzq4CubQC6rsC7LsCicMCjsMCps4Cxs4CAA==");
		annComp.classifyDeck(deck);

		// ss.tagsAffin();
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

//		ss.matrix();
	}
}