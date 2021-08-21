package hstools.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hstools.domain.components.ArtificialNeuralNetwork;
import hstools.domain.components.CardComponent;
import hstools.domain.components.DeckComponent;
import hstools.domain.components.SynergyBuilder;
import hstools.domain.entities.Card;
import hstools.domain.entities.Deck;

@Component
public class UIMain extends JPanel {
	public UIMain() {
		setBorder(new LineBorder(Color.RED));
	}

	@Autowired
	private CardComponent cardComp;

	@Autowired
	private DeckComponent deckComp;

	@Autowired
	private SynergyBuilder synComp;

	@Autowired
	private ArtificialNeuralNetwork annComp;

	public void init() {
		setLayout(new FlowLayout());

		JComboBox<String> cbCards = new JComboBox<String>();

		JButton b1 = new JButton("LOAD CARDS");
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardComp.buildCards();
				for (Card c : cardComp.getCards()) {
					cbCards.addItem(c.getName());
				}
				UITabs.updateModel(cardComp.getCards());
			}
		});
		add(b1);

		JButton b6 = new JButton("LOAD PRO DECKS");
		b6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deckComp.decodeDecksFromFile("127_pro.txt");
				// annComp.generateTrainFile();
				System.out.println("deckstring" + "\t" + "card_adv" + "\t" + "low_cost_minions" + "\t"
						+ "mid_cost_minions" + "\t" + "high_cost" + "\t" + "survs" + "\t" + "board_control" + "\t"
						+ "stats_cost" + "\t" + "archtype");
				for (Deck deck : deckComp.getDecks()) {
					// annComp.classifyDeck(deck);
					deckComp.calcStats(deck);
					System.out.println(deck.getDeckstring() + "\t" + deck.getStats());
				}
			}
		});
		add(b6);

		add(cbCards);

		JButton b2 = new JButton("IMPORT & BUILD CARD TAGS");
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardComp.importTags();
				cardComp.buildAllCardTags();
			}
		});
		add(b2);

		JButton b3 = new JButton("IMPORT CARD RANKS");
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardComp.buildCardRanks();
			}
		});
		add(b3);

		JButton b4 = new JButton("IMPORT TAGS SYNERGIES");
		b4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synComp.importLoadTagSinergies();
				synComp.printTagsSynergiesGraphViz();
			}
		});
		add(b4);

		JButton b5 = new JButton("CALCULATE CARD SYNERGIES");
		b5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String idsORname = (String) cbCards.getSelectedItem();
				Card c = cardComp.getCard("carnivorous cube");
				synComp.sinergias(c);
//				for (Card c : cardComp.getCards()) {
//					if (c.getText() != null && !"".equals(c.getText().toString())) {
//						System.out.println(synComp.sinergias(c).size() + "\t" + c.getName() + "\t" + c.getTags() + "\t"
//								+ c.getText());
//					}
//				}
			}
		});
		add(b5);

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
	}
}
