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
public class Inscription extends Serveur implements Runnable{
	/**
	 * Nom du nouveau membre
	 */
	private String nom;
	
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
	 * Initialise le processus d'inscription avec la socket de connexion du client
	 */
	public Inscription(Socket sockConnexion){
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
	
	public void run() {
		/* On informe le client du début du processus d'inscription */
		System.out.println("On rentre dans le processus d'inscription");
		System.out.println(this.sockConnexion);
		this.envoyerMesg("2");
		

	}

}
