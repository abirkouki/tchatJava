package ihm;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import client.Utilisateur;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JInternalFrame;
import javax.swing.JTextPane;

public class FenInscription {

	private JFrame frmApllicationTchatStri;
	private JTextField saiNom;
	private JTextField saiPrenom;
	private JTextField saiLogin;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private Socket sockConnexion;
	private ArrayList<Utilisateur> listeUtilisateurs;
	/**
	 * Buffer permettant de lire les messages du serveur
	 */
	private BufferedReader lire = null;
	
	/**
	 * Bufer d'écriture pour envoyer des messages au serveur
	 */
	private PrintWriter ecrire;

	/**
	 * Launch the application.
	 */
	public void ouvrirFenetre() {
		this.frmApllicationTchatStri.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public FenInscription(Socket sockConnexion) {
		this.sockConnexion = sockConnexion;
		initialize();
	}
	
	/**
	 * Ferme la fenêtre
	 */
	public void fermerFenetre(){
		this.frmApllicationTchatStri.setVisible(false);
		this.frmApllicationTchatStri.dispose();
	}
	
	/**
	 * Récupère la liste des utilisateurs
	 */
	public void initListeUtilisateurs() {
		this.listeUtilisateurs = new ArrayList<Utilisateur>();
		ObjectInputStream entree;
		try{
			entree = new ObjectInputStream(new FileInputStream("saveUtilisateurs.dat"));
			ArrayList<Utilisateur> readObject = (ArrayList<Utilisateur>) entree.readObject();
			this.listeUtilisateurs = readObject;
			entree.close();
			//System.out.println("Initialisation de la liste des utilisateurs -> OK");
		}
		catch(ClassNotFoundException c)
		{
			System.out.println("ERREUR initialisation de la liste des utilisateurs -> Problème de classe");
			c.printStackTrace();
		}
		catch(FileNotFoundException f)
		{
			System.out.println("ERREUR initialisation de la liste des utilisateurs -> Fichier introuvable");
			f.printStackTrace();
		}
		catch(IOException e)
		{
			System.out.println("ERREUR initialisation de la liste des utilisateurs -> KO");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Récupère un message reçu du serveur
	 * @return Message reçu du serveur.
	 */
	public String lireMesg(){
		try{
			String message;
			/* On initialise le buffer de lecture pour récupérer la confirmation de connexion de la part du serveur */
			lire = new BufferedReader(new InputStreamReader(sockConnexion.getInputStream()));
			/* On récupère le message du serveur */
			message = lire.readLine();
			return message;
		}
		catch (IOException exception){
			return("Impossible de récupérer le message du serveur");
		}
	}
	
	/**
	 * Envoie un message au serveur, puis vide le buffer d'écriture.
	 * @param message Message que le client veut envoyer au serveur
	 */
	public void envoyerMesg(String message){
		try{
			ecrire = new PrintWriter(this.sockConnexion.getOutputStream());
			ecrire.println(message);
			ecrire.flush();
		}
		catch(IOException exception){
			System.out.println("Imposible d'envoyer un message au serveur");
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmApllicationTchatStri = new JFrame();
		frmApllicationTchatStri.setTitle("Apllication Tchat STRI");
		frmApllicationTchatStri.setBounds(100, 100, 1024, 700);
		frmApllicationTchatStri.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmApllicationTchatStri.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		final JInternalFrame popUpConfirmation = new JInternalFrame("Inscription Réussie");
		popUpConfirmation.setBounds(6, 220, 1010, 236);
		panel.add(popUpConfirmation);
		popUpConfirmation.setVisible(false);
		
		JPanel panel_1 = new JPanel();
		popUpConfirmation.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		
		JLabel libPopup1 = new JLabel("Votre inscription a bien été effectuée, vous pouvez maintenant vous connecter à l'aide de votre login et de votre mot de passe.");
		libPopup1.setFont(new Font("Liberation Serif", Font.PLAIN, 17));
		libPopup1.setBounds(67, 33, 865, 42);
		panel_1.add(libPopup1);
		
		JLabel libPopup2 = new JLabel("(Ps : pensez a bien noter vos identifiants de connexion, ils ne sont pas récupérables)");
		libPopup2.setFont(new Font("Liberation Serif", Font.ITALIC, 17));
		libPopup2.setBounds(210, 84, 617, 42);
		panel_1.add(libPopup2);
		
		JButton btnPopupOk = new JButton("OK");
		btnPopupOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* Clic bouton OK de la popup */
				FenMenuPrincipal fenMenu = new FenMenuPrincipal();
				/* On retourne au menu */
				fenMenu.ouvrirFenetre();
				/* On ferme la connexion et la fenêtre d'inscription */
				try{
					sockConnexion.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				/* On ferme la fenêtre d'inscription */
				fermerFenetre();
				
			}
		});
		btnPopupOk.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnPopupOk.setBounds(815, 167, 117, 25);
		panel_1.add(btnPopupOk);
		
		JLabel libTitle = new JLabel("FORMULAIRE D'INSCRIPTION");
		libTitle.setForeground(Color.RED);
		libTitle.setFont(new Font("Liberation Serif", Font.BOLD, 23));
		libTitle.setBounds(329, 35, 363, 35);
		panel.add(libTitle);
		
		final JLabel libErreur = new JLabel("Merci de remplir le formulaire intégralement");
		libErreur.setHorizontalAlignment(SwingConstants.CENTER);
		libErreur.setFont(new Font("Liberation Serif", Font.BOLD | Font.ITALIC, 15));
		libErreur.setBounds(12, 140, 998, 18);
		panel.add(libErreur);
		
		JLabel libNom = new JLabel("Votre Nom :");
		libNom.setHorizontalAlignment(SwingConstants.LEFT);
		libNom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libNom.setBounds(238, 254, 101, 21);
		panel.add(libNom);
		
		JLabel libPrenom = new JLabel("Votre Prénom :");
		libPrenom.setHorizontalAlignment(SwingConstants.LEFT);
		libPrenom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libPrenom.setBounds(238, 317, 124, 21);
		panel.add(libPrenom);
		
		JLabel libLogin = new JLabel("Choisir un Login :");
		libLogin.setHorizontalAlignment(SwingConstants.LEFT);
		libLogin.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libLogin.setBounds(238, 383, 139, 21);
		panel.add(libLogin);
		
		JLabel libPass = new JLabel("Choisir un Mot de passe :");
		libPass.setHorizontalAlignment(SwingConstants.LEFT);
		libPass.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libPass.setBounds(238, 452, 192, 21);
		panel.add(libPass);
		
		JLabel libRepass = new JLabel("Confirmer le Mot de passe :");
		libRepass.setHorizontalAlignment(SwingConstants.LEFT);
		libRepass.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libRepass.setBounds(238, 514, 216, 21);
		panel.add(libRepass);
		
		saiNom = new JTextField();
		saiNom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		saiNom.setBounds(575, 254, 241, 21);
		panel.add(saiNom);
		saiNom.setColumns(10);
		
		saiPrenom = new JTextField();
		saiPrenom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		saiPrenom.setColumns(10);
		saiPrenom.setBounds(575, 317, 241, 21);
		panel.add(saiPrenom);
		
		saiLogin = new JTextField();
		saiLogin.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		saiLogin.setColumns(10);
		saiLogin.setBounds(575, 385, 241, 21);
		panel.add(saiLogin);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(575, 453, 241, 19);
		panel.add(passwordField);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(575, 516, 241, 19);
		panel.add(passwordField_1);
		
		JButton btnConfirm = new JButton("Valider");
		btnConfirm.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				/* On vérifie les infos */
				Boolean erreur = false; /* Présence d'erreurs dans le formulaire */
				/* On récupère la liste des utilisateurs */
				initListeUtilisateurs();
				/* On va en premier lieu vérifier que le login n'est pas déjà utilisé */
				int i; /* indice de parcours de la liste des utilisateurs */
				/* On transforme les password en String */
				String mdp = new String(passwordField.getPassword());
				String confirm = new String(passwordField_1.getPassword());
				/* On vérifie que tous les champs sont bien remplis */
				if(saiNom.getText().compareTo("") == 0 || saiPrenom.getText().compareTo("") == 0 || saiLogin.getText().compareTo("") == 0 || mdp.compareTo("") == 0 || confirm.compareTo("") == 0){
					erreur = true;
					libErreur.setText("ERREUR : Vous devez remplir tous les champs");
				}
				/* On parcours la liste en entier */
				for(i=1;i<listeUtilisateurs.size()+1;i++){
					/* On vérifie que le login n'est pas utilisé */
					if(saiLogin.getText().compareTo(listeUtilisateurs.get(i).getLogin()) == 0){
						/* Si le login est déjà utilisé on met erreur à true */
						erreur = true;
						libErreur.setText("ERREUR : Le login que vous avez choisi est déjà utilisé");
					}
				}
				/* On vérifie maintenant que le mot de passe et la confirmation sont identiques */
				if(mdp.compareTo(confirm)!=0){
					/* Si ce n'est pas le cas on met erreur à true */
					erreur = true;
					libErreur.setText("ERREUR : Votre confirmation de mot de passe n'est pas identique au mot de passe");
				}
				/* On vérifie que il n'y a pas de / dans les saisie du l'utilisateur */
				if(saiNom.getText().contains("/") || saiPrenom.getText().contains("/") || saiLogin.getText().contains("/")){
					erreur = true;
					libErreur.setText("ERREUR : Vos informations ne peuvent pas contenir de  /");
				}
				/* On vérifie la valeur de erreur */
				if(erreur != true){
					/* On envoie les infos au serveur */
					/* On concatène tout dans une chaine sous forme : nom$£€prenom$£€login$£€password */
					String infos = saiNom.getText()+"/"+saiPrenom.getText()+"/"+saiLogin.getText()+"/"+mdp;
					/* On envoie les infos au serveur */
					envoyerMesg(infos);
					/* On attend un message de confirmation du serveur 1 -> ok / 0 -> ko */
					if(Integer.parseInt(lireMesg()) != 1){
						/* L'inscription a échouée, on réessaye */
						libErreur.setText("ERREUR : L'inscription a échoué, merci de réessayer ultérieurement");
					}else{
						/* L'inscription a réussi, on affiche une popup pour informer le client et on le redirige vers le menu principal */
						popUpConfirmation.setVisible(true);
					}
				}
			}
		});
		btnConfirm.setSelectedIcon(null);
		btnConfirm.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnConfirm.setBounds(575, 600, 117, 25);
		panel.add(btnConfirm);
		
		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FenMenuPrincipal fenMenu = new FenMenuPrincipal();
				fenMenu.ouvrirFenetre();
				fermerFenetre();
			}
		});
		btnAnnuler.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnAnnuler.setBounds(699, 600, 117, 25);
		panel.add(btnAnnuler);
	}
		
		
}
