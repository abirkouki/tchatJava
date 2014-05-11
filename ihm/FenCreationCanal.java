package ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import client.Utilisateur;

public class FenCreationCanal {

	private Socket sockConnexion;
	private Utilisateur utilisateur;
	private JFrame frmApplicationTchatStri;

	/**
	 * Buffer permettant de lire les messages du serveur
	 */
	private BufferedReader lire = null;
	
	/**
	 * Bufer d'écriture pour envoyer des messages au serveur
	 */
	private PrintWriter ecrire;
	private JTextField saiTitreCanal;
	private final JLabel libTypeCanal = new JLabel("Type de canal :");
	private JTable tableau;
	private String listeInvite = "";

	/**
	 * Launch the application.
	 */
	public void ouvrirFenetre(){
		this.frmApplicationTchatStri.setVisible(true);
	}
	

	/**
	 * Create the application.
	 * @throws InterruptedException 
	 */
	public FenCreationCanal(Socket sockConnexion,Utilisateur utilisateur){
		this.sockConnexion = sockConnexion;
		this.utilisateur = utilisateur;
		initialize();
	}
	
	/**
	 * Ferme la fenêtre
	 */
	public void fermerFenetre(){
		this.frmApplicationTchatStri.setVisible(false);
		this.frmApplicationTchatStri.dispose();
	}
	
	/**
	 * Récupère un message reçu du client
	 * @return Message reçu du client.
	 */
	public String lireMesg(){
		try{
			String message;
			/* On initialise le buffer de lecture pour récupérer la confirmation de connexion de la part du serveur */
			lire = new BufferedReader(new InputStreamReader(this.sockConnexion.getInputStream()));
			/* On récupère le message du serveur */
			message = lire.readLine();
			return message;
		}
		catch (IOException exception){
			return("Impossible de récupérer le message du serveur");
		}
	}
	
