/**
 * 
 */
package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import client.Utilisateur;

/**
 * La classe application permet de réaliser les requêtes du client une fois que celui-ci a été identifié sur l'application.
 * @author florian
 *
 */
public class Application implements Runnable {
	
	/**
	 * Socket de connexion du client
	 */
	private Socket sockConnexion;
	
	/**
	 * Buffer de lecture pour lire les messages reçu du client
	 */
	private BufferedReader lire;
	
	/**
	 * Buffer permettant d'écrire un message au client connecté
	 */
	private PrintWriter ecrire = null;
	
	/**
	 * Serveur sur lequel tourne l'aplli
	 */
	private Serveur serveur;
	
	
	/**
	 * Créer une application serveur permettant de gérer les requêtes clientes
	 * @param sockConnexion Identifiant de la connexion du client
	 */
	public Application(Socket sockConnexion, Serveur serveur){
		this.sockConnexion = sockConnexion;	
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
	 * Fonction permettant de gérer les requêtes clientes (boucle infinie)
	 */
	public void run() {
		int requeteClient; /* Identifiant de la requête client */
		String infosUtilisateur;
		String[] infosUtilisateurDecomp;
		/* On réalise un boucle infinie pour lire et traiter les demandes clientes */
		while(true){
			/* On attend une requête d'un client */
			requeteClient = Integer.parseInt(lireMesg());
			/* On identifie la requête client et on lui confirme que on a bien reçu sa requête en lui renvoyant l'identifiant */
			if(requeteClient == 1){
				/* Demande de modification de statut */
				/* on confirme la bonne réception de la demande */
				envoyerMesg("1");
				/* On attend les nouvelles infos sous forme idUser/statut/justif */
				infosUtilisateur = lireMesg();
				System.out.println("Message reçu du client : "+infosUtilisateur);
				infosUtilisateurDecomp = infosUtilisateur.split("/");
				/* On recherche l'utilisateur dans la liste des connectés */
				int i; /* indice de parcours de la liste */
				for(i=0;i<this.serveur.getListeConnecte().size();i++){
					if(this.serveur.getListeConnecte().get(i).getId() == Integer.parseInt(infosUtilisateurDecomp[0])){
						/* On a trouvé l'utilisateur, on modifie son statut */
						this.serveur.getListeConnecte().get(i).setStatut(Integer.parseInt(infosUtilisateurDecomp[1]));
						this.serveur.getListeConnecte().get(i).setJustification(infosUtilisateurDecomp[2]);
					}
				}
				/* On informe le client que le changement de statut a bien été pris en compte */
				envoyerMesg("1");
			}
		}
		
	}

}
