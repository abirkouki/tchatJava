/**
 * Package des classes de l'application cliente.
 */
package client;

/**
 * @author florian
 * Classe caractérisant un utilisateur de l'application cliente, caractérisé par son identifiant (attribué automatiquement à l'inscription), son nom, son prénom, son grade et son statut.
 */
public class Utilisateur {
	
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
	 * Grade d'un utilisateur qui lui est attribué par les administrateurs.
	 * 0 : utilisateur basique
	 * 1 : utilisateur avancé
	 * 2 : Administrateur
	 */
	private int grade;
	
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
	 * Constrcuteur de la classe Utilisateur.
	 * Par défaut quand on créer un utilisateur son statut est "En ligne"
	 * @param id Identifiant (unique) qui représente un utilisateur, attribué automatiquement à l'inscription.
	 * @param nom Nom d'un utilisateur, saisi pendant l'inscription.
	 * @param prenom Prénom de l'utilisateur, saisi pendant l'inscription.
	 * @param grade Grade d'un utilisateur, attribué par les Administrateurs, à l'inscription le grade est le plus bas (0).
	 */
	public Utilisateur(int id,String nom, String prenom, int grade) {
		/* Par défaut le statut est "En ligne" -> 0 */
		this.statut = 0;
		/* L'utilisateur ne peut justifier que un statut "Abscent" -> 3, donc on initialise la justification à null */
		this.justification = null;
		/* On affecte aux autres attributs les valeurs passées en paramètre */
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.grade = grade;
		
	}

	/**
	 * Retourne le grade d'un utilisateur.
	 * @return Grade de l'utilisateur.
	 */
	public int getGrade() {
		return grade;
	}

	/**
	 * Affecte un nouveau grade à l'utilisateur.
	 * @param grade Nouveau grade de l'utilisateur.
	 */
	public void setGrade(int grade) {
		this.grade = grade;
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
	
	

}