	/**
	 * Envoie un message à un client, puis vide le buffer d'écriture.
	 * @param message Message que le serveur veut envoyer au client
	 */
	public void envoyerMesg(String message){
		try{
			ecrire = new PrintWriter(this.sockConnexion.getOutputStream());
			ecrire.println(message);
			ecrire.flush();
		}
		catch(IOException exception){
			System.out.println("Imposible d'envoyer un message au client");
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		final String infosUtil;
		infosUtil = String.valueOf(utilisateur.getId())+"/"+utilisateur.getLogin()+"/"+utilisateur.getNom()+"/"+utilisateur.getPrenom()+"/"+utilisateur.getPassword()+"/"+String.valueOf(utilisateur.getGrade());
		frmApplicationTchatStri = new JFrame();
		frmApplicationTchatStri.setFont(null);
		frmApplicationTchatStri.setTitle("Application Tchat STRI");
		frmApplicationTchatStri.setBounds(100, 100, 1024, 700);
		frmApplicationTchatStri.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel panel = new JPanel();
		frmApplicationTchatStri.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		final JInternalFrame intFenAddInvit = new JInternalFrame("Ajouter des invités sur votre canal");
		intFenAddInvit.setBounds(22, 86, 998, 493);
		intFenAddInvit.setOpaque(false);
		panel.add(intFenAddInvit);
		
		JLabel libTitre = new JLabel("CREATION D'UN CANAL");
		libTitre.setForeground(Color.RED);
		libTitre.setFont(new Font("Liberation Serif", Font.BOLD, 23));
		libTitre.setBounds(371, 36, 279, 35);
		panel.add(libTitre);
		
		final JLabel libInfo = new JLabel("Veuillez renseigner toutes les informations sur le canal que vous voulez créer");
		libInfo.setHorizontalAlignment(SwingConstants.CENTER);
		libInfo.setFont(new Font("Liberation Serif", Font.BOLD | Font.ITALIC, 15));
		libInfo.setBounds(12, 122, 998, 18);
		panel.add(libInfo);
		
		JLabel libTitreCanal = new JLabel("Titre de votre canal :");
		libTitreCanal.setHorizontalAlignment(SwingConstants.LEFT);
		libTitreCanal.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libTitreCanal.setBounds(241, 237, 157, 21);
		panel.add(libTitreCanal);
		
		saiTitreCanal = new JTextField();
		saiTitreCanal.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		saiTitreCanal.setColumns(10);
		saiTitreCanal.setBounds(531, 237, 356, 21);
		panel.add(saiTitreCanal);
		libTypeCanal.setBounds(267, 328, 131, 31);
		panel.add(libTypeCanal);
		libTypeCanal.setHorizontalAlignment(SwingConstants.LEFT);
		libTypeCanal.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		
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
	   final ModeleTableau modele = new ModeleTableau(donneesInit, typeColones);
	   
	   final JButton btnAddInvit = new JButton("Ajouter des invités");
	   JPanel panel_1 = new JPanel();
	   intFenAddInvit.getContentPane().add(panel_1, BorderLayout.CENTER);
	   panel_1.setLayout(null);
	   
	   JButton btnTermine = new JButton("Termine");
	   btnTermine.addActionListener(new ActionListener() {
	   	public void actionPerformed(ActionEvent arg0) {
	   		/* On parcours chaque ligne du tableau*/
	   		int j; /* indice de parcours des lignes du tableau */
	   		for(j=0;j<tableau.getRowCount();j++){
	   			/* on regarde la valeur de la colone invité */
	   			if(tableau.getValueAt(j, 3).equals(true)){
	   				/* Si la case invité est cochée, on ajoute l'identifiant a la liste des invités */
	   				listeInvite += String.valueOf(tableau.getValueAt(j, 0))+"#";
	   			}
	   		}
	   		/* On ferme ensuite la fenêtre interne */
	   		intFenAddInvit.setVisible(false);
	   		btnAddInvit.setVisible(true);
	   	}
	   });
	   btnTermine.setFont(new Font("Liberation Serif", Font.BOLD, 15));
	   btnTermine.setBounds(845, 12, 131, 25);
	   panel_1.add(btnTermine);
		
		btnAddInvit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listeInvite = ""; /* on vide la liste des invités */
				/* On demande au serveur la liste des utilisateurs */
				envoyerMesg("7");
				if(Integer.parseInt(lireMesg()) == 7){
				
					String listeUtils = lireMesg();
					String[] listeUtilsDecomp = listeUtils.split("/");
					int i = 0;
					/* On va trier dans l'ordre alphabétique des noms */
					String temp;
					for(i=0;i<listeUtilsDecomp.length-1;i++){
						/* On compare i et i+1 */
						if(listeUtilsDecomp[i].split("#")[1].compareToIgnoreCase(listeUtilsDecomp[i+1].split("#")[1])<=0 ){
							/* si i < i+1 , on passe au suivant */
						}else{
							/* si i > i+1, on les inverse et on repart à 0 */
							temp = listeUtilsDecomp[i];
							listeUtilsDecomp[i] = listeUtilsDecomp[i+1];
							listeUtilsDecomp[i+1] = temp;
							i = -1;
						}
					}
					/* On ajoute les utilisateurs triés dans l'ordre alphabétique dans le tableau */
					for(i=0;i<listeUtilsDecomp.length;i++){
						Object[] donnees = {listeUtilsDecomp[i].split("#")[0],listeUtilsDecomp[i].split("#")[1],listeUtilsDecomp[i].split("#")[2], new Boolean(false)};
						modele.addRow(donnees);
					}
					
					/* on créer le tableau à partir du modèle */
					   tableau = new JTable(modele);
					   /* On masque la colone Id */
						  tableau.getColumnModel().getColumn(0).setMinWidth(0);
						  tableau.getColumnModel().getColumn(0).setMaxWidth(0);
						  
						  /* On bloque le redimensionnement des colones */
						  tableau.getTableHeader().setReorderingAllowed(false);
						  tableau.getTableHeader().setResizingAllowed(false);
						  
						  /* On ajoute un ascenceur au tableau */
						   JScrollPane scrollPane = new JScrollPane(tableau);
						   scrollPane.setBounds(270, 27, 856, 516);
						   scrollPane.setBackground(new Color(238,238,238));
						   scrollPane.setOpaque(true);
						   intFenAddInvit.getContentPane().add(scrollPane, BorderLayout.NORTH);
						   scrollPane.setViewportView(tableau);
					intFenAddInvit.setVisible(true);
					btnAddInvit.setVisible(false);
				}
				   
			}
		});
		btnAddInvit.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnAddInvit.setBounds(577, 424, 169, 25);
		/* Au départ comme c'est public qui est select par défaut on met le bouton addInvit inviseble */
		btnAddInvit.setVisible(false);
		panel.add(btnAddInvit);
		
		final JRadioButton radioTypePriv = new JRadioButton("Privé");
		final JRadioButton radioTypePub = new JRadioButton("Public");
		radioTypePub.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* Si on coche public, on décoche automatiquement privé */
				radioTypePub.setSelected(true);
				radioTypePriv.setSelected(false);
				btnAddInvit.setVisible(false);
			}
		});
		radioTypePub.setSelected(true);
		radioTypePub.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		radioTypePub.setBounds(531, 332, 75, 23);
		panel.add(radioTypePub);
		
		
		radioTypePriv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* Si on coche privé, on décoche automatiquement public */
				radioTypePub.setSelected(false);
				radioTypePriv.setSelected(true);
				btnAddInvit.setVisible(true);
			}
		});
		radioTypePriv.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		radioTypePriv.setBounds(671, 333, 75, 23);
		panel.add(radioTypePriv);
		
		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* On ferme la fenêtre et on retourne à l'accueil */
				FenAccueil fenAcc = new FenAccueil(sockConnexion, infosUtil);
				fenAcc.ouvrirFenetre();
				fermerFenetre();
			}
		});
		btnAnnuler.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnAnnuler.setBounds(879, 618, 131, 25);
		panel.add(btnAnnuler);
		
		JButton btnValider = new JButton("Valider");
		btnValider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* Vérification que le titre n'est pas vide */
				if(saiTitreCanal.getText().compareTo("") == 0){
					/* si le titre n'est pas rentré, on informe l'utilisateur */
					libInfo.setText("ERREUR, vous devez donner un titre à votre canal");
				}else{
					/* On teste maintenant, que le titre ne contient pas de / (caractère interdit) */
					if(saiTitreCanal.getText().contains("/") == true){
						/* si il y a un / dans le titre on informe l'utilisateur */
						libInfo.setText("ERREUR, le titre ne doit pas contenir de /");
					}else{
						/* On regarde si il s'agit d'un canal public ou privé */
						int type; /* type de canal 1:public 2:privé */
						if(radioTypePriv.isSelected() == true){
							type = 2;
						}else{
							type = 1;
						}
						/* On va préparer la chaine qui va être envoyée au serveur */
						String infosCanal = String.valueOf(utilisateur.getId())+"/"+saiTitreCanal.getText()+"/"+String.valueOf(type)+"/"+listeInvite;
						/* On fait une demande d'ajout de canal au serveur */
						envoyerMesg("3");
						/* On attend que le serveur valide notre requete */
						if(Integer.parseInt(lireMesg()) == 3){
							/* On envoie les infos au serveur */
							envoyerMesg(infosCanal);
							/* On attend de voir si le serveur confirme la création du canal */
							if(Integer.parseInt(lireMesg()) == 1){
								JOptionPane.showMessageDialog(panel, "Votre création d'un canal a bien été prise en compte","Création d'un canal",JOptionPane.INFORMATION_MESSAGE);
								FenAccueil fenAcc = new FenAccueil(sockConnexion, infosUtil);
								fenAcc.ouvrirFenetre();
								fermerFenetre();
							}else{
								JOptionPane.showMessageDialog(panel, "Votre création d'un canal a échoué, merci de réessayer ultérieurement","Erreur création d'un canal",JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		});
		btnValider.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnValider.setBounds(736, 618, 131, 25);
		panel.add(btnValider);
		
	}
}
