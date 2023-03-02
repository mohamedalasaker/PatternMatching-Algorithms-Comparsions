package projectv1_2;

import java.awt.EventQueue;

import javax.print.attribute.standard.SheetCollate;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import javax.swing.border.BevelBorder;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

public class GUI_App {

	// max/min size of documents to generate
	// change those to desirable size
	// maxSize MUST BE >= minSize
	// minSize is not input size to start from
	final static int maxSize = 10000;
	final static int minSize = 100;

	// GUI Attributes
	private JFrame frame;
	private JTextField sizeTextField;
	private JTable tableOutput;
	private JTextField textFieldShowDocument;
	private JTextArea textAreaPattern;

	static List<String> patternDocuments;// List of documents that contain the pattern

	// List of lists of locations of each start index of the pattern in the
	// corresponding document
	static List<List<Integer>> locations;

	// arrays to store average times of each algorithm
	double[] avgTimeBruteForce;
	double[] avgTimeBoyerMoore;
	double[] avgTimeKMP;

	// this counter is used to store the time that each algorithm took
	// over 5 runs on the same text
	static long counterTimeBrutrForce = 0;
	static long counterTimeBoyerMoore = 0;
	static long counterTimeKMP = 0;

	// array to store generated texts (documents).
	String[] generatedDocuments;

	// used to make sure not to regenerate after pressing the Run button
	boolean isGenereted = false;

	// GUI-related code

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_App window = new GUI_App(); // create a new window
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
	public GUI_App() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 *
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 10, 1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel main = new JPanel();
		frame.setBounds(100, 10, 1000, 600);
		frame.setContentPane(main);

		main.setLayout(null);

		// Button group
		ButtonGroup bg = new ButtonGroup();

		JRadioButton BF_RadioButton = new JRadioButton("Brute-Force");
		BF_RadioButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
		BF_RadioButton.setVerticalAlignment(SwingConstants.BOTTOM);
		BF_RadioButton.setBounds(655, 116, 227, 35);
		bg.add(BF_RadioButton);

		JRadioButton BMRadioButton = new JRadioButton("Boyer-Moore");
		BMRadioButton.setVerticalAlignment(SwingConstants.BOTTOM);
		BMRadioButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
		BMRadioButton.setBounds(655, 154, 227, 35);
		bg.add(BMRadioButton);

		JRadioButton KMP_RadioButton = new JRadioButton("Knuth-Morris-Pratt");
		KMP_RadioButton.setVerticalAlignment(SwingConstants.BOTTOM);
		KMP_RadioButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
		KMP_RadioButton.setBounds(655, 192, 227, 35);
		bg.add(KMP_RadioButton);

		main.add(KMP_RadioButton);
		main.add(BMRadioButton);
		main.add(BF_RadioButton);
		//////////////////////

		JLabel ChooseLabel = new JLabel("Choose the algorithm:");
		ChooseLabel.setFont(new Font("Tahoma", Font.PLAIN, 23));
		ChooseLabel.setBounds(629, 71, 310, 54);
		main.add(ChooseLabel);

		JPanel HeaderPanel = new JPanel();
		HeaderPanel.setBackground(new Color(0, 128, 255));
		HeaderPanel.setBounds(0, 0, 986, 65);
		main.add(HeaderPanel);

