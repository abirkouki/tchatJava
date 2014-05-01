/**
 * 
 */
package serveur;

import java.util.ArrayList;

import client.Utilisateur;

/**
 * Un canal privé est un canal accessible que par les utilisateurs invités et les Administrateurs
 * @author florian
 *
 */
public class CanalPrive extends Canal {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4791033860115958541L;
	/**
	 * Liste des utilisateurs autorisés à rejoindre le canal
	 */
	private ArrayList<Utilisateur> listeInvite;
	
	/**
	 * Initialise un canal privé avec un créateur et un titre. La liste des utilisateurs invité est par défaut initialisé avec juste le créateur.
	 * @param titreCanal
	 * @param createur
	 */
	public CanalPrive(int idCanal, String titreCanal, Utilisateur createur) {
		super(idCanal,titreCanal, createur);
		this.listeInvite = new ArrayList<Utilisateur>();
		this.listeInvite.add(this.getCreateur());
	}

}
