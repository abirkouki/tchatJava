/**
 * 
 */
package serveur;

import client.Utilisateur;

/**
 * Un canal public est un canal accessible pour tout les membres.
 * @author florian
 *
 */
public class CanalPublic extends Canal{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construit un canal public
	 * @param titreCanal Titre du canal
	 * @param createur Créateur du canal
	 */
	public CanalPublic(int idCanal, String titreCanal, Utilisateur createur) {
		super(idCanal,titreCanal, createur);
	}

}
