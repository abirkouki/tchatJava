/**
 * Package contenant toutes les classes pour le serveur du projet.
 */
package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author STRI
 * Classe permettant d'établir la connexion entre un client et le serveur.
 */
public class ConnexionServeur implements Runnable{
	
	/**
	 * Thread permettant de lancer le processus d'inscription
	 */
	private Thread thInscription = null;
	
	/**
	 * Thread permettant de lancer le processus d'authentification
	 */
	private Thread thAuthentification = null;
	
	/**
	 * Buffer permettant d'écrire un message au client connecté
	 */
	private PrintWriter ecrire = null;
	
	/**
	 * Socket du serveur sur lequel les clients vont se connecter
	 */
	private ServerSocket sockServeur = null;
	
	/**
	 * Socket permettant de connecter un client au serveur
	 */
	private Socket sockConnexion = null;
	
	/**
	 * Serveur sur lequel tourne l'appli
	 */
	private Serveur serveur;
	
	
	/**
	 * Buffer de lecture pour lire les messages reçus du client
	 */
	private BufferedReader lire;
	
	/**
	 * Constructeur de la connexion permettant de pointer sur le serveur passé en paramètre
	 * @param sockServeur Socket du serveur qui héberge actuellement  l'application.
	 * @param serveur Serveur sur lequel les clients vont réaliser la connexion.
	 */
	public ConnexionServeur(ServerSocket sockServeur, Serveur serveur){
		/* On récupère le socket du serveur passé en paramètre */
		this.sockServeur = sockServeur;
		this.serveur = serveur;
	}
	

	/**
	 * Accesseur sur le socket du serveur.
	 * @return Le socket du serveur
	 */
	public ServerSocket getSockServeur() {
		return sockServeur;
	}


	/**
	 * Accesseur sur le socket de connexion du client.
	 * @return Le socket de connexion au serveur
	 */
	public Socket getSockConnexion() {
		return sockConnexion;
	}

	
	/**
	 * Récupère un message reçu du client
	 * @return Message reçu du client.
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
	 * Permet de réaliser la connexion entre un client et le serveur.
	 */
	public void run() {
		int requeteClient;
		try{
			/* On boucle indéfiniment pour permettre la connexion de plusieurs clients */
			while(true){
				/* On attend la connexion d'un client, et quand le client se connecte on initialise la socket de connexion */
				this.sockConnexion = this.sockServeur.accept();
				System.out.println("Client connecté.");
				/* On envoie un message (1) au client pour l'informer qu'il est bien connecté */
				this.envoyerMesg(Integer.toString(1));
				/* On lit la requête client */
				/* 2 types de requêtes ici, 1 : demande d'authentification / 2 : demande d'inscription */
				requeteClient = Integer.parseInt(this.lireMesg());
				/* On l'affiche dans la console du serveur */
				System.out.println("Message du client : "+requeteClient);
				/* Selon le type de requête, on lance le thread correspondant */
				if(requeteClient == 1){
					/* Demande d'authentification */
					/* On crée le thread correspondant */
					this.thAuthentification = new Thread(new Authentification(this.sockConnexion, this.sockServeur, this.serveur));
					/* On lance le thread correspondant */
					this.thAuthentification.start();
				}else{
					if(requeteClient == 2){
						/* Demande d'inscription */
						/* On crée le thread correspondant */
						this.thInscription = new Thread(new Inscription(this.sockConnexion, this.sockServeur, this.serveur));
						/* On lance le thread d'inscription */
						this.thInscription.start();
					}
				}
				
			}
		}
		catch(IOException exception){
			this.envoyerMesg("0");
			System.out.println("ERREUR : Impossible d'établir la connexion sur le serveur");
		}
		
	}

	
}
