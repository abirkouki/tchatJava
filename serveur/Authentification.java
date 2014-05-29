/**
 * 
 */
package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import client.Utilisateur;

/**
 * @author florian
 *
 */
public class Authentification implements Runnable {
	
	/**
	 * Socket de connexion avec le client
	 */
	private Socket sockConnexion;
	
	/**
	 * Login reçu du client
	 */
	private String login;
	
	/**
	 * Password reçu du client
	 */
	private String pass;
	
	/**
	 * Buffer de lecture pour lire les messages reçu du client
	 */
	private BufferedReader lire;
	
	/**
	 * Buffer permettant d'écrire un message au client connecté
	 */
	private PrintWriter ecrire = null;
	
	/**
	 * Socket du serveur
	 */
	private ServerSocket sockServ;
	
	/**
	 * Thread de l'application
	 */
	private Thread thApplication = null;
	
	/**
	 * Serveur sur lequel tourne l'aplli
	 */
	private Serveur serveur;
	
	/**
	 * Constructeur de la classe Authentification
	 * @param sockConnexion Socket de connexion du client
	 * @param sockServ Socket du serveur.
	 * @param serveur Serveur sur lequel tourne actuellement l'application.
	 */
	public Authentification(Socket sockConnexion, ServerSocket sockServ, Serveur serveur){
		this.sockConnexion = sockConnexion;
		this.sockServ = sockServ;
		this.serveur = serveur;
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
	 * Boucle permettant de vérifier les identifiants d'un utilisateur et de le connecter
	 */
	public void run() {
		while(true){
			/* On attend la demande client (0 : authentification normale ou 1 : authentification en tant que visiteur) */
			int requeteClient = Integer.parseInt(lireMesg()); /* Identifiant de la requete cliente */
			if(requeteClient == 0){
				Boolean erreur = true;
					String infos = "";
					String[] infosDecomp;
					/* On attend les infos du client */
					infos = lireMesg();
					System.out.println(infos);
					/* On décompose en login + mdp */
					infosDecomp = infos.split("/");
					this.login = infosDecomp[0];
					this.pass = infosDecomp[1];
					/* On raffraichit la liste des utilisateurs */
					this.serveur.initUtilisateurs();
					/* On parcours la liste pour vérifier si la combinaison login + mdp est correcte */
					int i; /* indice de parcours de la liste */
					Boolean loginExist = false;
					for(i=0;i<this.serveur.getListeUtilisateurs().size();i++){
						if(this.login.equals(this.serveur.getListeUtilisateurs().get(i).getLogin())){
							/* On a localisé l'utilisateur on vérifie maintenant son mot de passe */
							loginExist = true;
							System.out.println("Login trouvé");
							if(this.pass.equals(this.serveur.getListeUtilisateurs().get(i).getPassword()) && this.serveur.isBanni(this.serveur.getListeUtilisateurs().get(i))== false){
								/* Les mots de passes correspondent */
								envoyerMesg("1");
								System.out.println("Mdp ok");
								/* On ajoute l'utilisateur à la liste des utlisateurs connectés */
								Utilisateur nouvUtil = new Utilisateur(this.serveur.getListeUtilisateurs().get(i).getId(), this.serveur.getListeUtilisateurs().get(i).getLogin(), this.serveur.getListeUtilisateurs().get(i).getNom(), this.serveur.getListeUtilisateurs().get(i).getPrenom(), this.serveur.getListeUtilisateurs().get(i).getPassword(), this.serveur.getListeUtilisateurs().get(i).getGrade());
								this.serveur.addConnecte(nouvUtil);
								/* On envoi toutes les infos relatives à l'utilisateur */
								envoyerMesg(this.serveur.getListeUtilisateurs().get(i).getId()+"/"+this.serveur.getListeUtilisateurs().get(i).getLogin()+"/"+this.serveur.getListeUtilisateurs().get(i).getNom()+"/"+this.serveur.getListeUtilisateurs().get(i).getPrenom()+"/"+this.serveur.getListeUtilisateurs().get(i).getPassword()+"/"+this.serveur.getListeUtilisateurs().get(i).getGrade());
								erreur = false;
							}else{
								if(this.serveur.isBanni(this.serveur.getListeUtilisateurs().get(i))== true){
									envoyerMesg("2"); /* utilisateur banni */
								}else{
									/* Les mots de passes sont différents */
									envoyerMesg("0");
									System.out.println("Mdp pas ok");
								}
								
							}
						}
					}
					if(loginExist == false){
						/* le login n'existe pas */
						erreur = true;
						envoyerMesg("0");
						System.out.println("Login pas ok");
					}
					if(erreur == false){
						/* On lance le thread de Tchat*/
						System.out.println("Début de processus de Tchat normal");
						this.thApplication = new Thread(new Application(this.sockConnexion, this.serveur));
						this.thApplication.start();
						System.out.println("Thread start");
						break;
					}
			}
			if(requeteClient == 1){
				/* Connexion en tant que visiteur */
				/* On lance le thread de Tchat*/
				System.out.println("Début de processus de Tchat visiteur");
				this.thApplication = new Thread(new Application(this.sockConnexion, this.serveur));
				this.thApplication.start();
			}
		}
	}

}
