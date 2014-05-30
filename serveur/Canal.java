/**
 * 
 */
package serveur;

import java.io.Serializable;
import java.util.ArrayList;

import client.Utilisateur;

/**
 * Un canal est un fil de discussion comportant un titre et une liste de modérateurs.
 * @author florian
 */
public class Canal implements Serializable{
	
	/**
	 * Identifiant automatique de sérialisation
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Titre du canal
	 */
	private String titreCanal;
	
	/**
	 * Créateur du canal, est le seul à pouvoir le supprimer ou exclure des modérateurs
	 */
	private Utilisateur createur;
	
	/**
	 * Identifiant du canal
	 */
	private int idCanal;
	
	/**
	 * Liste des modérateurs du canal
	 */
	private ArrayList<Utilisateur> listeModerateurs;
	
	/**
	 * Liste des utilisateurs connectés sur le canal actuellement
	 */
	private ArrayList<Utilisateur> listeUtilisateursConectes;
	
	/**
	 * Liste des messages du serveur
	 */
	private ArrayList<String> listeMessages;
	
	/**
	 * Liste des utilisateurs qui ont été bannis d'un canal
	 */
	private ArrayList<Utilisateur> blackList;
	
	/**
	 * Construit un canal en lui donnant un titre et en spécifiant qui en est le créateur
	 * @param idCanal Identifiant du canal
	 * @param titre Titre du canal, permet aux utilisateurs de connaitre le theme de la discussion
	 * @param createur Utilisateur qui a créer le canal.
	 */
	public Canal(int idCanal, String titreCanal, Utilisateur createur){
		this.createur = createur;
		this.titreCanal = titreCanal;
		this.idCanal = idCanal;
		this.listeUtilisateursConectes = new ArrayList<Utilisateur>();
		this.listeModerateurs = new ArrayList<Utilisateur>();
		this.listeModerateurs.add(createur);
		this.listeMessages = new ArrayList<String>();
		this.blackList = new ArrayList<Utilisateur>();
	}
	
	/**
	 * Accesseur sur l'identifiant d'un canal
	 * @return L'identifiant d'un canal
	 */
	public int getId(){
		return this.idCanal;
	}
	
	/**
	 * Accesseur de l'attribut titre d'un canal
	 * @return Le titre du canal
	 */
	public String getTitre(){
		return this.titreCanal;
	}
	
	/**
	 * Modifie le titre d'un canal
	 * @param titre Nouveau titre du canal
	 */
	public void setTitre(String titre){
		this.titreCanal = titre;
	}
	
	/**
	 * Change le titre d'un canal
	 * @param titre Nouveau titre du canal
	 */
	public void modifierTitre(String titre){
		this.titreCanal = titre;
	}
	
	/**
	 * Accesseur de l'attribut créateur d'un canal
	 * @return L'utilisateur qui a créé le canal.
	 */
	public Utilisateur getCreateur(){
		return this.createur;
	}

	/**
	 * Accesseur pour la liste des modérateurs d'un canal
	 * @return Liste des modérateurs du canal.
	 */
	public ArrayList<Utilisateur> getListeModerateurs(){
		return this.listeModerateurs;
	}
	
	/**
	 * Accesseurs sur la liste des clients qui sont connectés sur le canal
	 * @return Liste des clients connectés sur le canal
	 */
	public ArrayList<Utilisateur> getListeConnectes(){
		return this.listeUtilisateursConectes;
	}
	
	/**
	 * Accesseurs sur la liste des messages d'un canal
	 * @return Liste des messages qui ont été envoyés sur un canal
	 */
	public ArrayList<String> getLitseMessages(){
		return this.listeMessages;
	}
	
	/**
	 * Ajoute un message à la liste des messages d'un canal
	 * @param message Message d'un utilisateur sur le canal
	 */
	public void addMessage(String message){
		this.listeMessages.add(message);
	}
	
	/**
	 * Vide la liste des utilisateurs connectés sur le canal (pour le redémarage du serveur)
	 */
	public void viderListeConnectes(){
		this.listeUtilisateursConectes = new ArrayList<Utilisateur>();
	}
	
	/**
	 * Vérifie si un utilisateur a été bannis ou pas d'un canal
	 * @param idUtilisateur Identifiant de l'utilisateur.
	 * @return True si l'utilisateur a bien été banni, False si l'utilisateur n'a pas été banni.
	 */
	public Boolean isBanni(int idUtilisateur){
		/* On parcours la liste des utilisateurs bannis pour vérifier si l'utilisateur y est ou pas */
		int i; /* indice de parcours de la liste */
		for(i=0;i<this.blackList.size();i++){
			if(this.blackList.get(i).getId() == idUtilisateur){
				/* L'identifiant est bien dans la liste des bannis */
				return true;
			}
		}
		return false;
	}
}
