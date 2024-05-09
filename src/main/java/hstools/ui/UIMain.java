package hstools.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hstools.domain.components.ArtificialNeuralNetwork;
import hstools.domain.components.CardComponent;
import hstools.domain.components.CountDeckWords;
import hstools.domain.components.DeckComponent;
import hstools.domain.components.SynergyBuilder;
import hstools.domain.entities.Card;
import hstools.domain.entities.Deck;

@Component
public class UIMain extends JPanel {
	@Autowired
	private CardComponent cardComp;

	@Autowired
	private DeckComponent deckComp;

	@Autowired
	private SynergyBuilder synComp;

	@Autowired
	private ArtificialNeuralNetwork annComp;

	private DefaultTableModel topTableModel, bottomTableModel;
	private JTable topTable, bottomTable;
	private JButton copyButton, deleteButton;
	private List<ColumnFilter> columnFilters;
	private JList<String> dataDTOJList;
	private List<Card> cardList;
	private Deck deck = new Deck("temp", "Rogue");

	public UIMain() {

		// cardList.add(new Card("name", "text"));
//		setTitle("Dual Table Example");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setSize(800, 600);
//		setLayout(new FlowLayout());
		setMaximumSize(new Dimension(800, 600));
		setPreferredSize(new Dimension(800, 600));

		columnFilters = new ArrayList<>();
		columnFilters.add(new ColumnFilter("Name"));
		columnFilters.add(new ColumnFilter("Text"));

		topTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Torna todas as células não editáveis
			}
		};

		// Adicione esta linha após a inicialização do topTableModel
		// Preencha o topTableModel com os dados da dataList

