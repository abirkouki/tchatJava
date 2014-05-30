package ihm;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import client.Administrateur;
import client.Membre;
import client.Utilisateur;

import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import serveur.Canal;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JInternalFrame;
import javax.swing.JList;

public class FenAccueil {

	/**
	 * Frame principale de la fenêtre
	 */
	private JFrame frmApplicationTchatStri;
	
	/**
	 * Socket de connexion du client.
	 */
	private Socket sockConnexion;
	
	/**
	 * Utilisateur courrant de l'application
	 */
	private Utilisateur utilisateur;
	
	/**
	 * Champs de saisie permettant de donner une justification à un statut d'absence
	 */
	private JTextField saiJustification;
	
	/**
	 * Buffer permettant de lire les messages du serveur
	 */
	private BufferedReader lire = null;
	
	/**
	 * Bufer d'écriture pour envoyer des messages au serveur
	 */
	private PrintWriter ecrire;
	
	/**
	 * Fenêtre interne permettant de rejoindre les canaux.
	 */
	private final JInternalFrame intFenRejCanal = new JInternalFrame("Rejoindre un canal");
	
	/**
	 * Ouvre la fenêtre.
	 */
	public void ouvrirFenetre(){
		this.frmApplicationTchatStri.setVisible(true);
	}
	

