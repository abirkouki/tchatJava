/**
 * Package contenant toutes les classes clientes
 */
package client;

/**
 * Classe caractérisant un utilisateur de type Administrateur, un administrateur possède des opérations supplémentaires sur l'application.
 * @author STRI
 *
 */
public class Administrateur extends Utilisateur {

	/**
	 * ID de sérialisation
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructeur d'un Administrateur.
	 * @param id Identifiant (unique) qui représente un utilisateur, attribué automatiquement à l'inscription.
	 * @param nom Nom d'un utilisateur, saisi pendant l'inscription.
	 * @param prenom Prénom de l'utilisateur, saisi pendant l'inscription.
	 * @param login Login de connexion de l'utilisateur
	 * @param motDePasse Mot de passe de l'utilisateur
	 * @param grade Grade de l'utilisateur
	 */
	public Administrateur(int id, String login, String nom, String prenom, String motDePasse, int grade) {
		/* On fait appel au constructeur parent */
		super(id, login, nom, prenom, motDePasse, grade);
	}
}
