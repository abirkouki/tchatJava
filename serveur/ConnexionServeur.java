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
 * @author florian
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
	 * Socket du serveur sur lequel les client vont se connecter
	 */
	private ServerSocket sockServeur = null;
	
	/**
	 * Socket permettant de connecter un client au serveur
	 */
	private Socket sockConnexion = null;
	
	
	/**
	 * Buffer de lecture pour lire les messages reçu du client
	 */
	private BufferedReader lire;
	
	/**
	 * Constructeur de la connexion permettant de pointer sur le serveur passé en paramètre
	 * @param serveur Serveur sur lequel les client vont réaliser la connexion.
	 */
	public ConnexionServeur(ServerSocket sockServeur){
		/* On récupère la socket du serveur passée en paramètre */
		this.sockServeur = sockServeur;
	}
	

	/**
	 * @return La socket du serveur
	 */
	public ServerSocket getSockServeur() {
		return sockServeur;
	}


	/**
	 * @return La socket de connexion au serveur
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
			System.out.println("Imposible d'envoyer un message au client");
		}
	}


	/**
	 * Permet de réaliser la connexion entre un client et le serveur.
	 */
	public void run() {
		int requeteClient;
		try{
			/* On boucle infinie pour permettre la connexion de plusieurs clients */
			while(true){
				/* On attend la connexion d'un client, et quand le client se connecte au initialise la socket de connexion */
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
					/* On créer le thread correspondant */
					this.thAuthentification = new Thread(new Authentification(this.sockConnexion));
					/* On lance le thread correspondant */
					this.thAuthentification.start();
				}else{
					if(requeteClient == 2){
						/* Demande d'inscription */
						/* On créer le thread correspondant */
						this.thInscription = new Thread(new Inscription(this.sockConnexion));
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
