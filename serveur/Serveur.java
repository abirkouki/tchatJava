/**
 * Package contenant toutes les classes pour le serveur du projet.
 */
package serveur;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Classe permettant de gérer le serveur du mini tchat
 * @author florian
 */
public class Serveur {
	
	/**
	 * Socket du serveur qui va écouter sur le port donné en paramètre du constructeur.
	 * Par défault on l'initialise à null.
	 */
	ServerSocket sockServeur = null;
	
	

	/**
	 * Constructeur du serveur permettant de créer et initialiser le serveur.
	 * @param numeroPort Numéro de port sur lequel le serveur va écouter.
	 * @throws IOException 
	 */
	public Serveur() {
		/* On essaye de connecter le serveur sur le numero de port demandé */
		try{
			/* On initialise la socket avec le numero de port fourni en paramètre */
			this.sockServeur = new ServerSocket(2369);
			/* On affiche un message pour informer du succes de la création de la socket */
			System.out.println("Création de la socket sur le port 2369 -> OK");
		}
		/* Si la connexion échoue, on affiche un message pour informer que le port est indisponible */
		catch(IOException exception){
			System.out.println("ERREUR : Le port 2369 est actuellement indisponible");
		}
	}
	
	/**
	 * Accesseur sur la socket du serveur
	 * @return Socket du serveur.
	 */
	public ServerSocket getSock(){
		return this.sockServeur;
	}

	

}
