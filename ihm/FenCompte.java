package ihm;

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

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import client.Utilisateur;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FenCompte {

	private JFrame frame;
	
	/**
	 * Buffer permettant de lire les messages du serveur
	 */
	private BufferedReader lire = null;
	
	/**
	 * Bufer d'écriture pour envoyer des messages au serveur
	 */
	private PrintWriter ecrire;
	
	/**
	 * Socket de connexion du client
	 */
	private Socket sockConnexion;
	
	/**
	 * Utilisateur courrant
	 */
	private Utilisateur utilisateur;
	
	private JPasswordField saiMdpNew2;
	private JPasswordField saiMdpNew;
	private JPasswordField saiMdpActu;

	/**
	 * Ouvre la fenêtre.
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
			System.out.println("Imposible d'envoyer un message au client");
		}
	}
	

	/**
	 * Create the application.
	 */
	public FenCompte(Socket sockConnexion, Utilisateur utilisateur){
		this.sockConnexion = sockConnexion;
		this.utilisateur = utilisateur;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Application Tchat STRI");
		frame.setResizable(false);
		frame.setBounds(100, 100, 1024, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel libTitre = new JLabel("GESTION DU COMPTE");
		libTitre.setForeground(Color.RED);
		libTitre.setFont(new Font("Liberation Serif", Font.BOLD, 23));
		libTitre.setBounds(380, 12, 262, 35);
		panel.add(libTitre);
		
		JLabel libInfosPerso = new JLabel("Vos informations personnelles :");
		libInfosPerso.setForeground(Color.BLACK);
		libInfosPerso.setFont(new Font("Liberation Serif", Font.ITALIC, 20));
		libInfosPerso.setBounds(48, 124, 262, 35);
		panel.add(libInfosPerso);
		
		JLabel libModiferPassword = new JLabel("Modifier votre mot de passe :");
		libModiferPassword.setForeground(Color.BLACK);
		libModiferPassword.setFont(new Font("Liberation Serif", Font.ITALIC, 20));
		libModiferPassword.setBounds(48, 359, 262, 35);
		panel.add(libModiferPassword);
		
		final JLabel libInfo = new JLabel("Veuillez saisir votre ancien et votre nouveau mot de passe");
		libInfo.setHorizontalAlignment(SwingConstants.CENTER);
		libInfo.setFont(new Font("Liberation Serif", Font.BOLD | Font.ITALIC, 15));
		libInfo.setBounds(12, 406, 998, 18);
		panel.add(libInfo);
		
		JLabel libMdpNew2 = new JLabel("Confirmer le Mot de passe :");
		libMdpNew2.setHorizontalAlignment(SwingConstants.LEFT);
		libMdpNew2.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libMdpNew2.setBounds(199, 579, 216, 21);
		panel.add(libMdpNew2);
		
		saiMdpNew2 = new JPasswordField();
		saiMdpNew2.setBounds(536, 581, 241, 19);
		panel.add(saiMdpNew2);
		
		JLabel libMdpNew = new JLabel("Nouveau mot de passe :");
		libMdpNew.setHorizontalAlignment(SwingConstants.LEFT);
		libMdpNew.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libMdpNew.setBounds(199, 529, 216, 21);
		panel.add(libMdpNew);
		
		saiMdpNew = new JPasswordField();
		saiMdpNew.setBounds(536, 531, 241, 19);
		panel.add(saiMdpNew);
		
		JLabel libMdpActu = new JLabel("Mot de passe actuel :");
		libMdpActu.setHorizontalAlignment(SwingConstants.LEFT);
		libMdpActu.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libMdpActu.setBounds(199, 475, 216, 21);
		panel.add(libMdpActu);
		
		saiMdpActu = new JPasswordField();
		saiMdpActu.setBounds(536, 477, 241, 19);
		panel.add(saiMdpActu);
		
		JLabel libNom = new JLabel("Votre nom :");
		libNom.setHorizontalAlignment(SwingConstants.LEFT);
		libNom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libNom.setBounds(94, 189, 216, 21);
		panel.add(libNom);
		
		JLabel libPrenom = new JLabel("Votre prénom :");
		libPrenom.setHorizontalAlignment(SwingConstants.LEFT);
		libPrenom.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libPrenom.setBounds(94, 243, 216, 21);
		panel.add(libPrenom);
		
		JLabel libLogin = new JLabel("Votre login :");
		libLogin.setHorizontalAlignment(SwingConstants.LEFT);
		libLogin.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libLogin.setBounds(94, 299, 216, 21);
		panel.add(libLogin);
		
		JLabel libNomUser = new JLabel(this.utilisateur.getNom());
		libNomUser.setHorizontalAlignment(SwingConstants.LEFT);
		libNomUser.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libNomUser.setBounds(278, 193, 216, 21);
		panel.add(libNomUser);
		
		JLabel libPrenomUser = new JLabel(this.utilisateur.getPrenom());
		libPrenomUser.setHorizontalAlignment(SwingConstants.LEFT);
		libPrenomUser.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libPrenomUser.setBounds(278, 247, 216, 21);
		panel.add(libPrenomUser);
		
		JLabel libLoginUser = new JLabel(this.utilisateur.getLogin());
		libLoginUser.setHorizontalAlignment(SwingConstants.LEFT);
		libLoginUser.setFont(new Font("Liberation Serif", Font.PLAIN, 18));
		libLoginUser.setBounds(278, 303, 216, 21);
		panel.add(libLoginUser);
		
		JButton button = new JButton("Valider");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* On vérifie que les trois champs sont remplis */
				String mdpOld = new String(saiMdpActu.getPassword());
				String mdpNew = new String(saiMdpNew.getPassword());
				String confirm = new String(saiMdpNew2.getPassword());
				if(mdpOld.equals("") || mdpNew.equals("") || confirm.equals("")){
					libInfo.setText("ERREUR : Vous devez remplir tous les champs");
				}else{
					/* on vérifie que le nouveau mot de passe est bien égal à la confirmation */
					if(mdpNew.equals(confirm) == false){
						libInfo.setText("ERREUR : Votre nouveau mot de passe et sa confirmation ne sont pas identiques");
					}else{
						/* on vérifie qu'il n'y a pas de / ou de # */
						if(mdpNew.contains("/") == true || mdpNew.contains("#") == true){
							libInfo.setText("ERREUR : Votre mot de passe ne peut contenir de / ou de #");
						}else{
							/* On envoi la requete de modification de password au serveur */
							envoyerMesg("13");
							/* on attend la réponse du serveur */
							if(Integer.parseInt(lireMesg()) == 13){
								/* On envoi au serveur l'ancien password et l'idUtil pour qu'il vérifie la bonne correspondance */
								envoyerMesg(String.valueOf(utilisateur.getId())+"/"+mdpOld+"/"+mdpNew);
								/* On attend la rep serveur 0 : Erreur / 1 :  Ok */
								int rep = Integer.parseInt(lireMesg());
								if(rep == 1){
									/* Ok */
									JOptionPane.showMessageDialog(panel, "Votre mot de passe a bien été modifié","Mot de passe modifié",JOptionPane.INFORMATION_MESSAGE);
								}else{
									/* erreur ancien mot de passe incorrect */
									libInfo.setText("ERREUR, Votre mot de passe acutel n'est pas correct !");
								}
							}else{
								JOptionPane.showMessageDialog(panel, "ERREUR, impossible de joindre le serveur, merci de réessayer ultérieurement","Erreur serveur injoignable",JOptionPane.ERROR_MESSAGE);
							}		
						}
					}
				}
			}
		});
		button.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		button.setBounds(660, 640, 117, 25);
		panel.add(button);
		
		JButton btnRetour = new JButton("Retour");
		btnRetour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* On ferme la fenêtre du canal et on affiche la page d'accueil */
				FenAccueil fenAcc = new FenAccueil(sockConnexion,String.valueOf(utilisateur.getId())+"/"+utilisateur.getLogin()+"/"+utilisateur.getNom()+"/"+utilisateur.getPrenom()+"/"+utilisateur.getPassword()+"/"+String.valueOf(utilisateur.getGrade()));
				fenAcc.ouvrirFenetre();
				fermerFenetre();
			}
		});
		btnRetour.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnRetour.setBounds(893, 640, 117, 25);
		panel.add(btnRetour);
	}
}
