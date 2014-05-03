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
import java.rmi.server.SocketSecurityException;

import client.Utilisateur;

/**
 * @author florian
 *
 */
public class Inscription implements Runnable{
	/**
	 * Nom du nouveau membre
	 */
	private String nom;
	
	/**
	 * Thread permettant de lancer le processus d'authentification
	 */
	private Thread thConnexion = null;
	
	/**
	 * Prénom du nouveau membre
	 */
	private String prenom;
	
	/**
	 * Mot de passe du nouveau membre
	 */
	private String motDePasse;
	
	/**
	 * Login du nouveau membre
	 */
	private String login;

	/**
	 * Buffer de lecture pour lire les messages reçu du client
	 */
	private BufferedReader lire;
	
	/**
	 * Buffer permettant d'écrire un message au client connecté
	 */
	private PrintWriter ecrire = null;
	
	/**
	 * Socket de conexion du client
	 */
	private Socket sockConnexion;
	
	/**
	 * Serveur sur lequel tourne l'appli
	 */
	private Serveur serveur;
	
	/**
	 * Socket du server
	 */
	private ServerSocket sockServ;
	
	/**
	 * Initialise le processus d'inscription avec la socket de connexion du client
	 */
	public Inscription(Socket sockConnexion, ServerSocket sockServ, Serveur serveur){
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
	
	public void run() {
		Serveur serv =  new Serveur();
		/* On attend le message de l'utilisateur avec tous les champs (nom/prenom/login/password) */
		String infos = new String();
		infos = lireMesg();
		System.out.println("Message reçu du client : "+infos);
		/* On décompose le message reçu */
		String[] infosDecomp = infos.split("/");
		/* On affecte aux champs la bonne valeur */
		this.nom = infosDecomp[0];
		this.prenom = infosDecomp[1];
		this.login = infosDecomp[2];
		this.motDePasse = infosDecomp[3];
		/* On récupère la liste des utilisateurs */
		serv.initUtilisateurs();
		/* On récupère le nombre d'utilisateurs déjà inscrits pour affecter l'identifiant au nouvel utilisateur */
		int id = serv.getListeUtilisateurs().size();
		/* On créer le nouvel utilisateur */
		Utilisateur nouvelUtilisateur = new Utilisateur(id, this.login, this.nom, this.prenom, this.motDePasse, 1);
		/* On l'ajoute dans la liste des utilisateurs */
		serv.addUtilisateur(id, nouvelUtilisateur);
		/* On sauvegarde la nouvelle liste des utilisateurs */
		if(serv.saveUtilisateurs() != 0){
			/* Echec de la sauvegarde de la liste des utilisateurs, on abandonne tout et on informe le client */
			envoyerMesg("0");
		}else{
			/* L'utilisateur a bien été ajouté */
			envoyerMesg("1");
			/* On créer ensuite le thread qui va rediriger sur la classe ConnexionServeur */
			this.thConnexion = new Thread(new ConnexionServeur(this.sockServ,this.serveur));
			/* On lance le thread */
			this.thConnexion.start();
		}
	}

}
