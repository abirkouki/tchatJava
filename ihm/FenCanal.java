package ihm;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JEditorPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JList;
import javax.swing.ListModel;

import java.awt.Color;

import javax.swing.AbstractListModel;
import javax.swing.JTextField;

import java.awt.Font;

import javax.swing.JButton;

import client.Utilisateur;
import serveur.Canal;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JSplitPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
	public FenCanal(Socket sockConnexion, Canal canal, Utilisateur utilisateur) throws InterruptedException {
		this.sockConnexion = sockConnexion;
		this.canal = canal;
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
		 * Fonction permettant d'actualiser la liste des messages
		 * @param txtTchat Zone de texte contenant les messages.
		 */
		public void actualiser(JTextPane txtTchat){
			/* On vérifie que le champs de saisie n'est pas focus et qu'il est vide */
			if(saiMesg.getText().compareTo("")==0){
				/* On envoie une demande d'actualisation au serveur */
				envoyerMesg("5");
				/* On attend la confirmation */
				if(Integer.parseInt(lireMesg()) == 5){
					/* On envoie l'identifiant du canal */
					envoyerMesg(String.valueOf(canal.getId()));
					/* On attend la réponse du serveur avec tout les messages */
					String messages = lireMesg();
					System.out.println("Message du serveur : "+messages);
					String[] messagesDecomp = messages.split("#"); /* on décompose */
					int nbMessages = messagesDecomp.length; /* on récupère le nombre de messages */
					int i; /* indice de parcours */
					messages = ""; /* on vide la variable message */
					for(i=0;i<nbMessages;i++){
						messages += messagesDecomp[i]+newLine;
					}
					/* On ajoute la nouvelle chaine au champs texte */
					this.txtTchat.setText(messages);
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
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 27, 747, 547);
		panel.add(scrollPane);
		this.txtTchat = new JTextPane();
		scrollPane.setViewportView(txtTchat);
		
		this.txtTchat.setEditable(false);
		
		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement("user1");
		listModel.addElement("user2");
		listModel.addElement("user3");
		JList listListeUsers = new JList(listModel);
		listListeUsers.setBackground(Color.LIGHT_GRAY);
		listListeUsers.setBounds(820, 489, 155, -433);
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
		
		JLabel libNomCanal = new JLabel("Vous êtes actuellement sur le canal : "+this.canal.getTitre());
		libNomCanal.setFont(new Font("Liberation Serif", Font.BOLD, 17));
		libNomCanal.setBounds(23, 0, 747, 27);
		panel.add(libNomCanal);
		
		JButton btnQuitter = new JButton("Quitter Canal");
		btnQuitter.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnQuitter.setBounds(858, 592, 139, 25);
		panel.add(btnQuitter);
		
		JButton btnAccueil = new JButton("Retour Accueil");
		btnAccueil.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnAccueil.setBounds(858, 629, 139, 25);
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
