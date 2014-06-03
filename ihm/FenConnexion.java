/**
 * Package contenant toutes les fenêtres de l'application
 */
package ihm;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JButton;

import client.Administrateur;
import client.Membre;
import client.Utilisateur;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Fenêtre permettant à un utilisateur qui s'est inscrit sur le site de s'identifier
 * @author STRI
 *
 */
public class FenConnexion {

	/**
	 * Frame principale de l'application
	 */
	private JFrame frmApplication;
	
	/**
	 * Champs de saisie pour le login de l'utilisateur
	 */
	private JTextField saiLogin;
	
	/**
	 * Champs de saisie pour le mot de passe.
	 */
	private JPasswordField saiPass;
	
	/**
	 * Socket de connexion du client.
	 */
	private Socket sockConnexion;
	
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
		this.frmApplication.setVisible(true);
	}
	
	/**
	 * Ferme la fenêtre
	 */
	public void fermerFenetre(){
		this.frmApplication.setVisible(false);
		this.frmApplication.dispose();
	}

	/**
	 * Construit la fenêtre de connexion.
	 * @param sockConnexion Socket de connexion du client.
	 */
	public FenConnexion(Socket sockConnexion) {
		this.sockConnexion = sockConnexion;
		initialize();
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
	 * Initialise la fenêtre de connexion avec tous les composants.
	 */
	private void initialize() {
		frmApplication = new JFrame();
		frmApplication.setTitle("Application Tchat STRI ");
		frmApplication.setResizable(false);
		frmApplication.setBounds(100, 100, 1024, 700);
		frmApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmApplication.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel libTitle = new JLabel("CONNEXION");
		libTitle.setForeground(Color.RED);
		libTitle.setFont(new Font("Liberation Serif", Font.BOLD, 23));
		libTitle.setBounds(436, 39, 149, 35);
		panel.add(libTitle);
		
		final JLabel libInfo = new JLabel("Veuillez saisir votre Login et votre Mot de passe");
		libInfo.setHorizontalAlignment(SwingConstants.CENTER);
		libInfo.setFont(new Font("Liberation Serif", Font.BOLD | Font.ITALIC, 15));
		libInfo.setBounds(12, 144, 998, 18);
		panel.add(libInfo);
		
		JLabel libLogin = new JLabel("Votre Login :");
		libLogin.setHorizontalAlignment(SwingConstants.LEFT);
		libLogin.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libLogin.setBounds(311, 251, 103, 21);
		panel.add(libLogin);
		
		JLabel libPass = new JLabel("Votre Mot de passe :");
		libPass.setHorizontalAlignment(SwingConstants.LEFT);
		libPass.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libPass.setBounds(311, 350, 158, 21);
		panel.add(libPass);
		
		saiLogin = new JTextField();
		saiLogin.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				 int code = e.getKeyCode ();
                 if (code == KeyEvent.VK_ENTER){
                	 envoyerMesg("0");
                	 Boolean erreur = false; /* Variable pour la présence d'erreurs */
     				/* On récupère la valeur des deux champs */
     				String login = saiLogin.getText();
     				String pass = new String(saiPass.getPassword());
     				/* On vérifie que l'utilisateur a bien rempli tous les champs */
     				if(login.compareTo("") == 0 || pass.compareTo("") == 0){
    					erreur = true;
    					libInfo.setText("ERREUR : Vous devez saisir votre Login ET votre Mot de passe");
    				}else{
	    				/* Si tous les champs sont bien remplis on envoie la demande de connexion au serveur : chaîne de forme login/password */
	    				envoyerMesg(login+"/"+pass);
	    				if(erreur == false){
	    					/* On attend la réponse du serveur 0 : : login ou mdp faux ou 1 : ok */
	    					int codeServ = Integer.parseInt(lireMesg());
	    					System.out.println("Code serv = "+String.valueOf(codeServ));
	    					if(codeServ == 0){
	    						/* Erreur de login ou de Mdp */
	    						libInfo.setText("ERREUR : Votre login ou votre mot de passe est incorrect");
	    					}else{
	    						if(codeServ == 2){
	    							libInfo.setText("ERREUR : Vous avez été banni de l'application");
	    						}else{
	    							/* on récupère les infos de l'utilisateur */
		    						String infosUtil = lireMesg();
		    						/* On ouvre la fenêtre Principale avec les infos utilisateurs et le socket en paramètres */
		    						Utilisateur utilisateur = null;
		    						if(Integer.parseInt(infosUtil.split("/")[5]) == 2){
		    							utilisateur = new Administrateur(Integer.parseInt(infosUtil.split("/")[0]), infosUtil.split("/")[1], infosUtil.split("/")[2], infosUtil.split("/")[3], infosUtil.split("/")[4], Integer.parseInt(infosUtil.split("/")[5]));
		    						}else{
		    							utilisateur = new Membre(Integer.parseInt(infosUtil.split("/")[0]), infosUtil.split("/")[1], infosUtil.split("/")[2], infosUtil.split("/")[3], infosUtil.split("/")[4], Integer.parseInt(infosUtil.split("/")[5]));
		    						}
		    						FenAccueil fenAccueil = new FenAccueil(sockConnexion, utilisateur);
		    						fenAccueil.ouvrirFenetre();
		    						fermerFenetre();
	    						}
	    						
	    						
	    					}
	    				}
    				}
                 }
			}
		});
		saiLogin.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		saiLogin.setBounds(487, 253, 277, 19);
		panel.add(saiLogin);
		saiLogin.setColumns(10);
		
		saiPass = new JPasswordField();
		saiPass.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				 int code = e.getKeyCode ();
                 if (code == KeyEvent.VK_ENTER){
                	 envoyerMesg("0");
                	 Boolean erreur = false; /* Variable pour la présence d'erreurs */
     				/* On récupère la valeur des deux champs */
     				String login = saiLogin.getText();
     				String pass = new String(saiPass.getPassword());
     				/* On vérifie que l'utilisateur a bien rempli tous les champs */
     				if(login.compareTo("") == 0 || pass.compareTo("") == 0){
    					erreur = true;
    					libInfo.setText("ERREUR : Vous devez saisir votre Login ET votre Mot de passe");
    				}else{
	    				/* Si tous les champs sont bien remplis on envoie la demande de connexion au serveur : chaîne de forme login/password */
	    				envoyerMesg(login+"/"+pass);
	    				if(erreur == false){
	    					/* On attend la réponse du serveur 0 : : login ou mdp faux ou 1 : ok */
	    					int codeServ = Integer.parseInt(lireMesg());
	    					System.out.println("Code serv = "+String.valueOf(codeServ));
	    					if(codeServ == 0){
	    						/* Erreur de login ou de Mdp */
	    						libInfo.setText("ERREUR : Votre login ou votre mot de passe est incorrect");
	    					}else{
	    						if(codeServ == 2){
	    							libInfo.setText("ERREUR : Vous avez été banni de l'application");
	    						}else{
	    							/* on récupère les infos de l'utilisateur */
		    						String infosUtil = lireMesg();
		    						/* On ouvre la fenêtre Principale avec les infos utilisateurs et le socket en paramètres */
		    						Utilisateur utilisateur = null;
		    						if(Integer.parseInt(infosUtil.split("/")[5]) == 2){
		    							utilisateur = new Administrateur(Integer.parseInt(infosUtil.split("/")[0]), infosUtil.split("/")[1], infosUtil.split("/")[2], infosUtil.split("/")[3], infosUtil.split("/")[4], Integer.parseInt(infosUtil.split("/")[5]));
		    						}else{
		    							utilisateur = new Membre(Integer.parseInt(infosUtil.split("/")[0]), infosUtil.split("/")[1], infosUtil.split("/")[2], infosUtil.split("/")[3], infosUtil.split("/")[4], Integer.parseInt(infosUtil.split("/")[5]));
		    						}
		    						FenAccueil fenAccueil = new FenAccueil(sockConnexion, utilisateur);
		    						fenAccueil.ouvrirFenetre();
		    						fermerFenetre();
	    						}
	    						
	    						
	    					}
	    				}
    				}
                 }
			}
		});
		saiPass.setBounds(487, 352, 277, 19);
		panel.add(saiPass);
		
		JButton btnValider = new JButton("Valider");
		btnValider.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				 int code = e.getKeyCode ();
                 if (code == KeyEvent.VK_ENTER){
                	 envoyerMesg("0");
                	 Boolean erreur = false; /* Variable pour la présence d'erreurs */
     				/* On récupère la valeur des deux champs */
     				String login = saiLogin.getText();
     				String pass = new String(saiPass.getPassword());
     				/* On vérifie que l'utilisateur a bien rempli tous les champs */
     				if(login.compareTo("") == 0 || pass.compareTo("") == 0){
    					erreur = true;
    					libInfo.setText("ERREUR : Vous devez saisir votre Login ET votre Mot de passe");
    				}else{
	    				/* Si tous les champs sont bien remplis on envoie la demande de connexion au serveur : chaîne de forme login/password */
	    				envoyerMesg(login+"/"+pass);
	    				if(erreur == false){
	    					/* On attend la réponse du serveur 0 : : login ou mdp faux ou 1 : ok */
	    					int codeServ = Integer.parseInt(lireMesg());
	    					System.out.println("Code serv = "+String.valueOf(codeServ));
	    					if(codeServ == 0){
	    						/* Erreur de login ou de Mdp */
	    						libInfo.setText("ERREUR : Votre login ou votre mot de passe est incorrect");
	    					}else{
	    						if(codeServ == 2){
	    							libInfo.setText("ERREUR : Vous avez été banni de l'application");
	    						}else{
	    							/* on récupère les infos de l'utilisateur */
		    						String infosUtil = lireMesg();
		    						/* On ouvre la fenêtre Principale avec les infos utilisateurs et le socket en paramètres */
		    						Utilisateur utilisateur = null;
		    						if(Integer.parseInt(infosUtil.split("/")[5]) == 2){
		    							utilisateur = new Administrateur(Integer.parseInt(infosUtil.split("/")[0]), infosUtil.split("/")[1], infosUtil.split("/")[2], infosUtil.split("/")[3], infosUtil.split("/")[4], Integer.parseInt(infosUtil.split("/")[5]));
		    						}else{
		    							utilisateur = new Membre(Integer.parseInt(infosUtil.split("/")[0]), infosUtil.split("/")[1], infosUtil.split("/")[2], infosUtil.split("/")[3], infosUtil.split("/")[4], Integer.parseInt(infosUtil.split("/")[5]));
		    						}
		    						FenAccueil fenAccueil = new FenAccueil(sockConnexion, utilisateur);
		    						fenAccueil.ouvrirFenetre();
		    						fermerFenetre();
	    						}
	    						
	    						
	    					}
	    				}
    				}
                 }
			}
		});
		/* Click sur le bouton Valider */
		btnValider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				envoyerMesg("0");
           	 Boolean erreur = false; /* Variable pour la présence d'erreurs */
				/* On récupère la valeur des deux champs */
				String login = saiLogin.getText();
				String pass = new String(saiPass.getPassword());
				/* On vérifie que l'utilisateur a bien rempli tous les champs */
				if(login.compareTo("") == 0 || pass.compareTo("") == 0){
					erreur = true;
					libInfo.setText("ERREUR : Vous devez saisir votre Login ET votre Mot de passe");
				}else{
   				/* Si tous les champs sont bien remplis on envoie la demande de connexion au serveur : chaîne de forme login/password */
   				envoyerMesg(login+"/"+pass);
	   				if(erreur == false){
	   					/* On attend la réponse du serveur 0 : : login ou mdp faux ou 1 : ok */
	   					int codeServ = Integer.parseInt(lireMesg());
	   					System.out.println("Code serv = "+String.valueOf(codeServ));
	   					if(codeServ == 0){
	   						/* Erreur de login ou de Mdp */
	   						libInfo.setText("ERREUR : Votre login ou votre mot de passe est incorrect");
	   					}else{
	   						if(codeServ == 2){
	   							libInfo.setText("ERREUR : Vous avez été banni de l'application");
	   						}else{
	   								/* on récupère les infos de l'utilisateur */
		    						String infosUtil = lireMesg();
		    						/* On ouvre la fenêtre Principale avec les infos utilisateurs et le socket en paramètres */
		    						Utilisateur utilisateur = null;
		    						if(Integer.parseInt(infosUtil.split("/")[5]) == 2){
		    							utilisateur = new Administrateur(Integer.parseInt(infosUtil.split("/")[0]), infosUtil.split("/")[1], infosUtil.split("/")[2], infosUtil.split("/")[3], infosUtil.split("/")[4], Integer.parseInt(infosUtil.split("/")[5]));
		    						}else{
		    							utilisateur = new Membre(Integer.parseInt(infosUtil.split("/")[0]), infosUtil.split("/")[1], infosUtil.split("/")[2], infosUtil.split("/")[3], infosUtil.split("/")[4], Integer.parseInt(infosUtil.split("/")[5]));
		    						}
		    						FenAccueil fenAccueil = new FenAccueil(sockConnexion, utilisateur);
		    						fenAccueil.ouvrirFenetre();
		    						fermerFenetre();
	   						}
	   						
	   						
	   					}
	   				}
				}
			}
		});
		btnValider.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnValider.setBounds(523, 426, 117, 25);
		panel.add(btnValider);
		
		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode ();
                if (code == KeyEvent.VK_ENTER){
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
			}
		});
		/* Click sur le bouton Annuler */
		btnAnnuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		btnAnnuler.setBounds(647, 426, 117, 25);
		panel.add(btnAnnuler);
		
		JButton btnVisiteur = new JButton("Se connecter en tant que Visiteur");
		btnVisiteur.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* click sur le bouton "se connecter en tant que visiteur" */
				FenConnexionVisiteur fenConnexion = new FenConnexionVisiteur(sockConnexion);
				fenConnexion.ouvrirFenetre();
				fermerFenetre();
			}
		});
		btnVisiteur.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnVisiteur.setBounds(372, 549, 277, 25);
		panel.add(btnVisiteur);
	}
}
