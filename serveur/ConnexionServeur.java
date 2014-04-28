/**
 * Package contenant toutes les classes pour le serveur du projet.
 */
package serveur;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author florian
 * Classe permettant d'établir la connexion entre un client et le serveur.
 */
public class ConnexionServeur{
	
	/**
	 * Serveur sur lequel les client vont se connecter
	 */
	Serveur serveur = null;
	
	/**
	 * Buffer permettant d'écrire un message au client connecté
	 */
	PrintWriter ecrire = null;
	
	/**
	 * Socket du serveur sur lequel les client vont se connecter
	 */
	ServerSocket sockServeur = null;
	
	/**
	 * Socket permettant de connecter un client au serveur
	 */
	Socket sockConnexion = null;
	
	/**
	 * Constructeur de la connexion permettant de pointer sur le serveur passé en paramètre
	 * @param serveur Serveur sur lequel les client vont réaliser la connexion.
	 */
	public ConnexionServeur(Serveur serveur){
		/* On récupère la socket du serveur passé en paramètre */
		this.serveur = serveur;
		this.sockServeur = this.serveur.getSock();
	}
	
	/**
	 * Permet de réaliser la connexion entre un client et le serveur.
	 */
	public void connect() {
		try{
			/* On attend la connexion d'un client, et quand le client se connecte au initialise la socket de connexion */
			this.sockConnexion = this.sockServeur.accept();
			System.out.println("Client connecté.");
			/* On envoie un message au client pour l'informer qu'il est bien connecté */
			ecrire = new PrintWriter(sockConnexion.getOutputStream());
			ecrire.println("Connexion établie avec success");
			/* on vide ensuite le buffer d'écriture */
			ecrire.flush();
		}
		catch(IOException exception){
			System.out.println("ERREUR : Impossible d'établir la connexion sur le serveur");
		}
	}

	
}