//		topTableModel.addRow(new Object[] { "Exemplo Valor 1", "Exemplo Valor 2" });
//		topTableModel.addRow(new Object[] { "Exemplo Valor 2", "Exemplo Valor 2" });
//		topTableModel.addRow(new Object[] { "Exemplo Valor 1", "Exemplo Valor 1" });

		topTableModel.setColumnIdentifiers(new Object[] { "Has", "Name", "Rank", "Text" });
		topTable = new JTable(topTableModel);

		TableRowSorter<DefaultTableModel> topSorter = new TableRowSorter<>(topTableModel);
		topTable.setRowSorter(topSorter);

		topTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // Detecta um duplo clique
					int selectedRow = topTable.getSelectedRow();
					if (selectedRow != -1) {
						int modelRowIndex = topTable.convertRowIndexToModel(selectedRow);
						Object cardName = topTableModel.getValueAt(modelRowIndex, 1);
						Object[] rowData = new Object[] { 1, cardName, topTableModel.getValueAt(modelRowIndex, 2),
								topTableModel.getValueAt(modelRowIndex, 3) };
						int foundRow = findStringInColumn(bottomTable, (String) cardName, 1);
						if (foundRow == -1) {
							bottomTableModel.addRow(rowData);
						} else {
							bottomTableModel.setValueAt(((int) bottomTableModel.getValueAt(foundRow, 0) + 1), foundRow,
									0);
						}
						if (deck.getCards().get(cardName) == null) {
							deck.getCards().put(cardComp.getCard((String) cardName), 1);
						} else {
							deck.getCards().put(cardComp.getCard((String) cardName), deck.getCards().get(cardName) + 1);
						}
						dataDTOJList.setListData(new Vector<>(deckComp.calcSuggestions(deck).stream().map(
								// Node::getName
								c -> c.getNome() + "=" + c.getStats().getTempDeckFreq())
								.collect(Collectors.toList())));
					}
				}
			}
		});

		bottomTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Torna todas as células não editáveis
			}
		};
		bottomTableModel.setColumnIdentifiers(new Object[] { "Qnt", "Name", "Rank", "Text" });
		bottomTable = new JTable(bottomTableModel);
		TableRowSorter<DefaultTableModel> bottomSorter = new TableRowSorter<>(bottomTableModel);
		bottomTable.setRowSorter(bottomSorter);
		bottomTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // Detecta um duplo clique
					int selectedRow = bottomTable.getSelectedRow();
					if (selectedRow != -1) {
						// int modelRowIndex = topTable.convertRowIndexToModel(selectedRow);
						Card card = cardComp.getCard((String) bottomTableModel.getValueAt(selectedRow, 1));
						int val = (int) bottomTableModel.getValueAt(selectedRow, 0);
						// int foundRow = findStringInColumn(bottomTable, (String) cardName, 1);
						if (val > 1) {
							bottomTableModel.setValueAt(--val, selectedRow, 0);
						} else {
							bottomTableModel.removeRow(selectedRow); // Remove a linha da tabela de baixo
						}
						if (deck.getCards().get(card) == 1) {
							deck.getCards().remove(card);
						} else {
							deck.getCards().put(card, deck.getCards().get(card) - 1);
						}
						dataDTOJList.setListData(new Vector<>(deckComp.calcSuggestions(deck).stream().map(
								// Node::getName
								c -> c.getNome() + "=" + c.getStats().getTempDeckFreq())
								.collect(Collectors.toList())));
					}
				}
			}
		});

		JScrollPane topScrollPane = new JScrollPane(topTable);
		JScrollPane bottomScrollPane = new JScrollPane(bottomTable);

		// Adicione seus próprios itens aqui
		// Converte a lista em um array de DataDTO
		dataDTOJList = new JList<>();
		dataDTOJList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // Detecta um duplo clique
					int selectedIndex = dataDTOJList.getSelectedIndex();
					if (selectedIndex != -1) {
						Card c = cardComp.getCard(dataDTOJList.getSelectedValue().split("=")[0]);
						Object name = c.getNome();
						Object rank = c.getStats().getRank();
						Object text = c.getTexto();

						int foundRow = findStringInColumn(bottomTable, (String) name, 1);
						if (foundRow == -1) {
							bottomTableModel.addRow(new Object[] { 1, name, rank, text });
						} else {
							bottomTableModel.setValueAt(((int) bottomTableModel.getValueAt(foundRow, 0) + 1), foundRow,
									0);
						}
						if (deck.getCards().get(c) == null) {
							deck.getCards().put(c, 1);
						} else {
							deck.getCards().put(c, deck.getCards().get(c) + 1);
						}
						
						dataDTOJList.setListData(new Vector<>(deckComp.calcSuggestions(deck).stream().map(
								// Node::getName
								c1 -> c1.getNome() + "=" + c1.getStats().getTempDeckFreq())
								.collect(Collectors.toList())));
					}
				}
			}
		});

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topScrollPane, bottomScrollPane);
		splitPane.setResizeWeight(0.5);

		JPanel filterPanel = topButtonsPanel();// new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (ColumnFilter columnFilter : columnFilters) {
			JPanel columnFilterPanel = new JPanel(new BorderLayout());
			columnFilterPanel.add(new JLabel(columnFilter.getLabel()), BorderLayout.NORTH);
			columnFilterPanel.add(columnFilter.getFilterTextField(), BorderLayout.CENTER);
			filterPanel.add(columnFilterPanel);
		}

		JPanel eastPanel = new JPanel(new BorderLayout());
		eastPanel.add(new JLabel("Suggestions"), BorderLayout.NORTH);
		eastPanel.add(new JScrollPane(dataDTOJList), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		copyButton = new JButton("Add to deck");
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = topTable.getSelectedRow();
				if (selectedRow != -1) {
					Object[] rowData = new Object[] { topTableModel.getValueAt(selectedRow, 0),
							topTableModel.getValueAt(selectedRow, 1), topTableModel.getValueAt(selectedRow, 2),
							topTableModel.getValueAt(selectedRow, 3) };
					bottomTableModel.addRow(rowData);
				}
			}
		});
		deleteButton = new JButton("Delete from deck");
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = bottomTable.getSelectedRow();
				if (selectedRow != -1) {
					bottomTableModel.removeRow(selectedRow);
				}
			}
		});
		buttonPanel.add(copyButton);
		buttonPanel.add(deleteButton);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(filterPanel, BorderLayout.NORTH);
		mainPanel.add(splitPane, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		mainPanel.add(eastPanel, BorderLayout.EAST);

		add(mainPanel);

		setVisible(true);
	}

	private int findStringInColumn(JTable table, String searchString, int columnIndex) {
		for (int row = 0; row < table.getModel().getRowCount(); row++) {
			String cellValue = table.getModel().getValueAt(row, columnIndex).toString();
			if (cellValue.contains(searchString))
				// table.setRowHeight(row, containsString ? table.getRowHeight() : 0);
				return row;
		}
		return -1;
	}

