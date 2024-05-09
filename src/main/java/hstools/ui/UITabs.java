package hstools.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import hstools.domain.entities.Card;

public class UITabs {
	JTabbedPane tabbedPane;
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
	private JFrame frame;
	private JTextField tfDeckName;
	private JTextField tfDeckString;
	private JTextField txtNemPrecisaBoto;
	private JTextField textField_2;
	private JTable table;
	static DefaultTableModel tmodel = new DefaultTableModel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UITabs window = new UITabs();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UITabs() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 625, 489);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel pDeck = new JPanel();
		tabbedPane.addTab("New tab", null, pDeck, null);
		GridBagLayout gbl_pDeck = new GridBagLayout();
		gbl_pDeck.columnWidths = new int[] { 575, 0 };
		gbl_pDeck.rowHeights = new int[] { 75, 272, 0 };
		gbl_pDeck.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pDeck.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		pDeck.setLayout(gbl_pDeck);

		JPanel pDeckTop = new JPanel();
		pDeckTop.setBorder(new TitledBorder(null, "Deck Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_pDeckTop = new GridBagConstraints();
		gbc_pDeckTop.fill = GridBagConstraints.HORIZONTAL;
		gbc_pDeckTop.anchor = GridBagConstraints.NORTH;
		gbc_pDeckTop.insets = new Insets(0, 0, 5, 0);
		gbc_pDeckTop.gridx = 0;
		gbc_pDeckTop.gridy = 0;
		pDeck.add(pDeckTop, gbc_pDeckTop);
		GridBagLayout gbl_pDeckTop = new GridBagLayout();
		gbl_pDeckTop.columnWidths = new int[] { 46, 0 };
		gbl_pDeckTop.rowHeights = new int[] { 14, 0 };
		gbl_pDeckTop.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_pDeckTop.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		pDeckTop.setLayout(gbl_pDeckTop);

		JLabel lDeckName = new JLabel("Deck Name");
		GridBagConstraints gbc_lDeckName = new GridBagConstraints();
		gbc_lDeckName.insets = new Insets(0, 0, 5, 5);
		gbc_lDeckName.anchor = GridBagConstraints.WEST;
		gbc_lDeckName.gridx = 0;
		gbc_lDeckName.gridy = 0;
		pDeckTop.add(lDeckName, gbc_lDeckName);

		tfDeckName = new JTextField();
		GridBagConstraints gbc_tfDeckName = new GridBagConstraints();
		gbc_tfDeckName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfDeckName.gridwidth = 9;
		gbc_tfDeckName.insets = new Insets(0, 0, 5, 5);
		gbc_tfDeckName.gridx = 1;
		gbc_tfDeckName.gridy = 0;
		pDeckTop.add(tfDeckName, gbc_tfDeckName);
		tfDeckName.setColumns(10);

		JLabel lDckName = new JLabel("Deck String");
		GridBagConstraints gbc_lDckName = new GridBagConstraints();
		gbc_lDckName.insets = new Insets(0, 0, 5, 5);
		gbc_lDckName.anchor = GridBagConstraints.WEST;
		gbc_lDckName.gridx = 0;
		gbc_lDckName.gridy = 1;
		pDeckTop.add(lDckName, gbc_lDckName);

		tfDeckString = new JTextField();
		GridBagConstraints gbc_tfDeckString = new GridBagConstraints();
		gbc_tfDeckString.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfDeckString.gridwidth = 9;
		gbc_tfDeckString.insets = new Insets(0, 0, 5, 5);
		gbc_tfDeckString.gridx = 1;
		gbc_tfDeckString.gridy = 1;
		pDeckTop.add(tfDeckString, gbc_tfDeckString);
		tfDeckString.setColumns(10);

		JButton bLoadDeck = new JButton("Load deck");
		GridBagConstraints gbc_bLoadDeck = new GridBagConstraints();
		gbc_bLoadDeck.anchor = GridBagConstraints.WEST;
		gbc_bLoadDeck.insets = new Insets(0, 0, 5, 0);
		gbc_bLoadDeck.gridx = 10;
		gbc_bLoadDeck.gridy = 1;
		pDeckTop.add(bLoadDeck, gbc_bLoadDeck);

		JPanel pFilters = new JPanel();
		pFilters.setBorder(new TitledBorder(null, "Filters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pFilters.setName("");
		GridBagLayout gbl_pFilters = new GridBagLayout();
		gbl_pFilters.columnWidths = new int[] { 60, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 };
		gbl_pFilters.rowHeights = new int[] { 0, 0, 30, 0, 0, 0, 0, 30, 30 };
		gbl_pFilters.columnWeights = new double[] { 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_pFilters.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		pFilters.setLayout(gbl_pFilters);

		GridBagConstraints gbc_pFilters = new GridBagConstraints();
		gbc_pFilters.insets = new Insets(0, 0, 5, 0);
		gbc_pFilters.fill = GridBagConstraints.HORIZONTAL;
		gbc_pFilters.anchor = GridBagConstraints.NORTH;
		gbc_pFilters.gridx = 0;
		gbc_pFilters.gridy = 1;
		pDeck.add(pFilters, gbc_pFilters);

		JLabel lblNewLabel = new JLabel("Class");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		pFilters.add(lblNewLabel, gbc_lblNewLabel);

		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setToolTipText("sjdgfshgfj");
		GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
		gbc_comboBox_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_2.gridwidth = 3;
		gbc_comboBox_2.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_2.gridx = 1;
		gbc_comboBox_2.gridy = 0;
		pFilters.add(comboBox_2, gbc_comboBox_2);

		JLabel lblNewLabel_1 = new JLabel("Format");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		pFilters.add(lblNewLabel_1, gbc_lblNewLabel_1);

		JRadioButton rdbtnNewRadioButton = new JRadioButton("Wild");
		GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
		gbc_rdbtnNewRadioButton.anchor = GridBagConstraints.WEST;
		gbc_rdbtnNewRadioButton.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton.gridx = 1;
		gbc_rdbtnNewRadioButton.gridy = 1;
		pFilters.add(rdbtnNewRadioButton, gbc_rdbtnNewRadioButton);

		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Std");
		GridBagConstraints gbc_rdbtnNewRadioButton_1 = new GridBagConstraints();
		gbc_rdbtnNewRadioButton_1.anchor = GridBagConstraints.WEST;
		gbc_rdbtnNewRadioButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNewRadioButton_1.gridx = 2;
		gbc_rdbtnNewRadioButton_1.gridy = 1;
		pFilters.add(rdbtnNewRadioButton_1, gbc_rdbtnNewRadioButton_1);

		JLabel lblNewLabel_4 = new JLabel("Card Filter");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 2;
		pFilters.add(lblNewLabel_4, gbc_lblNewLabel_4);

		txtNemPrecisaBoto = new JTextField();
		txtNemPrecisaBoto.setText("nem precisa botão");
		txtNemPrecisaBoto.setToolTipText("nem precisa botão");
		GridBagConstraints gbc_txtNemPrecisaBoto = new GridBagConstraints();
		gbc_txtNemPrecisaBoto.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNemPrecisaBoto.gridwidth = 9;
		gbc_txtNemPrecisaBoto.insets = new Insets(0, 0, 5, 5);
		gbc_txtNemPrecisaBoto.gridx = 1;
		gbc_txtNemPrecisaBoto.gridy = 2;
		pFilters.add(txtNemPrecisaBoto, gbc_txtNemPrecisaBoto);
		txtNemPrecisaBoto.setColumns(10);

		JLabel lblNewLabel_7 = new JLabel("Rank filter");
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_7.gridx = 0;
		gbc_lblNewLabel_7.gridy = 3;
		pFilters.add(lblNewLabel_7, gbc_lblNewLabel_7);

		textField_2 = new JTextField();
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 3;
		pFilters.add(textField_2, gbc_textField_2);
		textField_2.setColumns(10);

		JLabel lblNewLabel_8 = new JLabel("Attack");
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_8.gridx = 0;
		gbc_lblNewLabel_8.gridy = 4;
		pFilters.add(lblNewLabel_8, gbc_lblNewLabel_8);

		JSpinner spinner = new JSpinner();
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.insets = new Insets(0, 0, 5, 5);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 4;
		pFilters.add(spinner, gbc_spinner);

		JLabel lblNewLabel_9 = new JLabel("Health");
		GridBagConstraints gbc_lblNewLabel_9 = new GridBagConstraints();
		gbc_lblNewLabel_9.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_9.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_9.gridx = 2;
		gbc_lblNewLabel_9.gridy = 4;
		pFilters.add(lblNewLabel_9, gbc_lblNewLabel_9);

		JSpinner spinner_1 = new JSpinner();
		GridBagConstraints gbc_spinner_1 = new GridBagConstraints();
		gbc_spinner_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_1.insets = new Insets(0, 0, 5, 5);
		gbc_spinner_1.gridx = 3;
		gbc_spinner_1.gridy = 4;
		pFilters.add(spinner_1, gbc_spinner_1);

		JLabel lblNewLabel_10 = new JLabel("Type");
		GridBagConstraints gbc_lblNewLabel_10 = new GridBagConstraints();
		gbc_lblNewLabel_10.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_10.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_10.gridx = 0;
		gbc_lblNewLabel_10.gridy = 5;
		pFilters.add(lblNewLabel_10, gbc_lblNewLabel_10);

		JComboBox comboBox_3 = new JComboBox();
		GridBagConstraints gbc_comboBox_3 = new GridBagConstraints();
		gbc_comboBox_3.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_3.gridx = 1;
		gbc_comboBox_3.gridy = 5;
		pFilters.add(comboBox_3, gbc_comboBox_3);

		JLabel lblNewLabel_2 = new JLabel("Faction");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 2;
		gbc_lblNewLabel_2.gridy = 5;
		pFilters.add(lblNewLabel_2, gbc_lblNewLabel_2);

		JComboBox comboBox_4 = new JComboBox();
		GridBagConstraints gbc_comboBox_4 = new GridBagConstraints();
		gbc_comboBox_4.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_4.gridx = 3;
		gbc_comboBox_4.gridy = 5;
		pFilters.add(comboBox_4, gbc_comboBox_4);

		JLabel lblNewLabel_6 = new JLabel("Rarity Filter");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 6;
		pFilters.add(lblNewLabel_6, gbc_lblNewLabel_6);

		JCheckBox chckbxNewCheckBox_10 = new JCheckBox("free");
		GridBagConstraints gbc_chckbxNewCheckBox_10 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_10.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_10.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_10.gridx = 1;
		gbc_chckbxNewCheckBox_10.gridy = 6;
		pFilters.add(chckbxNewCheckBox_10, gbc_chckbxNewCheckBox_10);

		JCheckBox chckbxNewCheckBox_11 = new JCheckBox("com");
		GridBagConstraints gbc_chckbxNewCheckBox_11 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_11.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_11.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_11.gridx = 2;
		gbc_chckbxNewCheckBox_11.gridy = 6;
		pFilters.add(chckbxNewCheckBox_11, gbc_chckbxNewCheckBox_11);

		JCheckBox chckbxNewCheckBox_12 = new JCheckBox("rare");
		GridBagConstraints gbc_chckbxNewCheckBox_12 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_12.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_12.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_12.gridx = 3;
		gbc_chckbxNewCheckBox_12.gridy = 6;
		pFilters.add(chckbxNewCheckBox_12, gbc_chckbxNewCheckBox_12);

		JCheckBox chckbxNewCheckBox_13 = new JCheckBox("epic");
		GridBagConstraints gbc_chckbxNewCheckBox_13 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_13.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_13.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_13.gridx = 4;
		gbc_chckbxNewCheckBox_13.gridy = 6;
		pFilters.add(chckbxNewCheckBox_13, gbc_chckbxNewCheckBox_13);

		JCheckBox chckbxNewCheckBox_14 = new JCheckBox("legendary");
		GridBagConstraints gbc_chckbxNewCheckBox_14 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_14.gridwidth = 2;
		gbc_chckbxNewCheckBox_14.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_14.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_14.gridx = 5;
		gbc_chckbxNewCheckBox_14.gridy = 6;
		pFilters.add(chckbxNewCheckBox_14, gbc_chckbxNewCheckBox_14);

		JLabel lblNewLabel_5 = new JLabel("Mana Filter");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 7;
		pFilters.add(lblNewLabel_5, gbc_lblNewLabel_5);

		JCheckBox chckbxNewCheckBox = new JCheckBox("0-1");
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox.gridx = 1;
		gbc_chckbxNewCheckBox.gridy = 7;
		pFilters.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);

		JCheckBox chckbxNewCheckBox_2 = new JCheckBox("2");
		GridBagConstraints gbc_chckbxNewCheckBox_2 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_2.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_2.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox_2.gridx = 2;
		gbc_chckbxNewCheckBox_2.gridy = 7;
		pFilters.add(chckbxNewCheckBox_2, gbc_chckbxNewCheckBox_2);

		JCheckBox chckbxNewCheckBox_3 = new JCheckBox("3");
		GridBagConstraints gbc_chckbxNewCheckBox_3 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_3.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_3.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox_3.gridx = 3;
		gbc_chckbxNewCheckBox_3.gridy = 7;
		pFilters.add(chckbxNewCheckBox_3, gbc_chckbxNewCheckBox_3);

		JCheckBox chckbxNewCheckBox_4 = new JCheckBox("4");
		GridBagConstraints gbc_chckbxNewCheckBox_4 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_4.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_4.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox_4.gridx = 4;
		gbc_chckbxNewCheckBox_4.gridy = 7;
		pFilters.add(chckbxNewCheckBox_4, gbc_chckbxNewCheckBox_4);

		JCheckBox chckbxNewCheckBox_5 = new JCheckBox("5");
		GridBagConstraints gbc_chckbxNewCheckBox_5 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_5.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_5.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox_5.gridx = 5;
		gbc_chckbxNewCheckBox_5.gridy = 7;
		pFilters.add(chckbxNewCheckBox_5, gbc_chckbxNewCheckBox_5);

		JCheckBox chckbxNewCheckBox_6 = new JCheckBox("6");
		GridBagConstraints gbc_chckbxNewCheckBox_6 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_6.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_6.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox_6.gridx = 6;
		gbc_chckbxNewCheckBox_6.gridy = 7;
		pFilters.add(chckbxNewCheckBox_6, gbc_chckbxNewCheckBox_6);

		JCheckBox chckbxNewCheckBox_7 = new JCheckBox("7");
		GridBagConstraints gbc_chckbxNewCheckBox_7 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_7.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_7.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox_7.gridx = 7;
		gbc_chckbxNewCheckBox_7.gridy = 7;
		pFilters.add(chckbxNewCheckBox_7, gbc_chckbxNewCheckBox_7);

		JCheckBox chckbxNewCheckBox_8 = new JCheckBox("8");
		GridBagConstraints gbc_chckbxNewCheckBox_8 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_8.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_8.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox_8.gridx = 8;
		gbc_chckbxNewCheckBox_8.gridy = 7;
		pFilters.add(chckbxNewCheckBox_8, gbc_chckbxNewCheckBox_8);

		JCheckBox chckbxNewCheckBox_9 = new JCheckBox("9");
		GridBagConstraints gbc_chckbxNewCheckBox_9 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_9.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_9.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxNewCheckBox_9.gridx = 9;
		gbc_chckbxNewCheckBox_9.gridy = 7;
		pFilters.add(chckbxNewCheckBox_9, gbc_chckbxNewCheckBox_9);

		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("10+");
		GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_1.gridx = 10;
		gbc_chckbxNewCheckBox_1.gridy = 7;
		pFilters.add(chckbxNewCheckBox_1, gbc_chckbxNewCheckBox_1);

		JPanel pCards = new JPanel();
		pCards.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_pCards = new GridBagConstraints();
		gbc_pCards.fill = GridBagConstraints.BOTH;
		gbc_pCards.gridx = 0;
		gbc_pCards.gridy = 2;
		pDeck.add(pCards, gbc_pCards);
		pCards.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		pCards.add(scrollPane);

		table = new JTable();
		table.setModel(tmodel);
		//table.getColumnModel().getColumn(0).setMinWidth(30);
		scrollPane.setViewportView(table);
		tabbedPane.addTab("DECK BUILDER", null, pDeck, null);
	}

	public static void updateModel(List<Card> cards) {
		Object[][] dataVector = new Object[cards.size()][2];
		for (int i = 0; i < dataVector.length; i++) {
			Card card = cards.get(i);
			dataVector[i][0] = card.getNome();
			dataVector[i][1] = card.getTexto();
		}
		String[] columnIdentifiers = { "Name", "Text" };
		tmodel.setDataVector(dataVector, columnIdentifiers);
	}
}