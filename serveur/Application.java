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
			//System.out.println("On attend la req");
			String reqCli = lireMesg();
			//System.out.println("On a recu comme req : "+reqCli);
			requeteClient = Integer.parseInt(reqCli);
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
			if(requeteClient == 2){
				/* Requête pour rejoindre un canal */
				String listeCanaux = "";
				int i;
				/* Gérer les canaux privés */
				for(i=0;i<this.serveur.getListeCanaux().size();i++){
					listeCanaux += String.valueOf(this.serveur.getListeCanaux().get(i).getId())+"#"+this.serveur.getListeCanaux().get(i).getTitre()+"/";
				}
				envoyerMesg(listeCanaux);
			}
			if(requeteClient == 3){
				/* Demande de création d'un canal */
				/* On confirme la bonne réception de la demande */
				/* On attend les infos de l'utilisateur */
				/* On créer le canal */
				/* On l'ajoute à la liste des canaux du serveur */
				/* On créer le fichier des messages */
				/* on renvoie la confirmation de la création à l'utilisateur */
			}
			if(requeteClient == 4){
				/* Envoie d'un message sur un canal */
				/* on confirme la bonne réception de la demande */
				envoyerMesg("4");
				/* on récupère la chaine du client qu'il va falloir décomposer */
				String message;
				String[] messageDecomp;
				message = lireMesg();
				System.out.println("Message reçu du client : "+message);
				messageDecomp = message.split("#"); /* on décompose la chaine idUser#idCanal#message */
				System.out.println("MesgDecomp : "+messageDecomp[0]+" "+messageDecomp[1]+" "+messageDecomp[2]);
				int idUser = Integer.parseInt(messageDecomp[0]);
				int idCanal = Integer.parseInt(messageDecomp[1]);
				/* on monte la chaine qui sera enregistrée (Nom prenom) : message */
				String message2 = "("+this.serveur.getUtilisateur(idUser).getNom()+" "+this.serveur.getUtilisateur(idUser).getPrenom()+") : "+messageDecomp[2];
				Canal canal = this.serveur.getCanal(idCanal);
				System.out.println("Mesg : "+message2);
				/* on enregistre le nouveau message dans la liste des messages du canal */
				if(canal != null){
					System.out.println("Canal not null");
					canal.addMessage(message2);
					envoyerMesg("1");
				}else{
					System.out.println("Canal null");
					envoyerMesg("0");
				}
			}
			if(requeteClient == 5){
				/* Actualisation des messages d'un canal */
				/* on confirme la bonne réception de la demande */
				envoyerMesg("5");
				/* On lit l'identifiant du canal */
				int idCanal;
				idCanal = Integer.parseInt(lireMesg());
				/* On cherche le canal correspondant dans la liste */
				int i; /* indice de parcours de la liste des messages */
				/* On parcours l'ensemble de la liste pour chercher le canal */
				for(i=0;i<this.serveur.getListeCanaux().size();i++){
					if(this.serveur.getListeCanaux().get(i).getId() == idCanal){
						/* on a trouvé le canal */
						/* on envoie la liste des messages */
						String messages = ""; /* chaine contenant les messages séparés par # */
						int j; /* indice de parcours de la liste des messages */
						/* on construit la chaine que l'on va devoir envoyé */
						for(j=0;j<this.serveur.getListeCanaux().get(i).getLitseMessages().size();j++){
							messages += this.serveur.getListeCanaux().get(i).getLitseMessages().get(j)+"#";
						}
						/* on envoie la chaine des messages */
						envoyerMesg(messages);
					}
				}
			}
		}
		
	}

}
