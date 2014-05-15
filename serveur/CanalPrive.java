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
	 * Identifiant de sérialisation.
	 */
	private static final long serialVersionUID = 4791033860115958541L;
	
	/**
	 * Liste des utilisateurs autorisés à rejoindre le canal
	 */
	private ArrayList<Utilisateur> listeInvite;
	
	/**
	 * Initialise un canal privé avec un créateur et un titre. La liste des utilisateurs invité est par défaut initialisé avec juste le créateur.
	 * @param titreCanal Titre du canal
	 * @param createur Utilisateur qui a créé le canal
	 * @param idCanal Identifiant du canal
	 * @param listeInvite Liste des utilisateurs qui pourront accèder au canal.
	 */
	public CanalPrive(int idCanal, String titreCanal, Utilisateur createur, ArrayList<Utilisateur> listeInvite) {
		super(idCanal,titreCanal, createur);
		this.listeInvite = listeInvite;
		this.listeInvite.add(this.getCreateur());
	}
	
	/**
	 * Vérifie si un utilisateur est invité sur un canal de type privé
	 * @param idUtilisateur Identifiant de l'utilisateur souhaitant rejoindre le canal
	 * @return True si l'utilisateur est bien dans la liste des invité ou False si l'utilisateur n'est pas dans la liste des invités
	 */
	public Boolean isInvite(int idUtilisateur){
		int i; /* indice de parcours de la liste des invités */
		/* On parcours toute la liste à la recherche de l'utilisateur passé en paramètre */
		for(i=0;i<this.listeInvite.size();i++){
			/* on compare l'identifiant en paramètre avec ceux des utilisateurs de la liste */
			if(this.listeInvite.get(i).getId() == idUtilisateur){
				/* On a trouvé l'utilisateur, il est donc bien invité sur le canal */
				return true;
			}
		}
		/* L'utilisateur n'est pas présent dans la liste */
		return false;
	}

}
