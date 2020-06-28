package hstools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hstools.domain.components.ArtificialNeuralNetwork;
import hstools.domain.components.CardComponent;
import hstools.domain.components.DeckComponent;
import hstools.domain.entities.Deck;
import hstools.repositories.CardRepository;
import hstools.ui.MainScreen;

@SpringBootApplication // (scanBasePackages = { "hstools.components" })
//@ComponentScan("hstools.domain.components")
//@EnableNeo4jRepositories("hstools.repositories")
public class HearthstoneToolsApplication extends JFrame implements CommandLineRunner {
//	@Autowired
//	private CardComponent cardComp;
//
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
//
//	@Autowired
//	private CardRepository cRepo;

	public static void main(String[] args) {
		SpringApplication.run(HearthstoneToolsApplication.class, args);
	}
	
	public void MainScreen(){
		setLayout(new FlowLayout());
		JTextField t = new JTextField(100);
		add(t);
		JButton b = new JButton("CLASSIFY DECKSTRING");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Deck deck = deckComp.decodeDeckString(t.getText());
				annComp.classifyDeck(deck);
			}
		});
		add(b);
		setTitle("Hearthstone Tools");
		setSize(1200, 100);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void run(String... args) throws Exception {
		MainScreen();
//		deckComp.decodeDecksFromFile("commonHSTopDecks.txt");
//		annComp.generateTrainFile();

	//	Deck deck = deckComp.decodeDeckString(
				// https://www.hearthstonetopdecks.com/decks/deathrattle-hunter-deck-list-guide-standard/
				// "AAEBAR8CxQiG0wIOqAK1A+sH2wntCf4Mzq4CubQC6rsC7LsCicMCjsMCps4Cxs4CAA==");
				// https://www.hearthstonetopdecks.com/decks/witchwood-tempo-mage-deck-list-guide-standard/
				// "AAEBAf0EBHGi0wLu9gLvgAMNuwKVA6sEtATmBJYF7AXBwQKYxAKP0wL77AKV/wK5/wIA");
				// https://www.hearthstonetopdecks.com/decks/cube-warlock-deck-list-guide-standard/
				// "AAEBAf0GBsnCApfTAurmAtvpApz4Arf9AgyKAfcEtgfhB5vCAufLAvLQAvjQAojSAovhAvzlAujnAgA=");
				// https://www.hearthstonetopdecks.com/decks/dogs-1-legend-kingsbane-mill-rogue/
	//			"AAEBAaIHCLICqAipzQKxzgKA0wLQ4wLf4wK77wILigG0AcQB7QLLA80D+AeGCamvAuXRAtvjAgA=");
//		annComp.classifyDeck(deck);
	}
}