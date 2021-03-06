/**
 * Package contenant les classes serveurs
 */
package serveur;

import client.Utilisateur;

/**
 * Un canal public est un canal accessible pour tous les membres.
 * @author STRI
 *
 */
public class CanalPublic extends Canal{

	/**
	 * Identifiant de sérialisation.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construit un canal public
	 * @param idCanal Identifiant du canal
	 * @param titreCanal Titre du canal
	 * @param createur Créateur du canal
	 */
	public CanalPublic(int idCanal, String titreCanal, Utilisateur createur) {
		super(idCanal,titreCanal, createur);
	}

}
