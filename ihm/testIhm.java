package ihm;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class testIhm {

	private JFrame frame;
	private JTable tableau;

	/**
	 * Launch the application.
	 */
	public void ouvrirFenetre(){
		this.frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public testIhm() {
		initialize();
	}
	
	/**
	 * Permet de centrer les éléments dans un tableau
	 * @param table
	 */
	private void centrerTable(JTable table) {     
		DefaultTableCellRenderer custom = new DefaultTableCellRenderer(); 
		custom.setHorizontalAlignment(JLabel.CENTER); 
		for (int i=0 ; i<table.getColumnCount() ; i++){
			table.getColumnModel().getColumn(i).setCellRenderer(custom); 
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1157, 740);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		/* Par défaut le tableau est vide */
		final Vector donneesInit = new Vector();
		
	   
	   /* On prépare le type des colones */
	   Class[] typeColones = {String.class, String.class, String.class, Boolean.class};
	   
	   
	   /* Modèle pour notre tableau */
	   class ModeleTableau extends AbstractTableModel{
			private Vector donnees;
			private String[] titreCol = new String[]{"Id","Nom","Prénom", "Invité"};
			private Class[] classCol;
	 
			public ModeleTableau(Vector donnees, Class[] classCol){
				super();
				this.classCol = classCol;
				this.donnees = donnees;
			}
			
			public String getColumnName(int col) {
			     return this.titreCol[col];
			}
	 
			public int getColumnCount() {
				return this.titreCol.length;
			}
	 
			public int getRowCount() {
				return this.donnees.size();
			}
	 
			public Object getValueAt(int row, int col) {
				Object[] obj = (Object[]) this.donnees.get(row);
				return obj[col];
			}
	 
			public boolean isCellEditable(int arg0, int arg1) {
				if(arg1 == 3){
					return true;
				}else{
					return false;
				}
				
			}
	 
			public Class getColumnClass(int arg0) {
				return this.classCol[arg0];
			}
			
			/* Pour ajouter une ligne au Model */
			public void addRow(Object[] donnees){
				this.donnees.add(donnees);
				/* Pour que le changement dans les donnees soit pris en compte */
				fireTableDataChanged(); 
			}
			
			public void setValueAt(Object val, int row, int col) {
				Object[] obj = (Object[]) this.donnees.get(row);
				obj[col] = val;
			}
	 
		}
	   
	   /* On construit le modèle par défaut */
	   ModeleTableau modele = new ModeleTableau(donneesInit, typeColones);
	   
	   /* On ajoute les données dans le modèle */
	   Object[] donnees = {"2","Ibra","Zlatan",new Boolean(false)};
	   modele.addRow(donnees);
	   Object[] donnees2 = {"3","Moulto", "Flo", new Boolean(false)};
	   modele.addRow(donnees2);
	   
	   /* on créer le tableau à partir du modèle */
	   this.tableau = new JTable(modele);
	   
	   /* On masque la colone Id */
	  tableau.getColumnModel().getColumn(0).setMinWidth(0);
	  tableau.getColumnModel().getColumn(0).setMaxWidth(0);
	  
	  /* On bloque le redimensionnement des colones */
	  tableau.getTableHeader().setReorderingAllowed(false);
	  tableau.getTableHeader().setResizingAllowed(false);
	  
	  /* On ajoute un ascenceur au tableau */
	   JScrollPane scrollPane = new JScrollPane(tableau);
	   scrollPane.setBounds(270, 27, 856, 516);
	   panel.add(scrollPane);
	   scrollPane.setViewportView(tableau);
	}
	
}
