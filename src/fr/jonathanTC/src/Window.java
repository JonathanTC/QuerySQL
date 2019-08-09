package fr.jonathanTC.src;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

public class Window extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea queryArea;
	private JTable table;
	private JLabel label;
	private DefaultTableModel tableModel;
	private JScrollPane scroll;

	public Window() {
		super();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(400, 300);
		
		initializeComponent();
	}
	
	public Window(String title) {
		super(title);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(400, 300);
		
		initializeComponent();
	}
	
	public void initializeComponent() {
		this.setLayout(new BorderLayout());
		
		// make toolbar
		JToolBar bar = new JToolBar();
		this.add(bar, BorderLayout.NORTH);
		
		// make execute button and his action
		JButton bexecute = new JButton("Execute");
				bexecute.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						executeAction();
					}
				});
		bar.add(bexecute);
		
		// make text area
		queryArea = new JTextArea();
		queryArea.setText("SELECT * FROM classe");
		
		// make scrollpane for JTable
		table = new JTable();
		scroll = new JScrollPane(table);
		
		// make splitpane
		JSplitPane split = new JSplitPane();
		split.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split.setTopComponent(queryArea);
		split.setBottomComponent(scroll);
		this.add(split, BorderLayout.CENTER);
		
		//make info label
		label = new JLabel("Information :");
		this.add(label, BorderLayout.SOUTH);
	}
	
	
	public void executeAction() {		
		try {
			Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Ecole", "postgres", "popol");
			Statement state = conn.createStatement();
			
			ResultSet result = state.executeQuery(queryArea.getText());
			ResultSetMetaData metaData = result.getMetaData();

			// make table and his model
			tableModel = new DefaultTableModel();
			table.setModel(tableModel);
			
			for(int i=1; i<=metaData.getColumnCount(); i++) {
				String colName = metaData.getColumnLabel(i);
				tableModel.addColumn(colName);
			}
			
			int count = 0;
			double time = System.currentTimeMillis();
			
			while(result.next()) {
				
				Object[] row = new Object[metaData.getColumnCount()];
				
				for(int i=1; i<=metaData.getColumnCount(); i++) {
					row[i-1] = result.getObject(i).toString();
				}
				
				tableModel.addRow(row);
				count++;
			}
			
			time = System.currentTimeMillis() - time;
			label.setText("time : " + time + " ms\t lines : " + count);
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}
