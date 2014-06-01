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
	 * Créer une application serveur permettant de gérer les requêtes clientes.
	 * @param sockConnexion Identifiant de la connexion du client.
	 * @param serveur Serveur qui héberge actuellement l'application.
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
			if(requeteClient == 1){ /* Demande de modification de statut */
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
			if(requeteClient == 2){ /* Requête pour rejoindre un canal */
				/* On atteste la bonne réception de la requête */
				envoyerMesg("2");
				/* On attend que le client nous envoie l'identifiant de l'utilisateur */
				int idUtil = Integer.parseInt(lireMesg());
				
				String listeCanaux = "";
				int i;
				/* On regarde si l'utilisateur est Admin sur l'application ou pas */
				if(this.serveur.getUtilisateur(idUtil).getGrade() == 2){
					/* Si il est admin, alors il a accès à tous les canaux */
					for(i=0;i<this.serveur.getListeCanaux().size();i++){
						listeCanaux += String.valueOf(this.serveur.getListeCanaux().get(i).getId())+"#"+this.serveur.getListeCanaux().get(i).getTitre()+"/";
					}
				}else{
					/* Si l'utilisateur n'est pas admin alors on regarde la liste des invités sur les canaux */
					/* Gérer les canaux privés */
					for(i=0;i<this.serveur.getListeCanaux().size();i++){
						if(this.serveur.getListeCanaux().get(i) instanceof CanalPrive){
							/* si c'est un canal privé on regarde la liste des invités */
							CanalPrive canPriv = (CanalPrive) this.serveur.getListeCanaux().get(i);
							if(canPriv.isInvite(idUtil) == true && canPriv.isBanni(idUtil) == false){
								listeCanaux += String.valueOf(this.serveur.getListeCanaux().get(i).getId())+"#"+this.serveur.getListeCanaux().get(i).getTitre()+"/";
							}
						}else{
							listeCanaux += String.valueOf(this.serveur.getListeCanaux().get(i).getId())+"#"+this.serveur.getListeCanaux().get(i).getTitre()+"/";
						}	
					}
				}
				/* on envoie la liste */
				envoyerMesg(listeCanaux);
				
			}
			if(requeteClient == 3){ /* Demande de création d'un canal */
				
				/* On confirme la bonne réception de la demande */
				envoyerMesg("3");
				/* On attend les infos de l'utilisateur */
				String infosCanal = lireMesg();
				String[] infosCanalDecomp = infosCanal.split("/");
				/* On créer le canal */
				Canal canal = null;
				/* on vérifie le type de canal */
				if(Integer.parseInt(infosCanalDecomp[2]) == 1){
					/* canal public */
					canal = new CanalPublic(this.serveur.getListeCanaux().size(), infosCanalDecomp[1], this.serveur.getUtilisateur(Integer.parseInt(infosCanalDecomp[0])));
				}else{
					/* On créer la liste des invité */
					String[] invites = infosCanalDecomp[3].split("#");
					ArrayList<Utilisateur> listeInvit =  new ArrayList<Utilisateur>();
					int i = 0;
					for(i=0;i<invites.length;i++){
						listeInvit.add(this.serveur.getUtilisateur(Integer.parseInt(invites[i])));
					}
					canal = new CanalPrive(this.serveur.getListeCanaux().size(), infosCanalDecomp[1], this.serveur.getUtilisateur(Integer.parseInt(infosCanalDecomp[0])),listeInvit);
				}
				/* On l'ajoute à la liste des canaux du serveur */
				this.serveur.addCanal(canal);
				/* On sauvegarde la liste des canaux */
				this.serveur.saveCanaux();
				/* on renvoie la confirmation de la création à l'utilisateur */
				envoyerMesg("1");
			}
			if(requeteClient == 4){ /* Envoie d'un message sur un canal */
				
				/* on confirme la bonne réception de la demande */
				envoyerMesg("4");
				/* on récupère la chaine du client qu'il va falloir décomposer */
				String message;
				String[] messageDecomp;
				message = lireMesg();
				//System.out.println("Message reçu du client : "+message);
				messageDecomp = message.split("#"); /* on décompose la chaine idUser#idCanal#message ou pour les visiteur -1#nom#prenom#idCanal#message */
				String message2 = "";
				int idCanal;
				if(Integer.parseInt(messageDecomp[0]) == -1){
					/* visiteur */
					idCanal = Integer.parseInt(messageDecomp[3]);
					message2 = "("+messageDecomp[1]+" "+messageDecomp[2]+") : "+messageDecomp[4];
				}else{
					/* membre */
					int idUser = Integer.parseInt(messageDecomp[0]);
					idCanal = Integer.parseInt(messageDecomp[1]);
					/* on monte la chaine qui sera enregistrée (Nom prenom) : message */
					message2 = "("+this.serveur.getUtilisateur(idUser).getNom()+" "+this.serveur.getUtilisateur(idUser).getPrenom()+") : "+messageDecomp[2];
				}
				//System.out.println("MesgDecomp : "+messageDecomp[0]+" "+messageDecomp[1]+" "+messageDecomp[2]);
				
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
			if(requeteClient == 5){ /* Actualisation d'un canal */
				
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
						/* on envoi la liste des utilisateurs connectés */
						String listeUsers="";
						if(this.serveur.getListeCanaux().get(i).getListeConnectes().size()>0){
								for(j=0;j<this.serveur.getListeCanaux().get(i).getListeConnectes().size();j++){
									//System.out.println("for listeconnect");
									/* on vérifie si c'est un modérateur du canal */
									int x;
									Boolean moderateur = false;
									for(x=0;x<this.serveur.getListeCanaux().get(i).getListeModerateurs().size();x++){
										//System.out.println("for listMod");
										if(this.serveur.getListeCanaux().get(i).getListeConnectes().get(j) == this.serveur.getListeCanaux().get(i).getListeModerateurs().get(x)){
											//System.out.println("Modo trouvé");
											moderateur = true;
										}
									}
									if(moderateur == true){
										//System.out.println("Modo on add");
										listeUsers += this.serveur.getListeCanaux().get(i).getListeConnectes().get(j).getNom()+" "+this.serveur.getListeCanaux().get(i).getListeConnectes().get(j).getPrenom()+" (Modérateur)#";
									}else{
										//System.out.println("Pad modo on add");
										listeUsers += this.serveur.getListeCanaux().get(i).getListeConnectes().get(j).getNom()+" "+this.serveur.getListeCanaux().get(i).getListeConnectes().get(j).getPrenom()+"#";
									}
								}
								//System.out.println(listeUsers);
								envoyerMesg(this.serveur.getListeCanaux().get(i).getTitre()+"/"+listeUsers);
								//System.out.println("on a envoyé");
						}else{
							envoyerMesg(this.serveur.getListeCanaux().get(i).getTitre()+"/"+"0");
						}
					}
				}
			}
			if(requeteClient == 6){ /* Modification d'un canal */
				envoyerMesg("6"); /* on confirme la bonne réception de la requête */
				/* on attend la demande client 1 : modif titre / 2 : modif liste membre / 3 : modif liste moderateurs */
				int idReq = Integer.parseInt(lireMesg());
				/* on regarde la demande */
				if(idReq == 1){
					/* demande de modification du titre d'un canal */
					envoyerMesg("1"); /* on confirme la demande */
					String titre = lireMesg(); /* idCanal#Titre */
					Canal canal = this.serveur.getCanal(Integer.parseInt(titre.split("#")[0]));
					/* on vérifie que on a bien trouvé le canal */
					if(canal != null){
						/* on modifie le titre */
						canal.setTitre(titre.split("#")[1]);
						this.serveur.saveCanaux(); /* on sauvegarde la liste des canaux */
						envoyerMesg("1");
					}else{
						/* on indique l'erreur */
						envoyerMesg("0");
					}
					
				}
				
			}
			if(requeteClient == 7){ /*Demande de la liste des utilisateurs */
				
				/* on confirme la bonne réception de la requête */
				envoyerMesg("7");
				/* On envoi la liste des utilisateurs (id#nom#prenom) séparés par des / */
				String utilisateurs = "";
				int i;
				for(i=0;i<this.serveur.getListeUtilisateurs().size();i++){
					utilisateurs += String.valueOf(this.serveur.getListeUtilisateurs().get(i).getId())+"#"+this.serveur.getListeUtilisateurs().get(i).getNom()+"#"+this.serveur.getListeUtilisateurs().get(i).getPrenom()+"/";
				}
				envoyerMesg(utilisateurs);
			}
			if(requeteClient == 8){ /* Demande rejoindre un canal */
				envoyerMesg("8");
				/* on récupère l'identifiant du canal et l'identifiant utilisateur*/
				String mesg = lireMesg(); /* idUtil#idCanal */
				int idCanal = Integer.parseInt(mesg.split("#")[1]);
				int idUtil = Integer.parseInt(mesg.split("#")[0]);
				int i;
				System.out.println("id canal = "+idCanal);
				Boolean trouve = false;
				Boolean moderateur=false;
				String infosCanal="";
				/* On regarde que l'identifiant est correct */
				for(i=0;i<this.serveur.getListeCanaux().size();i++){
					if(this.serveur.getListeCanaux().get(i).getId() == idCanal){
						trouve = true;
						/* on regarde si il s'agit d'un canal privé ou pas */
						if(this.serveur.getListeCanaux().get(i) instanceof CanalPrive){
							infosCanal = "1"+"/"+String.valueOf(this.serveur.getListeCanaux().get(i).getId())+"/"+this.serveur.getListeCanaux().get(i).getTitre();
						}else{
							infosCanal = "0"+"/"+String.valueOf(this.serveur.getListeCanaux().get(i).getId())+"/"+this.serveur.getListeCanaux().get(i).getTitre();
						}
						int j;
						/* on regarde si l'utilisateur est modérateur du canal */
						for(j=0;j<this.serveur.getListeCanaux().get(i).getListeModerateurs().size();j++){
							if(this.serveur.getListeCanaux().get(i).getListeModerateurs().get(j).getId() == idUtil){
								moderateur = true;
							}
						}
					}
				}
				if(trouve == true){ 
					/* on a trouvé le canal */
					if(moderateur == true){
						/* on répond favorablement et l'utilisateur est modérateur */
						envoyerMesg("2");
					}else{
						/* on répond favorablement */
						envoyerMesg("1");
					}
					envoyerMesg(infosCanal);
					/* on ajoute l'utilisateur à la liste des connectés si il n'y est pas déjà */
					if(this.serveur.getCanal(idCanal).isConnect(this.serveur.getUtilisateur(idUtil))== false){
						this.serveur.getCanal(idCanal).getListeConnectes().add(this.serveur.getUtilisateur(idUtil));
					}
				}else{
					/* impossible de rejoindre le canal */
					envoyerMesg("0");
				}
			}
			if(requeteClient == 9){ /* Déco d'un canal */
				/* on confirme la réception de la demande */
				envoyerMesg("9");
				/* On attend l'id canal et l'id utilisateur : idCanal#idUtil */
				String ids = lireMesg();
				int idCanal = Integer.parseInt(ids.split("#")[0]);
				int idUtil = Integer.parseInt(ids.split("#")[1]);
				/* On recherche le canal */
				if(this.serveur.getCanal(idCanal) != null){
					/* on a trouvé le canal on supprime l'utilisateur de la liste des connectés */
					int i;
					Boolean trouve = false;
					for(i=0;i<this.serveur.getCanal(idCanal).getListeConnectes().size();i++){
						if(this.serveur.getUtilisateur(idUtil) == this.serveur.getCanal(idCanal).getListeConnectes().get(i)){
							/* on a trouvé l'utilisateur on le supprime */
							this.serveur.getCanal(idCanal).getListeConnectes().remove(i);
							trouve = true;
						}
					}
					if(trouve != true){
						/* l'utilisateur n'a pas été trouvé */
						envoyerMesg("0");
					}else{
						envoyerMesg("1");
					}
				}else{
					envoyerMesg("0");
				}
			}
			if(requeteClient == 10){ /*Demande de la liste des utilisateurs d'un canal avec leur grade */
				
				/* on confirme la bonne réception de la requête */
				envoyerMesg("10");
				/* On demande l'id canal */
				int idCanal = Integer.parseInt(lireMesg());
				/* On envoi la liste des utilisateurs (id#nom#prenom) séparés par des / */
				String utilisateurs = "";
				int i;
				for(i=0;i<this.serveur.getCanal(idCanal).getListeConnectes().size();i++){
					if(this.serveur.getCanal(idCanal).isModo(this.serveur.getCanal(idCanal).getListeConnectes().get(i).getId())== true){
						/* L'utilisateur est modérateur */
						/* On regarde si il est banni */
						if(this.serveur.getCanal(idCanal).isBanni(this.serveur.getCanal(idCanal).getListeConnectes().get(i).getId())== true){
							/* il est banni */
							utilisateurs += String.valueOf(this.serveur.getCanal(idCanal).getListeConnectes().get(i).getId())+"#"+this.serveur.getCanal(idCanal).getListeConnectes().get(i).getNom()+"#"+this.serveur.getCanal(idCanal).getListeConnectes().get(i).getPrenom()+"#1#1/";
						}else{
							utilisateurs += String.valueOf(this.serveur.getCanal(idCanal).getListeConnectes().get(i).getId())+"#"+this.serveur.getCanal(idCanal).getListeConnectes().get(i).getNom()+"#"+this.serveur.getCanal(idCanal).getListeConnectes().get(i).getPrenom()+"#1#0/";
						}
					}else{
						/* il n'est pas modo */
						/* On regarde si il est banni */
						if(this.serveur.getCanal(idCanal).isBanni(this.serveur.getCanal(idCanal).getListeConnectes().get(i).getId())== true){
							/* il est banni */
							utilisateurs += String.valueOf(this.serveur.getCanal(idCanal).getListeConnectes().get(i).getId())+"#"+this.serveur.getCanal(idCanal).getListeConnectes().get(i).getNom()+"#"+this.serveur.getCanal(idCanal).getListeConnectes().get(i).getPrenom()+"#0#1/";
						}else{
							utilisateurs += String.valueOf(this.serveur.getCanal(idCanal).getListeConnectes().get(i).getId())+"#"+this.serveur.getCanal(idCanal).getListeConnectes().get(i).getNom()+"#"+this.serveur.getCanal(idCanal).getListeConnectes().get(i).getPrenom()+"#0#0/";
						}
					}
					
				}
				envoyerMesg(utilisateurs);
			}
			if(requeteClient == 11){ /* Modification des grades utilisateurs sur un canal */
				/* on confirme la demande */
				envoyerMesg("11");
				/* on récupère l'identifiant du canal */
				int idCanal = Integer.parseInt(lireMesg());
				/* On récupère les infos */
				String infos = lireMesg();
				/* On décompose les infos par utilisateurs */
				String infosUtil[] = infos.split("/");
				/* On va effectuer le traitement nécessaire pour chaques utilisateur */
				/* On vide dabord la liste des moderateurs */
				this.serveur.getCanal(idCanal).viderListeModerateurs();
				/* pour chaques utilisateur on regarde dabord s'il est banni (prioritaire) */
				int i;
				for(i=0;i<infosUtil.length;i++){
					if(Integer.parseInt(infosUtil[i].split("#")[2]) == 1){
						/* si il est banni on l'ajoute à la blacklist et on le supprime de la liste des connectés */
						this.serveur.getCanal(idCanal).getBlackList().add(this.serveur.getUtilisateur(Integer.parseInt(infosUtil[i].split("#")[0])));
						int j;
						for(j=0;j<this.serveur.getCanal(idCanal).getListeConnectes().size();j++){
							if(this.serveur.getCanal(idCanal).getListeConnectes().get(j).getId() ==Integer.parseInt(infosUtil[i].split("#")[0]) ){
								this.serveur.getCanal(idCanal).getListeConnectes().remove(j);
							}
						}
						
					}else{
						/* Si il n'est pas banni on regarde si il est modérateur et si oui on l'ajoute */
						if(Integer.parseInt(infosUtil[i].split("#")[1]) == 1){
							this.serveur.getCanal(idCanal).getListeModerateurs().add(this.serveur.getUtilisateur(Integer.parseInt(infosUtil[i].split("#")[0])));
						}
					}
				}
				envoyerMesg("1");
			}
			if(requeteClient == 12){ /* Demande des canaux ouverts pour un utilisateur */
				/* On cofirme la bonne réception de la demande */
				envoyerMesg("12");
				/* On récupère l'identifiant de l'utilisateur */
				int idUtil = Integer.parseInt(lireMesg());
				/* On parcours la liste des canaux et on cherche sil'utilisateur est connecté dessus */
				int i; /* indice de parcours de la liste des canaux */
				String listeCanaux = ""; /* chaine qui va contenir la liste des canaux sur lesquels l'utilisateur est connecté */
				for(i=0;i<this.serveur.getListeCanaux().size();i++){
					/* pour chaque canal on regarde si l'utilisateur est connecté dessus */
					if(this.serveur.getListeCanaux().get(i).isConnect(this.serveur.getUtilisateur(idUtil)) == true){
						/* L'utilisateur est connecté sur le canal , on ajoute le canal à la liste */
						listeCanaux += String.valueOf(this.serveur.getListeCanaux().get(i).getId())+"#"+this.serveur.getListeCanaux().get(i).getTitre()+"/";
					}
				}
				/* On envoi la liste au client */
				envoyerMesg(listeCanaux);
			}
			if(requeteClient == 13){ /* Demande de modification de mot de passe */
				/* on atteste bonne réception de la demande */
				envoyerMesg("13");
				/* On récupère les infos */
				String infos = lireMesg();
				int idUtil = Integer.parseInt(infos.split("/")[0]);
				String mdpOld = infos.split("/")[1];
				String mdpNew = infos.split("/")[2];
				/* on regarde si l'ancien mot de passe correspond */
				if(this.serveur.getUtilisateur(idUtil).getPassword().equals(mdpOld)){
					/* Les mot de passes correspondent on procède à la modification */
					this.serveur.getUtilisateur(idUtil).setPass(mdpNew);
					/* on envoi le message de confirmation */
					envoyerMesg("1");
				}else{
					/* les mot de passes ne correspondent pas */
					envoyerMesg("0");
				}
			}
			
		} /* fin de la boucle infinie */
		
	}

}
