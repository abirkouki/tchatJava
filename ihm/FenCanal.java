package ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

import com.sun.org.apache.bcel.internal.generic.LUSHR;

import serveur.Canal;
import client.Utilisateur;

public class FenCanal{
	
	/**
	 * Frame principale de la fenêtre.
	 */
	private JFrame frmApplicationTchatStri;
	
	/**
	 * Socket de connexion du client
	 */
	private Socket sockConnexion;
	
	/**
	 * Permet de savoir si il s'agit d'un canal privé ou pas
	 */
	private Boolean isPrive;
	
	/**
	 * Champs de saisie permettant d'envoyer un message sur le canal.
	 */
	private JTextField saiMesg;
	
	/**
	 * Canal correspondant à la fenêtre.
	 */
	private Canal canal;
	
	/**
	 * Utilisateur de la fenêtre.
	 */
	private Utilisateur utilisateur;
	
	/**
	 * Permet de réaliser un retour à la ligne
	 */
	private String newLine = System.getProperty("line.separator");
	
	/**
	 * Zone de texte permettant d'afficher les messages des clients.
	 */
	private JTextPane txtTchat;
	
	/**
	 * Buffer permettant de lire les messages du serveur
	 */
	private BufferedReader lire = null;
	
	/**
	 * Bufer d'écriture pour envoyer des messages au serveur
	 */
	private PrintWriter ecrire;
	
	/**
	 * Titre du canal
	 */
	private JLabel libNomCanal;
	
	/**
	 * Tableau contenant la liste des utilisateurs
	 */
	private JTable tableau;
	
	/**
	 * Chaine contenant les identifiants des utilisateurs connectés sur le canal
	 */
	private String listeInvite = "";
	
	/**
	 * Liste des utilisateurs connectés sur le canal
	 */
	private JList listListeUsers;
	
	/**
	 * Permet de savoir si l'utilisateur est un modérateur du canal
	 */
	private Boolean isModerateur;
	
	/**
	 * Champs de saisie du titre du canal
	 */
	private JTextField saiTitre;
	
	/**
	 * Variable permettant de savoir si on a modifié le titre
	 */
	private Boolean modifTitre = false;

	/**
	 * Ouvre la fenêtre
	 */
	public void ouvrirFenetre(){
		this.frmApplicationTchatStri.setVisible(true);
	}
	

