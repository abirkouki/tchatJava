/**
 * Package contenant toutes les classes relatives à l'application cliente
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * @author florian
 * Classe permettant d'établir la connexion entre un client et le serveur.
 */
public class ConnexionClient {
	
	/**
	 * Socket permettant de réaliser la connexion au serveur
	 */
	private Socket sockConnexion = null;

	
	/**
	 * Buffer permettant de lire les messages du serveur
	 */
	private BufferedReader lire = null;
	
	/**
	 * Bufer d'écriture pour envoyer des messages au serveur
	 */
	private PrintWriter ecrire;
	
	/**
	 * Retourne la socket de connexion du client
	 * @return La socket de connexion du client
	 */
	public Socket getSock(){
		return this.sockConnexion;
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
	 * Réalise la connexion du client au serveur et intialise la socket de connexion.
	 */
	public void connect(int requete){
		int codeServeur; /* code de début ou de fin de processus envoyé par le serveur */
		/* On tente de connecter le client au serveur en lui envoyant l'identifiant du client */
		try{
			this.sockConnexion = new Socket(InetAddress.getLocalHost(), 2369);
			/* On récupère le message du serveur après notre demande de connexion */
			/* Si il est différent de 0 on envoie notre requête (ident : demande d'identification / inscr : demande d'inscription */
			codeServeur = Integer.parseInt(this.lireMesg());
			if(codeServeur != 0){
				/* Si le serveur confirme la connexion, on envoie notre requête */
				this.envoyerMesg(Integer.toString(requete));
			}else{
				/* Informer le client de l'échec de la connexion au serveur */
			}
			
		}
		catch(IOException exception){
			System.out.println("ERREUR : Connexion impossible au serveur");
		}
		
	}
	
}
