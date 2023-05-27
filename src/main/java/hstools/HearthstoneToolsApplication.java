package hstools;

import java.io.PrintWriter;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hstools.domain.components.ArtificialNeuralNetwork;
import hstools.domain.components.CardComponent;
import hstools.domain.components.DeckComponent;
import hstools.domain.components.FilesComponent;
import hstools.domain.components.NetworkComponent;
import hstools.domain.components.SynergyBuilder;
import hstools.domain.entities.RapidApiInfo;
import hstools.ui.UIMain;

@SpringBootApplication // (scanBasePackages = { "hstools.components" })
//@ComponentScan("hstools.domain.components")
//@EnableNeo4jRepositories("hstools.repositories")
public class HearthstoneToolsApplication extends JFrame implements CommandLineRunner {
	@Autowired
	private CardComponent cardComp;

	@Autowired
	private DeckComponent deckComp;

	@Autowired
	private SynergyBuilder synComp;

//	@Autowired
//	private DataScienceComponent scienceComp;

	@Autowired
	private ArtificialNeuralNetwork annComp;
//
//	@Autowired
//	private CardRepository cRepo;

	public static RapidApiInfo rapidApiInfo;

	@Autowired
	private NetworkComponent net;
	
	@Autowired
	private FilesComponent files;

	// public static CardSets cs;

	public static void main(String[] args) {
		SpringApplication.run(HearthstoneToolsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		updateData();
		init();
//		deckComp.decodeDecksFromFile("commonHSTopDecks.txt");
//		annComp.generateTrainFile();

		// Deck deck = deckComp.decodeDeckString(
		// https://www.hearthstonetopdecks.com/decks/deathrattle-hunter-deck-list-guide-standard/
		// "AAEBAR8CxQiG0wIOqAK1A+sH2wntCf4Mzq4CubQC6rsC7LsCicMCjsMCps4Cxs4CAA==");
		// https://www.hearthstonetopdecks.com/decks/witchwood-tempo-mage-deck-list-guide-standard/
		// "AAEBAf0EBHGi0wLu9gLvgAMNuwKVA6sEtATmBJYF7AXBwQKYxAKP0wL77AKV/wK5/wIA");
		// https://www.hearthstonetopdecks.com/decks/cube-warlock-deck-list-guide-standard/
		// "AAEBAf0GBsnCApfTAurmAtvpApz4Arf9AgyKAfcEtgfhB5vCAufLAvLQAvjQAojSAovhAvzlAujnAgA=");
		// https://www.hearthstonetopdecks.com/decks/dogs-1-legend-kingsbane-mill-rogue/
		// "AAEBAaIHCLICqAipzQKxzgKA0wLQ4wLf4wK77wILigG0AcQB7QLLA80D+AeGCamvAuXRAtvjAgA=");
//		annComp.classifyDeck(deck);
	}

	private void updateData() throws JsonProcessingException {
		rapidApiInfo = files.loadRapidApiInfoFile();
		RapidApiInfo rapidApiInfoNew = net.rapidApiInfo();
		if (!rapidApiInfoNew.getPatch().equals(rapidApiInfo.getPatch())) {
			rapidApiInfo = rapidApiInfoNew;
			files.updateRapidApiInfoFile(rapidApiInfo);
			String jsonCards = net.rapidApiAllCollectibleCards();
			// TODO flat antes de salvar em file?
			files.updateCardsFile(jsonCards);
		}
	}

	@Autowired
	UIMain ui;

	private void init() {
		setTitle("Hearthstone Tools");
		setSize(1300, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// UIMain ui = new UIMain();
		ui.init();
//		ui.add(new UITabs().getTabbedPane());
		this.setContentPane(ui);
		setVisible(true);
	}
}