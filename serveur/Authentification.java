/**
 * 
 */
package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author florian
 *
 */
public class Authentification extends Serveur implements Runnable {
	
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
	 * Constructeur de la classe Authentification
	 * @param sockConnexion Socket de connexion du client
	 */
	public Authentification(Socket sockConnexion){
		this.sockConnexion = sockConnexion;
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
		Boolean erreur = true;
		do{
			/* On attend les infos du client */
			String infos = lireMesg();
			/* On décompose en login + mdp */
			String[] infosDecomp = infos.split("/");
			this.login = infosDecomp[0];
			this.pass = infosDecomp[1];
			/* On récupère la liste des utilisateurs */
			this.initListeUtilisateurs();
			/* On parcours la liste pour vérifier si la combinaison login + mdp est correcte */
			int i; /* indice de parcours de la liste */
			for(i=0;i<listeUtilisateurs.size();i++){
				if(this.login.compareTo(listeUtilisateurs.get(i).getLogin()) == 0){
					/* On a localisé l'utilisateur on vérifie maintenant son mot de passe */
					if(this.pass.compareTo(listeUtilisateurs.get(i).getPassword()) == 0){
						/* Les mots de passes correspondent */
						envoyerMesg("1");
						/* On envoi toutes les infos relatives à l'utilisateur */
						envoyerMesg(listeUtilisateurs.get(i).getId()+"/"+listeUtilisateurs.get(i).getLogin()+"/"+listeUtilisateurs.get(i).getNom()+"/"+listeUtilisateurs.get(i).getPrenom()+"/"+listeUtilisateurs.get(i).getPassword()+"/"+listeUtilisateurs.get(i).getGrade());
						/* On met le booleen a false pour sortir de la boucle */
						erreur = false;
					}else{
						/* Les mots de passes sont différents */
						envoyerMesg("0");
						
					}
				}
			}
		}while(erreur != false);
		/* On lance le thread de Tchat*/
		//System.out.println("Début de processus de Tchat");

	}

}