//	JComboBox<String> cbCards = new JComboBox<>();

	public JPanel topButtonsPanel() {
		JPanel panel = new JPanel();
		// panel.setLayout(new FlowLayout());

//		cbCards.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				JComboBox<String> combo = (JComboBox<String>) e.getSource();
//				String selectedValue = (String) combo.getSelectedItem();
//				System.out.println(cardComp.getCard(selectedValue));
//			}
//		});
//
//		panel.add(cbCards);

		JButton b6 = new JButton("LOAD PRO DECKS");
		b6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deckComp.decodeDecksFromFile("combo-decks.txt");
				//deckComp.decodeDecksFromFile("400_Pro_Decks.txt");
				// annComp.generateTrainFile();
				System.out.println(deckComp.getDecks().size() + " decks loaded.");
//				System.out.println("deckstring" + "\t" + "card_adv" + "\t" + "low_cost_minions" + "\t"
//						+ "mid_cost_minions" + "\t" + "high_cost" + "\t" + "survs" + "\t" + "board_control" + "\t"
//						+ "stats_cost" + "\t" + "archtype");
				for (Deck deck : deckComp.getDecks()) {
					// annComp.classifyDeck(deck);
					deckComp.calcStats(deck);
					// System.out.println(deck.getDeckstring() + "\t" + deck.getStats());
				}
			}
		});
		panel.add(b6, BorderLayout.NORTH);

		JButton b3 = new JButton("IMPORT CARD RANKS");
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// cardComp.scrapCardRanks();
				cardComp.importCardRanks();
				refreshCards();
			}
		});
		panel.add(b3, BorderLayout.NORTH);

		JTextField t = new JTextField(20);
		panel.add(t, BorderLayout.NORTH);
		JButton b = new JButton("CLASSIFY DECKSTRING");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Deck deck = deckComp.decodeDeckString(t.getTexto());