	/**
	 * Créer une fenêtre de canal.
	 * @param sockConnexion Socket de connexion du client.
	 * @param canal Canal correspondant à la fenêtre
	 * @param utilisateur Utilisateur utilisant l'application
	 * @throws InterruptedException
	 */
	public FenCanal(Socket sockConnexion, Canal canal, Utilisateur utilisateur, Boolean moderateur, Boolean prive) throws InterruptedException {
		this.sockConnexion = sockConnexion;
		this.canal = canal;
		this.utilisateur = utilisateur;
		this.isModerateur = moderateur;
		this.isPrive = prive;
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
		 * Fonction permettant d'actualiser la liste des messages, le titre du canal et la liste des utilisateurs présents dans le canal.
		 * @param txtTchat Zone de texte contenant les messages.
		 */
		public void actualiser(JTextPane txtTchat){
			/* On vérifie que le champs de saisie n'est pas focus et qu'il est vide */
			if(saiMesg.getText().compareTo("")==0 && modifTitre == false){
				/* On envoie une demande d'actualisation au serveur */
				envoyerMesg("5");
				/* On attend la confirmation */
				if(Integer.parseInt(lireMesg()) == 5){
					/* On envoie l'identifiant du canal */
					envoyerMesg(String.valueOf(canal.getId()));
					/* On attend la réponse du serveur avec tout les messages */
					String messages = lireMesg();
					
					
					//System.out.println("Message du serveur : "+messages);
					
					/* On met à jour les messages */
					String[] messagesDecomp = messages.split("#"); /* on décompose */
					int nbMessages = messagesDecomp.length; /* on récupère le nombre de messages */
					int i; /* indice de parcours */
					messages = ""; /* on vide la variable message */
					for(i=0;i<nbMessages;i++){
						messages += messagesDecomp[i]+newLine;
					}
					/* On ajoute la nouvelle chaine au champs texte */
					this.txtTchat.setText(messages);
					
					/* On met à jour le titre */
					/* On récupère le titre du canal */
					String titre = lireMesg();
					//System.out.println("Mesg recu :"+titre);
					this.saiTitre.setText(titre.split("/")[0]);
					//System.out.println("On a passé l'étape du titre");
					/* On met à jour la liste des utilisateurs connectés sur le canal */
					/* Les visiteurs ne sont pas affichés dans cette liste car ils ne sont pas membres */
					/* On récupère la liste des utilisateurs du canal */
					if(titre.split("/")[1].compareTo("0") == 0){
						/* pas d'utilisateur connectés */
					}else{
							
							String[] utilCanalDecomp = titre.split("/")[1].split("#");
							//System.out.println(titre.split("/")[1]);
							this.listListeUsers.removeAll();
							this.listListeUsers.setListData(utilCanalDecomp);
					}
				}
			}
		}
		

	/**
	 * Initialise la fenêtre avec tous ses composants.
	 * @throws InterruptedException 
	 */
	private void initialize() throws InterruptedException {
		frmApplicationTchatStri = new JFrame();
		frmApplicationTchatStri.setTitle("Application Tchat STRI");
		frmApplicationTchatStri.setResizable(false);
		frmApplicationTchatStri.setBounds(100, 100, 1024, 700);
		frmApplicationTchatStri.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel panel = new JPanel();
		frmApplicationTchatStri.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		final JScrollPane scrollPaneMesg = new JScrollPane();
		scrollPaneMesg.setBounds(23, 27, 747, 547);
		panel.add(scrollPaneMesg);
		this.txtTchat = new JTextPane();
		scrollPaneMesg.setViewportView(txtTchat);
		
		this.txtTchat.setEditable(false);
		
		listListeUsers = new JList();
		listListeUsers.setBackground(UIManager.getColor("ColorChooser.swatchesDefaultRecentColor"));
		listListeUsers.setBounds(782, 65, 228, 433);
		panel.add(listListeUsers);
		
		saiMesg = new JTextField();
		saiMesg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode ();
                if (code == KeyEvent.VK_ENTER){
                	/* On vérifie que le sai n'est pas vide */
    				if(saiMesg.getText().compareTo("") != 0){
    					/* on vérifie qu'il n'y a pas de # dans le message */
    					if(saiMesg.getText().contains("#")){
    						JOptionPane.showMessageDialog(panel, "ERREUR, le caractère # est interdit !","ERREUR, caractère interdit",JOptionPane.ERROR_MESSAGE);
    					}else{
    						/* on envoie une requete d'envoie message au serveur */
    						envoyerMesg("4");
    						/* On attend la réponse du serveur */
    						if(Integer.parseInt(lireMesg()) == 4){
    							/* On regarde si il s'agit d'un visiteur */
    							if(utilisateur.getId() == -1){
    								envoyerMesg(String.valueOf(utilisateur.getId())+"#"+utilisateur.getNom()+"#"+utilisateur.getPrenom()+"#"+String.valueOf(canal.getId())+"#"+saiMesg.getText());
    							}else{
    								/* on envoie le message au serveur */
        							System.out.println("on envoie :"+String.valueOf(utilisateur.getId())+"#"+String.valueOf(canal.getId())+"#"+saiMesg.getText());
        							envoyerMesg(String.valueOf(utilisateur.getId())+"#"+String.valueOf(canal.getId())+"#"+saiMesg.getText());
    							}
    							
    							/* On vérifie que le serveur a bien reçu le message */
    							if(Integer.parseInt(lireMesg()) == 1){
    								saiMesg.setText("");
    								actualiser(txtTchat);
    							}else{
    								JOptionPane.showMessageDialog(panel, "ERREUR, votre message n'a pas été envoyé","ERREUR, message non envoyé",JOptionPane.ERROR_MESSAGE);
    							}
    						}
    					}
    				}
                }
			}
		});
		saiMesg.setFont(new Font("Liberation Serif", Font.PLAIN, 17));
		saiMesg.setBounds(23, 586, 621, 35);
		panel.add(saiMesg);
		saiMesg.setColumns(10);
		
		libNomCanal = new JLabel("Vous êtes actuellement sur le canal : ");
		libNomCanal.setFont(new Font("Liberation Serif", Font.BOLD, 17));
		libNomCanal.setBounds(23, 0, 283, 27);
		panel.add(libNomCanal);
		
		JButton btnQuitter = new JButton("Quitter Canal");
		btnQuitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* on envoi au serveur une demande de déconnexion d'un canal */
				envoyerMesg("9");
				/* on attend la réponse du serveur */
				if(Integer.parseInt(lireMesg())==9){
					/* le serveur confirme la demande de déconnexion */
					envoyerMesg(String.valueOf(canal.getId())+"#"+String.valueOf(utilisateur.getId()));
					/* On attend la réponse du serveur */
					if(Integer.parseInt(lireMesg()) == 1){
						/* Déconnexion ok */
						/* on retourne sur la page d'accueil */
						FenAccueil fenAcc = new FenAccueil(sockConnexion,String.valueOf(utilisateur.getId())+"/"+utilisateur.getLogin()+"/"+utilisateur.getNom()+"/"+utilisateur.getPrenom()+"/"+utilisateur.getPassword()+"/"+String.valueOf(utilisateur.getGrade()));
						fenAcc.ouvrirFenetre();
						fermerFenetre();
					}else{
						JOptionPane.showMessageDialog(panel, "ERREUR, votre demande de déconnexion du canal a échouée","ERREUR, déconnexion canal",JOptionPane.ERROR_MESSAGE);
					}
				}else{
					JOptionPane.showMessageDialog(panel, "ERREUR, votre demande de déconnexion du canal a échouée","ERREUR, déconnexion canal",JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		btnQuitter.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnQuitter.setBounds(23, 640, 139, 25);
		panel.add(btnQuitter);
		
		JButton btnAccueil = new JButton("Retour Accueil");
		btnAccueil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* On ferme la fenêtre du canal et on affiche la page d'accueil */
				FenAccueil fenAcc = new FenAccueil(sockConnexion,String.valueOf(utilisateur.getId())+"/"+utilisateur.getLogin()+"/"+utilisateur.getNom()+"/"+utilisateur.getPrenom()+"/"+utilisateur.getPassword()+"/"+String.valueOf(utilisateur.getGrade()));
				fenAcc.ouvrirFenetre();
				fermerFenetre();
			}
		});
		btnAccueil.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnAccueil.setBounds(871, 640, 139, 25);
		panel.add(btnAccueil);
		
		JButton btnEnvoyer = new JButton("Envoyer");
		btnEnvoyer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* On vérifie que le sai n'est pas vide */
				if(saiMesg.getText().compareTo("") != 0){
					/* on vérifie qu'il n'y a pas de # dans le message */
					if(saiMesg.getText().contains("#")){
						JOptionPane.showMessageDialog(panel, "ERREUR, le caractère # est interdit !","ERREUR, caractère interdit",JOptionPane.ERROR_MESSAGE);
					}else{
						/* on envoie une requete d'envoie message au serveur */
						envoyerMesg("4");
						/* On attend la réponse du serveur */
						if(Integer.parseInt(lireMesg()) == 4){
							/* On regarde si il s'agit d'un visiteur */
							if(utilisateur.getId() == -1){
								envoyerMesg(String.valueOf(utilisateur.getId())+"#"+utilisateur.getNom()+"#"+utilisateur.getPrenom()+"#"+String.valueOf(canal.getId())+"#"+saiMesg.getText());
							}else{
								/* on envoie le message au serveur */
    							System.out.println("on envoie :"+String.valueOf(utilisateur.getId())+"#"+String.valueOf(canal.getId())+"#"+saiMesg.getText());
    							envoyerMesg(String.valueOf(utilisateur.getId())+"#"+String.valueOf(canal.getId())+"#"+saiMesg.getText());
							}
							
							/* On vérifie que le serveur a bien reçu le message */
							if(Integer.parseInt(lireMesg()) == 1){
								saiMesg.setText("");
								actualiser(txtTchat);
							}else{
								JOptionPane.showMessageDialog(panel, "ERREUR, votre message n'a pas été envoyé","ERREUR, message non envoyé",JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
				
				
			}
		});
		
		btnEnvoyer.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode ();
                if (code == KeyEvent.VK_ENTER){
                	/* On vérifie que le sai n'est pas vide */
    				if(saiMesg.getText().compareTo("") != 0){
    					/* on vérifie qu'il n'y a pas de | dans le message */
    					if(saiMesg.getText().contains("#")){
    						JOptionPane.showMessageDialog(panel, "ERREUR, le caractère # est interdit !","ERREUR, caractère interdit",JOptionPane.ERROR_MESSAGE);
    					}else{
    						/* on envoie une requete d'envoie message au serveur */
    						envoyerMesg("4");
    						/* On attend la réponse du serveur */
    						if(Integer.parseInt(lireMesg()) == 4){
    							/* on envoie le message au serveur */
    							System.out.println("on envoie :"+String.valueOf(utilisateur.getId())+"#"+String.valueOf(canal.getId())+"#"+saiMesg.getText());
    							envoyerMesg(String.valueOf(utilisateur.getId())+"#"+String.valueOf(canal.getId())+"#"+saiMesg.getText());
    							/* On vérifie que le serveur a bien reçu le message */
    							if(Integer.parseInt(lireMesg()) == 1){
    								saiMesg.setText("");
    								actualiser(txtTchat);
    							}else{
    								JOptionPane.showMessageDialog(panel, "ERREUR, votre message n'a pas été envoyé","ERREUR, message non envoyé",JOptionPane.ERROR_MESSAGE);
    							}
    						}
    					}
    				}
                }
			}
		});
		btnEnvoyer.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnEnvoyer.setBounds(656, 592, 117, 25);
		panel.add(btnEnvoyer);
		
		final JButton btnModifTitre = new JButton("Editer");
		btnModifTitre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* click sur le bouton éditer */
				if(modifTitre == false){
					/* on va modifier le titre */
					saiTitre.setEditable(true); /* on rend le champs de saisie du titre éditable */
					saiTitre.requestFocus(); /* on focus le champs de saisie du titre */
					btnModifTitre.setText("Valider"); /* on modifie le titre du bouton */
					modifTitre = true;
				}else{
					/* on a modifié le titre */
					saiTitre.setEditable(false);
					btnModifTitre.setText("Editer"); /* on modifie le titre du bouton */
					modifTitre = false;
					/* on vérifie que le titre ne contient pas de / ou de # */
					if(saiTitre.getText().contains("#") || saiTitre.getText().contains("/")){
						/* on affiche un mesg d'erreur */
						JOptionPane.showMessageDialog(panel, "ERREUR, le titre ne peut pas contenir de # ou de /","ERREUR, titre incorrect",JOptionPane.ERROR_MESSAGE);
					}else{
						/* on envoie la requete au serveur */
						envoyerMesg("6");
						if(Integer.parseInt(lireMesg()) == 6){
							/* le serveur a répondu */
							envoyerMesg("1"); /* on envoi une demande de modification de titre */
							if(Integer.parseInt(lireMesg()) == 1){
								/* le serveur valide la demande, on lui envoi les infos idCanal#titre */
								envoyerMesg(String.valueOf(canal.getId())+"#"+saiTitre.getText());
								if(Integer.parseInt(lireMesg()) == 1){
									/* la modification a été effectuée avec succès */
									actualiser(txtTchat); /* on actualise */
									JOptionPane.showMessageDialog(panel, "Le titre du canal a bien été modifié","Titre modifié",JOptionPane.INFORMATION_MESSAGE);
								}else{
									JOptionPane.showMessageDialog(panel, "ERREUR, le titre du canal n'a pas été modifié","ERREUR, titre non modifié",JOptionPane.ERROR_MESSAGE);
								}
							}else{
								JOptionPane.showMessageDialog(panel, "ERREUR, le titre du canal n'a pas été modifié","ERREUR, titre non modifié",JOptionPane.ERROR_MESSAGE);
							}
						}else{
							JOptionPane.showMessageDialog(panel, "ERREUR, le titre du canal n'a pas été modifié","ERREUR, titre non modifié",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		btnModifTitre.setFont(new Font("Liberation Serif", Font.BOLD, 12));
		btnModifTitre.setBounds(779, 7, 73, 17);
		panel.add(btnModifTitre);
		
		saiTitre = new JTextField();
		saiTitre.setFont(new Font("Liberation Serif", Font.PLAIN, 17));
		saiTitre.setEditable(false);
		saiTitre.setBackground(UIManager.getColor("CheckBox.background"));
		saiTitre.setBounds(293, 0, 477, 27);
		panel.add(saiTitre);
		saiTitre.setColumns(10);
		saiTitre.setText(this.canal.getTitre());
		
		/* Fenêtre interne pour modifier le grade des utilisateurs */
		final JInternalFrame intFenModifGrade = new JInternalFrame("Modifier le grade des utilisateurs du canal");
		intFenModifGrade.setBounds(12, 81, 998, 493);
		intFenModifGrade.setOpaque(false);
		panel.add(intFenModifGrade);
		final JPanel panel_1 = new JPanel();
		intFenModifGrade.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		
		/* Par défaut le tableau est vide */
		final List<Object> donneesInit = new ArrayList<Object>();
		
	   
	   /* On prépare le type des colones */
	   Class[] typeColones = {String.class, String.class, String.class, Boolean.class, Boolean.class};
	   
	   
	   /* Modèle pour notre tableau */
	   class ModeleTableau extends AbstractTableModel{
			/**
			 * 
			 */
		   private static final long serialVersionUID = 1L;
			private List<Object> donnees;
			private String[] titreCol = new String[]{"Id","Nom","Prénom", "Modérateur", "Banni"};
			private Class[] classCol;
	 
			public ModeleTableau(List<Object> donneesInit, Class[] classCol){
				super();
				this.classCol = classCol;
				this.donnees = donneesInit;
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
				if(arg1 > 2){
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
		
		/* bouton permettant de modifier le grade des utilisateur sur le canal */
		final JButton btnModifUtil = new JButton("Grades");
		btnModifUtil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* on demande la liste des membres connectés sur le canal au serveur avec leur grade */
				listeInvite = ""; /* on vide la liste des invités */
				/* On demande au serveur la liste des utilisateurs */
				envoyerMesg("10");
				if(Integer.parseInt(lireMesg()) == 10){
					envoyerMesg(String.valueOf(canal.getId()));
					String listeUtils = lireMesg();
					System.out.println(listeUtils);
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
						if(Integer.parseInt(listeUtilsDecomp[i].split("#")[3]) == 0){
							/* Pas modérateur */
							if(Integer.parseInt(listeUtilsDecomp[i].split("#")[4]) == 0){
								/* Pas banni */
								Object[] donnees = {listeUtilsDecomp[i].split("#")[0],listeUtilsDecomp[i].split("#")[1],listeUtilsDecomp[i].split("#")[2], new Boolean(false), new Boolean(false)};
								modele.addRow(donnees);
							}else{
								/* Banni */
								Object[] donnees = {listeUtilsDecomp[i].split("#")[0],listeUtilsDecomp[i].split("#")[1],listeUtilsDecomp[i].split("#")[2], new Boolean(false), new Boolean(true)};
								modele.addRow(donnees);
							}
						}else{
							/* Modérateur */
							if(Integer.parseInt(listeUtilsDecomp[i].split("#")[4]) == 0){
								/* Pas banni */
								Object[] donnees = {listeUtilsDecomp[i].split("#")[0],listeUtilsDecomp[i].split("#")[1],listeUtilsDecomp[i].split("#")[2], new Boolean(true), new Boolean(false)};
								modele.addRow(donnees);
							}else{
								/* Banni */
								Object[] donnees = {listeUtilsDecomp[i].split("#")[0],listeUtilsDecomp[i].split("#")[1],listeUtilsDecomp[i].split("#")[2], new Boolean(true), new Boolean(true)};
								modele.addRow(donnees);
							}
						}
						
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
						   intFenModifGrade.getContentPane().add(scrollPane, BorderLayout.NORTH);
						   scrollPane.setViewportView(tableau);
						   intFenModifGrade.setVisible(true);
						   scrollPaneMesg.setVisible(false);
						   listListeUsers.setVisible(false);
						   btnModifUtil.setVisible(false);
						   JButton btnTermine = new JButton("Termine");
						   btnTermine.addActionListener(new ActionListener() {
						   	public void actionPerformed(ActionEvent arg0) {
						   		/* On parcours chaque ligne du tableau*/
						   		int j; /* indice de parcours des lignes du tableau */
						   		for(j=0;j<tableau.getRowCount();j++){
						   			/* on regarde la valeur de la colone Modérateur */
						   			if(tableau.getValueAt(j, 3).equals(true)){
						   				/* L'utilisateur est coché comme modérateur */
						   				/* On regarde si banni est coché */
						   				if(tableau.getValueAt(j, 4).equals(true)){
						   					/* L'utilisateur est banni aussi */
						   					listeInvite += String.valueOf(tableau.getValueAt(j, 0))+"#1#1/";
						   				}else{
						   					listeInvite += String.valueOf(tableau.getValueAt(j, 0))+"#1#0/";
						   				}	
						   			}else{
						   				/* On regarde si banni est coché */
						   				if(tableau.getValueAt(j, 4).equals(true)){
						   					/* L'utilisateur est banni aussi */
						   					listeInvite += String.valueOf(tableau.getValueAt(j, 0))+"#0#1/";
						   				}else{
						   					listeInvite += String.valueOf(tableau.getValueAt(j, 0))+"#0#0/";
						   				}
						   			}
						   		}
						   		/* On envoi la demande de mise à jour des membres */
						   		envoyerMesg("11");
						   		/* Si le serveur répond favorablement on envoi l'identifiant du canal */
						   		if(Integer.parseInt(lireMesg()) == 11){
						   			/* on envoi l'identifiant du canal */
						   			envoyerMesg(String.valueOf(canal.getId()));
						   			/* On envoi ensuite la liste des infos */
						   			envoyerMesg(listeInvite);
						   			/* On attend la réponse du serveur */
						   			if(Integer.parseInt(lireMesg()) == 1){
						   				/* la modification est ok */
						   				JOptionPane.showMessageDialog(panel, "Mise à jour des grades terminée","Mise à jour des grades",JOptionPane.INFORMATION_MESSAGE);
						   				/* On affiche un message et on relance la page */
						   				try {
											FenCanal fenCanal = new FenCanal(sockConnexion, canal, utilisateur, isModerateur,isPrive);
											fenCanal.ouvrirFenetre();
											fermerFenetre();
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
						   			}else{
						   				JOptionPane.showMessageDialog(panel, "ERREUR, la mise à jour des grades utilisateurs a échouée","ERREUR, mise à jour des grades",JOptionPane.ERROR_MESSAGE);
						   			}
						   		}else{
						   			JOptionPane.showMessageDialog(panel, "ERREUR, la mise à jour des grades utilisateurs a échouée","ERREUR, mise à jour des grades",JOptionPane.ERROR_MESSAGE);
						   		}
						   		
						   	}
						   });
						   btnTermine.setFont(new Font("Liberation Serif", Font.BOLD, 15));
						   btnTermine.setBounds(845, 12, 131, 25);
						   panel_1.add(btnTermine);
				}
			}
		});
		btnModifUtil.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnModifUtil.setBounds(840, 510, 139, 25);
		panel.add(btnModifUtil);
		
		if(isPrive == true){
			/* On affiche le bouton ajouter des invité seulement si il s'agit d'un canal privé */
			JButton btnAddInvites = new JButton("Ajouter Invités");
			btnAddInvites.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					/* On affiche la liste des utilisateurs pour modifier la liste des invités */
				}
			});
			btnAddInvites.setFont(new Font("Liberation Serif", Font.BOLD, 15));
			btnAddInvites.setBounds(840, 549, 139, 25);
			panel.add(btnAddInvites);
		}
		
		/* on affiche le bouton seulement si l'utilisateur est un modérateur du canal */
		if(this.isModerateur == true){
			btnModifTitre.setVisible(true);
			btnModifUtil.setVisible(true);
		}else{
			btnModifTitre.setVisible(false);
			btnModifUtil.setVisible(false);
		}
		
		
		
		/* Actualisation automatique de la zone de Tchat */
		Thread th = new Thread(){
			public void run(){
				while(true){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					actualiser(txtTchat);
				}
			}
		};
		th.start();
	}
}
