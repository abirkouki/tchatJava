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
import javax.swing.JTextField;
import javax.swing.JButton;

import serveur.Canal;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JInternalFrame;
import javax.swing.JList;

public class FenAccueil {

	private JFrame frmApplicationTchatStri;
	private Socket sockConnexion;
	private Utilisateur utilisateur;
	private JTextField saiJustification;
	/**
	 * Buffer permettant de lire les messages du serveur
	 */
	private BufferedReader lire = null;
	
	/**
	 * Bufer d'écriture pour envoyer des messages au serveur
	 */
	private PrintWriter ecrire;
	private final JInternalFrame intFenRejCanal = new JInternalFrame("Rejoindre un canal");
	
	/**
	 * Launch the application.
	 */
	public void ouvrirFenetre(){
		this.frmApplicationTchatStri.setVisible(true);
	}
	

	/**
	 * Create the application.
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
	 * Initialize the contents of the frame.
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
		
		intFenRejCanal.setBounds(321, 196, 436, 252);
		panel.add(intFenRejCanal);
		
		final JPanel panel_1 = new JPanel();
		intFenRejCanal.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		
		JButton btnRejoinde = new JButton("Rejoinde");
		btnRejoinde.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnRejoinde.setBounds(313, 183, 101, 25);
		panel_1.add(btnRejoinde);
		
		final DefaultListModel listCanauxModele = new DefaultListModel();
		
			
		JButton btnRejoindre = new JButton("Rejoindre un canal");
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
				/* On envoie la requete au serveur */
				envoyerMesg("2");
				/* On attend la liste des canaux que l'on va décomposée et stockée dans une Map composé de l'identifiant du canal et de son nom */
				Map<Integer, String> canauxDispo = new HashMap<Integer, String>();
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
				JList listCanaux = new JList();
				listCanaux.setModel(listCanauxModele);
				listCanaux.setBounds(12, 12, 402, 145);
				panel_1.add(listCanaux);
				
				/* On affiche ensuite la fenêtre interne */
				intFenRejCanal.setVisible(true);
			}
		});
		btnRejoindre.setFont(new Font("Liberation Serif", Font.BOLD, 25));
		btnRejoindre.setBounds(373, 264, 275, 35);
		panel.add(btnRejoindre);
		
		JButton btnCreer = new JButton("Créer un canal");
		btnCreer.setFont(new Font("Liberation Serif", Font.BOLD, 25));
		btnCreer.setBounds(373, 311, 275, 35);
		panel.add(btnCreer);
		
		JButton btnCompte = new JButton("Gestion du compte");
		btnCompte.setFont(new Font("Liberation Serif", Font.BOLD, 25));
		btnCompte.setBounds(373, 413, 275, 35);
		panel.add(btnCompte);
		
		/* Si l'utilisateur est administrateur on lui ajoute le bouton d'administration */
		if(this.utilisateur.getGrade() == 2){
			JButton btnAdmin = new JButton("Administration");
			btnAdmin.setFont(new Font("Liberation Serif", Font.BOLD, 25));
			btnAdmin.setBounds(373, 460, 275, 35);
			panel.add(btnAdmin);
		}
		
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