		JLabel lblNewLabel = new JLabel("Pattern-Matching application");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		HeaderPanel.add(lblNewLabel);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 35));

		JLabel enterPatternLabel = new JLabel("Enter Pattern to search for:");
		enterPatternLabel.setFont(new Font("Tahoma", Font.PLAIN, 23));
		enterPatternLabel.setBounds(629, 260, 310, 54);
		main.add(enterPatternLabel);

		textAreaPattern = new JTextArea();
		textAreaPattern.setBounds(629, 314, 347, 70);
		main.add(textAreaPattern);

		Border border = BorderFactory.createLineBorder(Color.BLACK);
		textAreaPattern
				.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		sizeTextField = new JTextField();
		sizeTextField.setBounds(629, 435, 187, 27);
		sizeTextField.setColumns(10);
		main.add(sizeTextField);

		JLabel lblSizeOfDocuments = new JLabel("size of documents starting from:");
		lblSizeOfDocuments.setFont(new Font("Tahoma", Font.PLAIN, 23));
		lblSizeOfDocuments.setBounds(629, 393, 335, 40);
		main.add(lblSizeOfDocuments);

		JButton runButton = new JButton("Run");
		runButton.setFont(new Font("Tahoma", Font.PLAIN, 19));
		runButton.setBounds(629, 473, 159, 35);
		main.add(runButton);

		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (textAreaPattern.getText().equals("") || sizeTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "please enter all data", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				String pat = textAreaPattern.getText();
				int size = Integer.parseInt(sizeTextField.getText());

				if (size > maxSize || size < minSize) {
					JOptionPane.showMessageDialog(null,
							"Size incorrect, please enter between " + minSize + " & " + maxSize + " (inclusive)",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}

				// to prevent regeneration of new documents
				if (!isGenereted) {
					generateDocuments(size);
					isGenereted = true;
				}

				// depending on choice, this block runs the algorithm associated with it
				if (BF_RadioButton.isSelected()) {
					runAlgorithm(size, pat, 0);
				} else if (BMRadioButton.isSelected()) {
					runAlgorithm(size, pat, 1);
				} else if (KMP_RadioButton.isSelected()) {
					runAlgorithm(size, pat, 2);
				} else {
					JOptionPane.showMessageDialog(null, "please choose algorithm", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		JPanel StatisticPanel = new JPanel();
		StatisticPanel.setBackground(new Color(255, 255, 255));
		StatisticPanel.setBounds(0, 50, 561, 497);
		StatisticPanel.setLayout(null);

		JLabel TitleStatistic = new JLabel("STATISTICS");
		TitleStatistic.setFont(new Font("Tahoma", Font.PLAIN, 26));
		TitleStatistic.setBounds(195, 20, 160, 32);
		StatisticPanel.add(TitleStatistic);

		tableOutput = new JTable();

		tableOutput.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tableOutput.setBackground(new Color(255, 255, 255));
		tableOutput.setModel(new DefaultTableModel(new Object[][] {

		}, new String[] { "document_id", "number of occurncces", "Locations of matching", "document_size", "data" }));

		tableOutput.getColumnModel().getColumn(0).setPreferredWidth(76);
		tableOutput.getColumnModel().getColumn(0).setMinWidth(76);
		tableOutput.getColumnModel().getColumn(1).setPreferredWidth(76);
		tableOutput.getColumnModel().getColumn(1).setMinWidth(76);
		tableOutput.getColumnModel().getColumn(2).setPreferredWidth(76);
		tableOutput.getColumnModel().getColumn(2).setMinWidth(76);
		tableOutput.getColumnModel().getColumn(3).setPreferredWidth(76);
		tableOutput.getColumnModel().getColumn(3).setMinWidth(76);
		tableOutput.getColumnModel().getColumn(4).setPreferredWidth(0);
		tableOutput.getColumnModel().getColumn(4).setMinWidth(0);
		tableOutput.getColumnModel().getColumn(4).setMaxWidth(0);
		tableOutput.getColumnModel().getColumn(4).setWidth(0);
		;
		tableOutput.setBounds(0, 120, 580, 357);
		tableOutput.getTableHeader().setBackground(Color.LIGHT_GRAY);
		tableOutput.setRowHeight(50);
		JScrollPane TableScroll = new JScrollPane(tableOutput);
		TableScroll.setBounds(0, 140, 561, 357);
		StatisticPanel.add(TableScroll);
		main.add(StatisticPanel);

		JLabel ShowDocumentLabel = new JLabel("Enter document_id :");
		ShowDocumentLabel.setFont(new Font("Tahoma", Font.PLAIN, 23));
		ShowDocumentLabel.setBounds(10, 65, 211, 19);
		StatisticPanel.add(ShowDocumentLabel);

		textFieldShowDocument = new JTextField();
		textFieldShowDocument.setBounds(231, 64, 96, 20);
		StatisticPanel.add(textFieldShowDocument);
		textFieldShowDocument.setColumns(10);

		JButton displayBtn = new JButton("Display document");
		displayBtn.setBackground(new Color(0, 128, 255));
		displayBtn.setForeground(new Color(255, 255, 255));
		displayBtn.setFont(new Font("Tahoma", Font.PLAIN, 17));
		displayBtn.setBounds(361, 59, 190, 34);
		StatisticPanel.add(displayBtn);

		displayBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (textFieldShowDocument.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Please choose document", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				int docID = Integer.parseInt(textFieldShowDocument.getText());

				DefaultTableModel model = (DefaultTableModel) tableOutput.getModel();

				try {
					String filename = "outPatternDocument.txt";
					BufferedWriter out = new BufferedWriter(new FileWriter(filename));
					out.write((String) model.getValueAt(docID, 4));
					out.close();
				} catch (Exception ee) {

				}

				// to show the file
				Runtime rs = Runtime.getRuntime();
				try {
					rs.exec("notepad outPatternDocument.txt");
				} catch (IOException eee) {
					System.out.println(e);
				}

			}
		});

		JButton chartBtn_1 = new JButton("Display chart");
		chartBtn_1.setForeground(Color.WHITE);
		chartBtn_1.setFont(new Font("Tahoma", Font.PLAIN, 17));
		chartBtn_1.setBackground(new Color(0, 128, 255));
		chartBtn_1.setBounds(361, 95, 190, 34);
		StatisticPanel.add(chartBtn_1);

		JButton reGenreateBtn = new JButton("Regenrate Documents");
		reGenreateBtn.setFont(new Font("Tahoma", Font.PLAIN, 19));
		reGenreateBtn.setBounds(625, 519, 227, 35);
		main.add(reGenreateBtn);

		reGenreateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (sizeTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "please enter size", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int size = Integer.parseInt(sizeTextField.getText());

				// when we click on regenerate button; it regenerates the docs
				generateDocuments(size);
			}
		});

		chartBtn_1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (counterTimeBoyerMoore == 0 && counterTimeBrutrForce == 0 && counterTimeKMP == 0) {
					JOptionPane.showMessageDialog(null, "Please run algorithm first", "Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				if (sizeTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "please enter size", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}

				int size = Integer.parseInt(sizeTextField.getText());

				// xData stores the x-axis data, x-axis represents the input size
				double[] xData = new double[((maxSize - size) / 100) + 1];
				for (int kk = 0; kk < xData.length; kk++, size += 100) {
					xData[kk] = size;
				}

				// Chart variable
				XYChart chart = null;

				// creates the chart with the appropriate title based on choice of algorithm
				if (BF_RadioButton.isSelected()) {
					chart = QuickChart.getChart("Brute-Force Chart", "Input size", "Time (ms)", "T(n)", xData,
							avgTimeBruteForce);
				} else if (BMRadioButton.isSelected()) {
					chart = QuickChart.getChart("Boyer-Moore Chart", "Input size", "Time (ms)", "T(n)", xData,
							avgTimeBoyerMoore);
				} else if (KMP_RadioButton.isSelected()) {
					chart = QuickChart.getChart("KMP Chart", "Input size", "Time (ms)", "T(n)", xData, avgTimeKMP);
				} else {
					JOptionPane.showMessageDialog(null, "please choose algorithm", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}

				JFrame frame = new JFrame("Chart");
				frame.getContentPane().setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				// chart
				JPanel chartPanel = new XChartPanel<XYChart>(chart);
				frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
				frame.pack();
				frame.setVisible(true);

			}
		});

	}

	// displays the documents' info. that contain the pattern (displays in the GUI
	// table).
	// plus extra stats
	public void displayDocuments() {
		DefaultTableModel model = (DefaultTableModel) tableOutput.getModel();
		model.setRowCount(0);
		if (patternDocuments == null) {
			JOptionPane.showMessageDialog(null, "could not find the pattern in any document", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
		for (int i = 0; patternDocuments != null && i < patternDocuments.size(); i++) {
			model.addRow(new Object[] { i + 1, locations.get(i).size(), locations.get(i),
					patternDocuments.get(i).length(), patternDocuments.get(i) });
		}
		patternDocuments = null;

	}

	// generates documents with lengths starting at size and ending with maxSize,
	// incrementing 100 each time
	public void generateDocuments(int size) {

		// ((maxSize - size) / 100) + 1 == gives number of documents
		generatedDocuments = new String[((maxSize - size) / 100) + 1];
		int c = 0;
		for (int i = size; i <= maxSize; i += 100, c++) {
			generatedDocuments[c] = PMAlgorithms.generateString(i);
		}
	}

	/*
	 * For each size, the method runs the chosen algorithm 5 times on the same
	 * string and stores any occurrence to the list GUI_App.locations. It also
	 * stores computes the averages of each size and stores them in their
	 * corresponding array, as well as storing the documents that contain the
	 * pattern in the list GUI_App.patternDocuments.
	 * 
	 */
	public void runAlgorithm(int size, String pattern, int choice) {

		// ((maxSize - size) / 100) + 1 == gives number of documents
		this.avgTimeBruteForce = new double[((maxSize - size) / 100) + 1];
		this.avgTimeBoyerMoore = new double[((maxSize - size) / 100) + 1];
		this.avgTimeKMP = new double[((maxSize - size) / 100) + 1];

		counterTimeBrutrForce = 0;
		counterTimeBoyerMoore = 0;
		counterTimeKMP = 0;

		char[] pat = pattern.toCharArray();

		int index = 0; // to index average arrays

		// to store documents that contain the pattern
		List<String> documents = new LinkedList<>();

		// a list of lists of all locations of the first letter of the pattern in the
		// text
		List<List<Integer>> locations = new LinkedList<>();

		for (int j = size; j <= maxSize; j += 100) {

			List<Integer> loc = null;
			String genString = generatedDocuments[index];

			// run five times
			for (int i = 0; i < 5; i++) {

				if (choice == 0) {
					loc = PMAlgorithms.bruteForce(genString.toCharArray(), pat);
				} else if (choice == 1) {
					loc = PMAlgorithms.boyerMoore(genString.toCharArray(), pat);
				} else if (choice == 2) {
					loc = PMAlgorithms.KMP(genString.toCharArray(), pat);
				}

			}

			// pattern found
			if (loc.size() != 0) {
				documents.add(genString);
				locations.add(loc);
				GUI_App.patternDocuments = documents;
				GUI_App.locations = locations;
			}

			
			// / 1000000 to convert from nano to milli
			if (choice == 0) {
				avgTimeBruteForce[index++] = (counterTimeBrutrForce / (5.0));
			} else if (choice == 1) {
				avgTimeBoyerMoore[index++] = (counterTimeBoyerMoore / (5.0));
			} else if (choice == 2) {
				avgTimeKMP[index++] = (counterTimeKMP / (5.0));
			}

		}
		displayDocuments();
	}

}
