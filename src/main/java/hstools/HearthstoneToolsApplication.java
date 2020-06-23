package hstools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import hstools.domain.components.ArtificialNeuralNetwork;
import hstools.domain.components.CardComponent;
import hstools.domain.components.DataScienceComponent;
import hstools.domain.components.DeckComponent;
import hstools.domain.components.SynergyBuilder;
import hstools.domain.entities.Deck;

@SpringBootApplication // (scanBasePackages = { "hstools.components" })
@ComponentScan("hstools.domain.components")
public class HearthstoneToolsApplication implements CommandLineRunner {
	// @Autowired
	// private EntityService neo4j;
	// private Session neo4j;
	// private SessionFactory neo4j;

	@Autowired
	private CardComponent cardComp;

	@Autowired
	private DeckComponent deckComp;

	@Autowired
	private SynergyBuilder synComp;

	@Autowired
	private DataScienceComponent scienceComp;
	
	@Autowired
	private ArtificialNeuralNetwork annComp;

	public static void main(String[] args) {
		SpringApplication.run(HearthstoneToolsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//deckComp.decodeDecksFromFile("commonHSTopDecks.txt");
		//annComp.generateTrainFile();
		Deck deck = deckComp.decodeDeckString(
		"AAEBAZ8FAtMWoM4CDkaMAegBzgPIBNcF1gbdCowO2BTdrgKLvQK4xwLYxwIA");
		annComp.classifyDeck(deck);
//		ss.tagsAffin();
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