package hstools.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
	private JList<DataDTO> dataDTOJList;
	private List<DataDTO> cardList=new ArrayList<>(); // Lista de objetos DTO

	public UIMain() {
		cardList.add(new DataDTO("name", "text"));
//		setTitle("Dual Table Example");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setSize(800, 600);
//		setLayout(new FlowLayout());
		setMaximumSize(new Dimension(800, 600));
		setPreferredSize(new Dimension(800, 600));

		columnFilters = new ArrayList<>();
		columnFilters.add(new ColumnFilter("Column 1"));
		columnFilters.add(new ColumnFilter("Column 2"));

		topTableModel = new DefaultTableModel();
		topTableModel.setColumnIdentifiers(new Object[] { "Column 1", "Column 2" });
		// Adicione esta linha após a inicialização do topTableModel
		topTableModel.addRow(new Object[] { "Exemplo Valor 1", "Exemplo Valor 2" });
		topTableModel.addRow(new Object[] { "Exemplo Valor 2", "Exemplo Valor 2" });
		topTableModel.addRow(new Object[] { "Exemplo Valor 1", "Exemplo Valor 1" });

		topTable = new JTable(topTableModel);
		TableRowSorter<DefaultTableModel> topSorter = new TableRowSorter<>(topTableModel);
		topTable.setRowSorter(topSorter);
		setupTableEditable(topTable, false); // Define a coluna "Column 2" como não editável

		topTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // Detecta um duplo clique
					int selectedRow = topTable.getSelectedRow();
					if (selectedRow != -1) {
						Object[] rowData = new Object[] { topTableModel.getValueAt(selectedRow, 0),
								topTableModel.getValueAt(selectedRow, 1) };
						bottomTableModel.addRow(rowData); // Copia a linha para a tabela de baixo
					}
				}
			}
		});

		bottomTableModel = new DefaultTableModel();
		bottomTableModel.setColumnIdentifiers(new Object[] { "Column 1", "Column 2" });
		bottomTable = new JTable(bottomTableModel);
		TableRowSorter<DefaultTableModel> bottomSorter = new TableRowSorter<>(bottomTableModel);
		bottomTable.setRowSorter(bottomSorter);
		bottomTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // Detecta um duplo clique
					int selectedRow = bottomTable.getSelectedRow();
					if (selectedRow != -1) {
						bottomTableModel.removeRow(selectedRow); // Remove a linha da tabela de baixo
					}
				}
			}
		});

		JScrollPane topScrollPane = new JScrollPane(topTable);
		JScrollPane bottomScrollPane = new JScrollPane(bottomTable);

		// Adicione seus próprios itens aqui
		dataDTOJList = new JList<>(cardList.toArray(new DataDTO[0])); // Converte a lista em um array de DataDTO
		dataDTOJList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // Detecta um duplo clique
					int selectedIndex = dataDTOJList.getSelectedIndex();
					if (selectedIndex != -1) {
						Object valueToCopy = topTableModel.getValueAt(selectedIndex, 0); // Você pode ajustar a coluna
																							// de onde deseja copiar o
																							// valor
						bottomTableModel.addRow(new Object[] { valueToCopy, "" }); // Adicione o valor à tabela de baixo
					}
				}
			}
		});

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topScrollPane, bottomScrollPane);
		splitPane.setResizeWeight(0.5);

		JPanel filterPanel = topButtonsPanel();//new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (ColumnFilter columnFilter : columnFilters) {
			JPanel columnFilterPanel = new JPanel(new BorderLayout());
			columnFilterPanel.add(new JLabel(columnFilter.getLabel()), BorderLayout.NORTH);
			columnFilterPanel.add(columnFilter.getFilterTextField(), BorderLayout.CENTER);
			filterPanel.add(columnFilterPanel);
		}

		JPanel eastPanel = new JPanel(new BorderLayout());
		eastPanel.add(new JLabel("String List"), BorderLayout.NORTH);
		eastPanel.add(new JScrollPane(dataDTOJList), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		copyButton = new JButton("Copy Selected Row");
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = topTable.getSelectedRow();
				if (selectedRow != -1) {
					Object[] rowData = new Object[] { topTableModel.getValueAt(selectedRow, 0),
							topTableModel.getValueAt(selectedRow, 1) };
					bottomTableModel.addRow(rowData);
				}
			}
		});
		deleteButton = new JButton("Delete Selected Row");
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
	
	JComboBox<String> cbCards = new JComboBox<>();
	public JPanel topButtonsPanel() {
		JPanel panel = new JPanel();
		//panel.setLayout(new FlowLayout());

		cbCards.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> combo = (JComboBox<String>) e.getSource();
                String selectedValue = (String) combo.getSelectedItem();
                System.out.println(cardComp.getCard(selectedValue));
            }
        });
		
		panel.add(cbCards);

		JButton b6 = new JButton("LOAD PRO DECKS");
		b6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deckComp.decodeDecksFromFile("400_Pro_Decks.txt");
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
		panel.add(b6);

		JButton b2 = new JButton("IMPORT & BUILD CARD TAGS");
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO melhor buscar da planilha online 
				//cardComp.importTags();
				cardComp.loadTags();
				cardComp.buildAllCardTags();
			}
		});
		panel.add(b2);

		JButton b3 = new JButton("IMPORT CARD RANKS");
		b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//cardComp.scrapCardRanks();
				cardComp.importCardRanks();
			}
		});
		panel.add(b3);

		JButton b4 = new JButton("IMPORT TAGS SYNERGIES");
		b4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//synComp.loadTags();
				synComp.loadTagSinergies();
				synComp.printTagsSynergiesGraphViz();
				
				//TODO calcula as sinergias
//				String idsORname = (String) cbCards.getSelectedItem();
//				Card c = cardComp.getCard("carnivorous cube");
//				synComp.sinergias(c);
//				for (Card c : cardComp.getCards()) {
//					if (c.getText() != null && !"".equals(c.getText().toString())) {
//						System.out.println(synComp.sinergias(c).size() + "\t" + c.getName() + "\t" + c.getTags() + "\t"
//								+ c.getText());
//					}
//				}
			}
		});
		panel.add(b4);

		JTextField t = new JTextField(1);
		panel.add(t);
		JButton b = new JButton("CLASSIFY DECKSTRING");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Deck deck = deckComp.decodeDeckString(t.getText());
//				annComp.classifyDeck(deck);
				System.out.println(CountDeckWords.keyWords);
				//List<Integer> results = new ArrayList<>();
				for (Deck ds : deckComp.getDecks()) {
					Map<String, Integer> map = new LinkedHashMap<String, Integer>();
					CountDeckWords.keyWords.forEach(k -> map.put(k, 0));
					CountDeckWords.wordsMap(ds, map);
					//results.forEach(m -> results.addAll(map.values()));
					System.out.println(map.values());
				}
				
				//results.forEach(l->System.out.println(l));
			}
		});
		panel.add(b);
		
		return panel;
	}
	
	public void init(){
		cardComp.buildCards();
		for (Card c : cardComp.getCards()) {
			cbCards.addItem(c.getName());
		}
		UITabs.updateModel(cardComp.getCards());
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new UIMain();
			}
		});
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