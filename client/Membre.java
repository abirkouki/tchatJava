/**
 * Package contenant toutes les classes clients
 */
package client;

/**
 * Un membre est un utilisateur qui est identifié sur l'application, il a donc réalisé une inscription au préalable.
 * @author STRI
 */
public class Membre extends Utilisateur {

	/**
	 * Attribut de Serializable
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructeur de la classe Membre
	 * @param id Identifiant (unique) qui représente un utilisateur, attribué automatiquement à l'inscription.
	 * @param nom Nom d'un utilisateur, saisi pendant l'inscription.
	 * @param prenom Prénom de l'utilisateur, saisi pendant l'inscription.
	 * @param login Login de connexion de l'utilisateur
	 * @param motDePasse Mot de passe de l'utilisateur
	 * @param grade Grade de l'utilisateur
	 */
	public Membre(int id, String login, String nom, String prenom, String motDePasse, int grade) {
		/* On fait appel au constructeur de la classe supérieure */
		super(id, login, nom, prenom, motDePasse, grade);
	}
}
