/**
 * Package des classes de l'application cliente.
 */
package client;

import java.io.Serializable;

/**
 * @author florian
 * Classe caractérisant un utilisateur de l'application cliente, caractérisé par son identifiant (attribué automatiquement à l'inscription), son nom, son prénom et son statut.
 */
public class Utilisateur implements Serializable {
	
	/**
	 * Attribut de Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Identifiant de l'utilisateur attribué à son inscription.
	 */
	private int id;
	
	/**
	 * Nom d'un utilisateur saisi durant l'inscription.
	 */
	private String nom;
	
	/**
	 * Prénom de l'utilisateur saisi durant l'inscription.
	 */
	private String prenom;
	
	/**
	 * Mot de passe d'un utilisateur nécessaire pour se connecter
	 */
	private String motDePasse;
	
	/**
	 * Statut d'un utilisateur
	 * 0:En ligne
	 * 1: Hors ligne
	 * 2:Occupé
	 * 3:Abscent
	 */
	private int statut;
	
	/**
	 * Justification du statut (Abscent).
	 */
	private String justification;
	
	/**
	 * Login de connexion d'un utilisateur
	 */
	private String login;
	

	/**
	 * Constrcuteur de la classe Utilisateur.
	 * Par défaut quand on créer un utilisateur son statut est "En ligne"
	 * @param id Identifiant (unique) qui représente un utilisateur, attribué automatiquement à l'inscription.
	 * @param nom Nom d'un utilisateur, saisi pendant l'inscription.
	 * @param prenom Prénom de l'utilisateur, saisi pendant l'inscription.
	 */
	public Utilisateur(int id,String login, String nom, String prenom, String motDePasse) {
		/* Par défaut le statut est "En ligne" -> 0 */
		this.statut = 0;
		/* L'utilisateur ne peut justifier que un statut "Abscent" -> 3, donc on initialise la justification à null */
		this.justification = null;
		/* On affecte aux autres attributs les valeurs passées en paramètre */
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.motDePasse = motDePasse;
		this.login = login;
		
	}

	
	/**
	 * Retourne le statut d'un utilisateur.
	 * @return Statut actuel de l'utilisateur.
	 */
	public int getStatut() {
		return statut;
	}

	/**
	 * Affecte un nouveau statut à un utilisateur.
	 * @param Nouveau statut de l'utilisateur.
	 */
	public void setStatut(int statut) {
		this.statut = statut;
	}

	/**
	 * Retourne la justification qu'un utilisateur a donné pour son statut "Abscent".
	 * @return Justification à un statut "Abscent".
	 */
	public String getJustification() {
		return justification;
	}

	/**
	 * Affecte une justification à un statut "Abscent".
	 * @param justification justification saisie par l'utilisateur suite au passage de son statut sur "Abscent".
	 */
	public void setJustification(String justification) {
		this.justification = justification;
	}

	/**
	 * Retourne l'identifiant de l'utilisateur qui a été attribué automatiquement à son inscription.
	 * @return Identifiant de l'utilisateur.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retourne le nom d'un utilisateur.
	 * @return Nom de l'utilisateur.
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Retourne le prénom de l'utilisateur.
	 * @return Prénom de l'utilisateur.
	 */
	public String getPrenom() {
		return prenom;
	}
	
	/**
	 * Retourne le login d'un utulisateur
	 * @return Login de l'utilisateur.
	 */
	public String getLogin(){
		return this.login;
	}
	

}