//				annComp.classifyDeck(deck);
				System.out.println(CountDeckWords.keyWords);
				// List<Integer> results = new ArrayList<>();
				for (Deck ds : deckComp.getDecks()) {
					Map<String, Integer> map = new LinkedHashMap<String, Integer>();
					CountDeckWords.keyWords.forEach(k -> map.put(k, 0));
					CountDeckWords.wordsMap(ds, map);
					// results.forEach(m -> results.addAll(map.values()));
					System.out.println(map.values());
				}

				// results.forEach(l->System.out.println(l));
			}
		});
		panel.add(b, BorderLayout.NORTH);

		return panel;
	}

	public void init() {
		cardComp.buildCards();
		setupTableEditable(topTable, false);
		setupTableEditable(bottomTable, false);
		UITabs.updateModel(cardComp.getCards());
		refreshCards();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				topTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				topTable.getColumnModel().getColumn(0).setMinWidth(40);
				topTable.getColumnModel().getColumn(0).setMaxWidth(40);
				topTable.getColumnModel().getColumn(0).setPreferredWidth(40);

				topTable.getColumnModel().getColumn(1).setMinWidth(120);
				topTable.getColumnModel().getColumn(1).setMaxWidth(120);
				topTable.getColumnModel().getColumn(1).setPreferredWidth(120);

				topTable.getColumnModel().getColumn(2).setMinWidth(40);
				topTable.getColumnModel().getColumn(2).setMaxWidth(40);
				topTable.getColumnModel().getColumn(2).setPreferredWidth(40);

				topTable.getColumnModel().getColumn(3).setMinWidth(800);
				topTable.getColumnModel().getColumn(3).setMaxWidth(800);
				topTable.getColumnModel().getColumn(3).setPreferredWidth(800);

				bottomTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				bottomTable.getColumnModel().getColumn(0).setMinWidth(40);
				bottomTable.getColumnModel().getColumn(0).setMaxWidth(40);
				bottomTable.getColumnModel().getColumn(0).setPreferredWidth(40);

				bottomTable.getColumnModel().getColumn(1).setMinWidth(120);
				bottomTable.getColumnModel().getColumn(1).setMaxWidth(120);
				bottomTable.getColumnModel().getColumn(1).setPreferredWidth(120);

				bottomTable.getColumnModel().getColumn(2).setMinWidth(40);
				bottomTable.getColumnModel().getColumn(2).setMaxWidth(40);
				bottomTable.getColumnModel().getColumn(2).setPreferredWidth(40);

				bottomTable.getColumnModel().getColumn(3).setMinWidth(800);
				bottomTable.getColumnModel().getColumn(3).setMaxWidth(800);
				bottomTable.getColumnModel().getColumn(3).setPreferredWidth(800);
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				// cardComp.importTags();
				cardComp.loadTags();
				cardComp.buildAllCardTags();
				// synComp.loadTags();
				synComp.loadTagSinergies();
				// synComp.printTagsSynergiesGraphViz();

				// TODO calcula as sinergias
//				String idsORname = (String) cbCards.getSelectedItem();
//				Card c = cardComp.getCard("carnivorous cube");
//				synComp.sinergias(c);
//				for (Card c : cardComp.getCards()) {
//					if (c.getTexto() != null && !"".equals(c.getTexto().toString())) {
//						System.out.println(synComp.sinergias(c).size() + "\t" + c.getNome() + "\t" + c.getTags() + "\t"
//								+ c.getTexto());
//					}
//				}
			}
		}).start();
	}

	private void refreshCards() {
		cardList = cardComp.getCards().stream().filter(c -> c.getClasses().contains(deck.getClasse()))
				.collect(Collectors.toList());// .subList(0, 10);
		topTableModel.setDataVector(new Vector<>(), new Vector<>(List.of("Has", "Name", "Rank", "Text")));
		for (Card dto : cardList) {
			topTableModel
					.addRow(new Object[] { "", dto.getNome(), dto.getStats().getRank(), dto.getTexto().toString() });
		}
	}

	private void setupTableEditable(JTable table, boolean isEditable) {
		TableColumn column = table.getColumnModel().getColumn(1); // Coluna "Column 2"
		if (!isEditable) {
			column.setCellEditor(new NonEditableCellEditor());
		}
	}

	private class NonEditableCellEditor extends DefaultCellEditor {
		public NonEditableCellEditor() {
			super(new JTextField());
		}

		@Override
		public boolean isCellEditable(EventObject e) {
			return false;
		}
	}

	private class ColumnFilter {
		private String label;
		private JTextField filterTextField;

		public ColumnFilter(String label) {
			this.label = label;
			filterTextField = new JTextField();
			filterTextField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					filterTableRows();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					filterTableRows();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					filterTableRows();
				}
			});
		}

		public String getLabel() {
			return label;
		}

		public JTextField getFilterTextField() {
			return filterTextField;
		}
	}

	private void filterTableRows() {
		RowFilter<DefaultTableModel, Object> rowFilter = null;
		try {
			List<RowFilter<Object, Object>> filters = new ArrayList<>();
			for (ColumnFilter columnFilter : columnFilters) {
				String text = columnFilter.getFilterTextField().getText();	
				int columnIndex = topTableModel.findColumn(columnFilter.getLabel());
				filters.add(RowFilter.regexFilter(text, columnIndex));
			}
			rowFilter = RowFilter.andFilter(filters);
		} catch (java.util.regex.PatternSyntaxException ex) {
			// Handle padrão de expressão regular inválido
		}

		TableRowSorter<DefaultTableModel> topSorter = (TableRowSorter<DefaultTableModel>) topTable.getRowSorter();
		topSorter.setRowFilter(rowFilter);
	}
}