	/**
	 * Construit une fenêtre d'accueil et affecte les valeurs aux attributs.
	 * @param sockConnexion Socket de connexion du client
	 * @param infosUtil Informations permettant de créer un utilisateur correspondant au client.
	 */
	public FenAccueil(Socket sockConnexion, String infosUtil) {
		this.sockConnexion = sockConnexion;
		String[] infosDecomp = infosUtil.split("/");
		if(Integer.parseInt(infosDecomp[5]) == 2){
			/* C'est un Administrateur */
			this.utilisateur = new Administrateur(Integer.parseInt(infosDecomp[0]), infosDecomp[1], infosDecomp[2],infosDecomp[3],infosDecomp[4], Integer.parseInt(infosDecomp[5]));
		}else{
			/* C'est un Membre */
			this.utilisateur = new Membre(Integer.parseInt(infosDecomp[0]), infosDecomp[1], infosDecomp[2],infosDecomp[3],infosDecomp[4], Integer.parseInt(infosDecomp[5]));
		}
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
	 * Initialise la fenêtre.
	 */
	private void initialize() {
		frmApplicationTchatStri = new JFrame();
		frmApplicationTchatStri.setTitle("Application Tchat STRI");
		frmApplicationTchatStri.setResizable(false);
		frmApplicationTchatStri.setBounds(100, 100, 1024, 700);
		frmApplicationTchatStri.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel panel = new JPanel();
		frmApplicationTchatStri.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		String statutActuel =  "En ligne";
		final JLabel libBienvenue = new JLabel("Bienvenue "+this.utilisateur.getNom()+" "+this.utilisateur.getPrenom()+" ("+statutActuel+")");
		libBienvenue.setFont(new Font("Liberation Serif", Font.BOLD, 17));
		libBienvenue.setBounds(12, 12, 729, 27);
		panel.add(libBienvenue);
		
		saiJustification = new JTextField();
		saiJustification.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		saiJustification.setColumns(10);
		saiJustification.setBounds(769, 84, 241, 21);
		panel.add(saiJustification);
		final JLabel libJustification = new JLabel("Justification :");
		libJustification.setFont(new Font("Liberation Serif", Font.BOLD, 17));
		libJustification.setBounds(769, 51, 126, 27);
		panel.add(libJustification);
		final JButton btnJustification = new JButton("Ok");
		btnJustification.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				utilisateur.setJustification(saiJustification.getText());
				libBienvenue.setText("Bienvenue "+utilisateur.getNom()+" "+utilisateur.getPrenom()+" (Abscent : "+utilisateur.getJustification()+")");
				/* Requete de modification de statut */
				envoyerMesg("1"); /* on envoie la demande au serveur */
				/* On attend la réponse du serveur qui doit être identique à notre requete */
				if(Integer.parseInt(lireMesg())==1){
					/* Le serveur a répondu favorable, on lui envoi les infos idUtil/statut/justif */
					envoyerMesg(String.valueOf(utilisateur.getId())+"/"+String.valueOf(utilisateur.getStatut())+"/"+utilisateur.getJustification());
					/* On attend sa réponse */
					if(Integer.parseInt(lireMesg())==1){
						/* Reponse favorable */
						JOptionPane.showMessageDialog(panel, "Votre modification de statut a bien été prise en compte","Modification statut",JOptionPane.INFORMATION_MESSAGE);
					}else{
						/* réponse défavorable */
						JOptionPane.showMessageDialog(panel, "Votre modification de statut a échouée, merci de réessayer ultérieurement","Erreur modification statut",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		btnJustification.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnJustification.setBounds(937, 117, 73, 25);
		panel.add(btnJustification);
		/* Par défaut la justification est invisible */
		saiJustification.setVisible(false);
		btnJustification.setVisible(false);
		libJustification.setVisible(false);
		
		final JComboBox comboStatut = new JComboBox();
		comboStatut.setFont(new Font("Liberation Serif", Font.PLAIN, 17));
		comboStatut.setBounds(843, 14, 167, 27);
		comboStatut.addItem("En ligne");
		comboStatut.addItem("Occupé");
		comboStatut.addItem("Abscent");
		class ListenerCombo implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				String statutSelect = comboStatut.getSelectedItem().toString();
				if(statutSelect.compareTo("En ligne") == 0){
					utilisateur.setStatut(0);
					utilisateur.setJustification("");
					saiJustification.setVisible(false);
					btnJustification.setVisible(false);
					libJustification.setVisible(false);
					libBienvenue.setText("Bienvenue "+utilisateur.getNom()+" "+utilisateur.getPrenom()+" ("+statutSelect+")");
					/* Requete de modification de statut */
					envoyerMesg("1"); /* on envoie la demande au serveur */
					/* On attend la réponse du serveur qui doit être identique à notre requete */
					if(Integer.parseInt(lireMesg())==1){
						/* Le serveur a répondu favorable, on lui envoi les infos idUtil/statut/justif */
						envoyerMesg(String.valueOf(utilisateur.getId())+"/"+String.valueOf(utilisateur.getStatut())+"/"+" ");
						/* On attend sa réponse */
						if(Integer.parseInt(lireMesg())==1){
							/* Reponse favorable */
							JOptionPane.showMessageDialog(panel, "Votre modification de statut a bien été prise en compte","Modification statut",JOptionPane.INFORMATION_MESSAGE);
						}else{
							/* réponse défavorable */
							JOptionPane.showMessageDialog(panel, "Votre modification de statut a échouée, merci de réessayer ultérieurement","Erreur modification statut",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				if(statutSelect.compareTo("Occupé") == 0){
					utilisateur.setStatut(1);
					utilisateur.setJustification("");
					saiJustification.setVisible(false);
					btnJustification.setVisible(false);
					libJustification.setVisible(false);
					libBienvenue.setText("Bienvenue "+utilisateur.getNom()+" "+utilisateur.getPrenom()+" ("+statutSelect+")");
					/* Requete de modification de statut */
					envoyerMesg("1"); /* on envoie la demande au serveur */
					/* On attend la réponse du serveur qui doit être identique à notre requete */
					if(Integer.parseInt(lireMesg())==1){
						/* Le serveur a répondu favorable, on lui envoi les infos idUtil/statut/justif */
						envoyerMesg(String.valueOf(utilisateur.getId())+"/"+String.valueOf(utilisateur.getStatut())+"/"+" ");
						/* On attend sa réponse */
						if(Integer.parseInt(lireMesg())==1){
							/* Reponse favorable */
							JOptionPane.showMessageDialog(panel, "Votre modification de statut a bien été prise en compte","Modification statut",JOptionPane.INFORMATION_MESSAGE);
						}else{
							/* réponse défavorable */
							JOptionPane.showMessageDialog(panel, "Votre modification de statut a échouée, merci de réessayer ultérieurement","Erreur modification statut",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				if(statutSelect.compareTo("Abscent") == 0){
					utilisateur.setStatut(2);
					saiJustification.setVisible(true);
					btnJustification.setVisible(true);
					libJustification.setVisible(true);
				}
			}
		}
		comboStatut.addActionListener(new ListenerCombo());
		panel.add(comboStatut);
		
		JLabel libStatut = new JLabel("Statut :");
		libStatut.setFont(new Font("Liberation Serif", Font.BOLD, 17));
		libStatut.setBounds(769, 12, 64, 27);
		panel.add(libStatut);
		
		intFenRejCanal.setBounds(314, 252, 436, 252);
		panel.add(intFenRejCanal);
		
		final JPanel panel_1 = new JPanel();
		intFenRejCanal.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		final JButton btnAdmin = new JButton("Administration");
		btnAdmin.setFont(new Font("Liberation Serif", Font.BOLD, 25));
		btnAdmin.setBounds(373, 460, 275, 35);
		btnAdmin.setVisible(false);
		panel.add(btnAdmin);
		/* Si l'utilisateur est administrateur on lui ajoute le bouton d'administration */
		if(this.utilisateur.getGrade() == 2){
			btnAdmin.setVisible(true);
		}
		final JButton btnCompte = new JButton("Gestion du compte");
		final JButton btnCreer = new JButton("Créer un canal");
		final DefaultListModel listCanauxModele = new DefaultListModel();
		
			
		final JButton btnRejoindre = new JButton("Rejoindre un canal");
		btnRejoindre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/*Canal canal = new Canal(0, "Par défaut", null);
				FenCanal fen;
				try {
					fen = new FenCanal(sockConnexion,canal, utilisateur);
					fen.ouvrirFenetre();
					fermerFenetre();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				/* On cache les boutons pour corriger un beug graphique */
				btnRejoindre.setVisible(false);
				btnCompte.setVisible(false);
				btnCreer.setVisible(false);
				btnAdmin.setVisible(false);
				/* On envoie la requete au serveur */
				envoyerMesg("2");
				/* On regarde que le serveur atteste bonne réception de la requete */
				if(Integer.parseInt(lireMesg()) == 2){
					/* On envoie l'identifiant de l'utilisateur */
					envoyerMesg(String.valueOf(utilisateur.getId()));
					/* On attend la liste des canaux que l'on va décomposée et stockée dans une Map composé de l'identifiant du canal et de son nom */
					final Map<Integer, String> canauxDispo = new HashMap<Integer, String>();
					int i; /* indice de parcours de la liste */
					String listeCanaux;
					String[] listeCanauxDecomp;
					int nbCanaux;
					listeCanaux = lireMesg(); /* idCanal#nomCanal/idCanal#nomCanal/... */
					listeCanauxDecomp = listeCanaux.split("/");
					nbCanaux = listeCanauxDecomp.length;
					/* On ajoute les canaux dans la Map */
					for(i=0;i<nbCanaux;i++){
						canauxDispo.put(Integer.parseInt(listeCanauxDecomp[i].split("#")[0]), listeCanauxDecomp[i].split("#")[1]);
						/* Au passage on les ajoute a la JList */
						listCanauxModele.addElement(listeCanauxDecomp[i].split("#")[1]);
					}
					/* On fabrique la liste */
					final JList listCanaux = new JList();
					listCanaux.setModel(listCanauxModele);
					listCanaux.setBounds(12, 12, 402, 145);
					
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setBounds(12, 12, 402, 145);
					panel_1.add(scrollPane);
					scrollPane.setViewportView(listCanaux);
					
					JButton btnRejoinde2 = new JButton("Rejoinde");
					btnRejoinde2.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							/* On demande au serveur de rejoindre le canal sélectionné */
							envoyerMesg("8");
							if(Integer.parseInt(lireMesg()) == 8){
								int index = listCanaux.getSelectedIndex();
								/* on vérifie que l'utilisateur a bien selectionné un canal */
								if(index < 0){
									JOptionPane.showMessageDialog(panel, "ERREUR, vous n'avez pas sélectionné de canal","Erreur pas de canal sélectionné",JOptionPane.ERROR_MESSAGE);
								}else{
									/* on envoie au serveur que l'on souhaite rejoindre le canal en lui envoyant l'identifiant du canal */
									envoyerMesg(String.valueOf(utilisateur.getId())+"#"+String.valueOf(index));
									/* On attend la réponse du serveur 0 : erreur / 1 : ok utilisateur / 2 : ok modérateur */
									int rep = Integer.parseInt(lireMesg());
									System.out.println("rep ="+rep);
									if(rep == 0){
										/* erreur */
										JOptionPane.showMessageDialog(panel, "ERREUR, impossible de rejoindre le canal","Erreur impossible de rejoindre le canal",JOptionPane.ERROR_MESSAGE);
									}else{
										/* Si c'est ok pour une connexion on récup les infos du canal */
										String infosCanal = lireMesg();
										System.out.println("infos canl = "+infosCanal);
										if(rep == 1){
											/* ok utilisateur */
											Canal canal = new Canal(Integer.parseInt(infosCanal.split("/")[0]), infosCanal.split("/")[1], null);
											try {
												FenCanal fenCanal = new FenCanal(sockConnexion, canal, utilisateur, false);
												fenCanal.ouvrirFenetre();
												fermerFenetre();
											} catch (InterruptedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}else{
											/* ok modérateur */
											Canal canal = new Canal(Integer.parseInt(infosCanal.split("/")[0]), infosCanal.split("/")[1], null);
											try {
												FenCanal fenCanal = new FenCanal(sockConnexion, canal, utilisateur, true);
												fenCanal.ouvrirFenetre();
												fermerFenetre();
											} catch (InterruptedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
								}
							}
						}
					});
					btnRejoinde2.setFont(new Font("Liberation Serif", Font.BOLD, 15));
					btnRejoinde2.setBounds(313, 183, 101, 25);
					panel_1.add(btnRejoinde2);
					
					JButton btnAnnuler2 = new JButton("Annuler");
					btnAnnuler2.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							/* on ferme la internaFrame et on réaffiche les boutons */
							intFenRejCanal.setVisible(false);
							listCanauxModele.removeAllElements(); /* on vide la liste */
							btnRejoindre.setVisible(true);
							btnCompte.setVisible(true);
							btnCreer.setVisible(true);
							if(utilisateur.getGrade()==2){
								btnAdmin.setVisible(true);
							}
						}
					});
					btnAnnuler2.setFont(new Font("Liberation Serif", Font.BOLD, 15));
					btnAnnuler2.setBounds(200, 183, 101, 25);
					panel_1.add(btnAnnuler2);
					
					/* On affiche ensuite la fenêtre interne */
					intFenRejCanal.setVisible(true);
					
					/* Gérer la partie sélection dans la liste + click que le bouton rejoindre */
				}else{
					/* Le serveur n'a pas attesté la bonne réception de la requête */
					JOptionPane.showMessageDialog(panel, "ERREUR, votre requête n'a pas été traitée par le serveur","Erreur requête non traitée",JOptionPane.ERROR_MESSAGE);
				}
				
				
				
			}
		});
		btnRejoindre.setFont(new Font("Liberation Serif", Font.BOLD, 25));
		btnRejoindre.setBounds(373, 264, 275, 35);
		panel.add(btnRejoindre);
		
		
		btnCreer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FenCreationCanal fenCreCan = new FenCreationCanal(sockConnexion, utilisateur);
				fenCreCan.ouvrirFenetre();
				fermerFenetre();
			}
		});
		btnCreer.setFont(new Font("Liberation Serif", Font.BOLD, 25));
		btnCreer.setBounds(373, 311, 275, 35);
		panel.add(btnCreer);
		
		
		btnCompte.setFont(new Font("Liberation Serif", Font.BOLD, 25));
		btnCompte.setBounds(373, 413, 275, 35);
		panel.add(btnCompte);
		
		
		
		JButton btnDeconnexion = new JButton("Déconnexion");
		btnDeconnexion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FenMenuPrincipal fenMenu = new FenMenuPrincipal();
				fenMenu.ouvrirFenetre();
				/* On ferme la connexion et la fenêtre d'inscription */
				try{
					sockConnexion.close();
				}
				catch(IOException ex){
					ex.printStackTrace();
				}
				fermerFenetre();
			}
		});
		btnDeconnexion.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnDeconnexion.setBounds(871, 640, 139, 25);
		panel.add(btnDeconnexion);
		
		JComboBox comboCanauxActifs = new JComboBox();
		comboCanauxActifs.setFont(new Font("Liberation Serif", Font.PLAIN, 17));
		comboCanauxActifs.setBounds(12, 123, 275, 24);
		panel.add(comboCanauxActifs);
		
		JLabel libListeActifs = new JLabel("Liste des canaux actifs :");
		libListeActifs.setFont(new Font("Liberation Serif", Font.BOLD, 17));
		libListeActifs.setBounds(12, 84, 275, 27);
		panel.add(libListeActifs);
		
	}
}
