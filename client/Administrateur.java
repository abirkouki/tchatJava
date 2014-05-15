/**
 * 
 */
package client;

/**
 * Classe caractérisant un utulisateur de type Administrateur, un administrateur possède des opérations supplémentaires sur l'application.
 * @author florian
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
		super(id, login, nom, prenom, motDePasse, grade);
	}

	

}
