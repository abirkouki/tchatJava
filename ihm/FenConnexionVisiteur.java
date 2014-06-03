/**
 * Package contenant toutes les fenêtres de l'application
 */
package ihm;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;

import serveur.Canal;
import client.Utilisateur;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Fenêtre permettant de réaliser une connexion en tant que visiteur (sans être identifié)
 * @author STRI
 *
 */
public class FenConnexionVisiteur {

	/**
	 * Frame principale de l'application
	 */
	private JFrame frame;
	
	/**
	 * Socket de connexion du client
	 */
	private Socket sockConnexion;
	
	/**
	 * Buffer permettant de lire les messages du serveur
	 */
	private BufferedReader lire = null;
	
	/**
	 * Buffer d'écriture pour envoyer des messages au serveur
	 */
	private PrintWriter ecrire;
	
	/**
	 * Champ de saisie du nom de l'utiliateur
	 */
	private JTextField saiNom;
	
	/**
	 * Champ de saisie du prénom de l'utilisateur
	 */
	private JTextField saiPrenom;

	/**
	 * Ouvre la fenêtre
	 */
	public void ouvrirFenetre(){
		this.frame.setVisible(true);
	}
	
	/**
	 * Ferme la fenêtre
	 */
	public void fermerFenetre(){
		this.frame.setVisible(false);
		this.frame.dispose();
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
			System.out.println("Impossible d'envoyer un message au client");
		}
	}

	/**
	 * Create the application.
	 */
	public FenConnexionVisiteur(Socket sockConnexion) {
		this.sockConnexion = sockConnexion;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Application Tchat STRI ");
		frame.setResizable(false);
		frame.setBounds(100, 100, 1024, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel libTitre = new JLabel("CONNEXION VISITEUR");
		libTitre.setForeground(Color.RED);
		libTitre.setFont(new Font("Liberation Serif", Font.BOLD, 23));
		libTitre.setBounds(381, 40, 260, 35);
		panel.add(libTitre);
		
		final JLabel libInfo = new JLabel("Veuillez saisir votre nom ainsi que votre prénom pour pouvoir vous connecter");
		libInfo.setHorizontalAlignment(SwingConstants.CENTER);
		libInfo.setFont(new Font("Liberation Serif", Font.BOLD | Font.ITALIC, 15));
		libInfo.setBounds(12, 178, 998, 18);
		panel.add(libInfo);
		
		JLabel libNom = new JLabel("Votre nom :");
		libNom.setHorizontalAlignment(SwingConstants.LEFT);
		libNom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libNom.setBounds(311, 285, 103, 21);
		panel.add(libNom);
		
		saiNom = new JTextField();
		saiNom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		saiNom.setColumns(10);
		saiNom.setBounds(487, 287, 277, 19);
		panel.add(saiNom);
		
		JLabel libPrenom = new JLabel("Votre prénom :");
		libPrenom.setHorizontalAlignment(SwingConstants.LEFT);
		libPrenom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libPrenom.setBounds(311, 382, 122, 21);
		panel.add(libPrenom);
		
		saiPrenom = new JTextField();
		saiPrenom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		saiPrenom.setColumns(10);
		saiPrenom.setBounds(487, 384, 277, 19);
		panel.add(saiPrenom);
		
		JButton btnValider = new JButton("Valider");
		btnValider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* On vérifie les champs */
				if(saiNom.getText().compareTo("") == 0 || saiPrenom.getText().compareTo("") == 0){
					libInfo.setText("ERREUR, vous devez saisir votre nom et votre prénom !");
				}else{
					if(saiNom.getText().contains("/") || saiNom.getText().contains("/") || saiPrenom.getText().contains("#") || saiPrenom.getText().contains("/")){
						libInfo.setText("ERREUR, les caractères # et / sont interdits");
					}else{
						envoyerMesg("1");
						/* On crée le canal par défaut */
						Canal canal = new Canal(0, "Par défaut", null);
						Utilisateur utilisateur = new Utilisateur(-1, "visit", saiNom.getText(), saiPrenom.getText(), "visit", 0);
						try {
							FenCanal fenCanal = new FenCanal(sockConnexion, canal, utilisateur, false,false);
							fenCanal.ouvrirFenetre();
							fermerFenetre();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
		});
		btnValider.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnValider.setBounds(523, 494, 117, 25);
		panel.add(btnValider);
		
		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* click sur le bouton annuler */
				FenMenuPrincipal fenMenu = new FenMenuPrincipal();
				fenMenu.ouvrirFenetre();
				/* On ferme la connexion et la fenêtre d'inscription */
				try{
					sockConnexion.close();
				}
				catch(IOException e1){
					e1.printStackTrace();
				}
				fermerFenetre();
			}
		});
		btnAnnuler.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnAnnuler.setBounds(647, 494, 117, 25);
		panel.add(btnAnnuler);
	}
}
