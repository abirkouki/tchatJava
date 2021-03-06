/**
 * Package contenant toutes les classes pour le serveur du projet.
 */
package serveur;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;

import client.Utilisateur;

/**
 * Classe permettant de gérer le serveur du mini tchat
 * @author STRI
 */
public class Serveur {
	
	/**
	 * Socket du serveur qui va écouter sur le port donné en paramètre du constructeur.
	 * Par défault on l'initialise à null.
	 */
	private ServerSocket sockServeur = null;
	
	/**
	 * Thread permettant la connexion de plusieurs clients en simultané
	 */
	private Thread thConnexion = null;
	
	/**
	 * Liste des utilisateurs inscrits sur l'application
	 */
	protected ArrayList<Utilisateur> listeUtilisateurs;
	
	/**
	 * Liste des utilisateurs connectés sur l'application
	 */
	protected ArrayList<Utilisateur> listeConnectes;
	
	/**
	 * Liste des canaux présents sur le serveur
	 */
	protected ArrayList<Canal> listeCanaux;
	
	/**
	 * Liste des utilisateurs qui ont été banni de l'application
	 */
	private ArrayList<Utilisateur> blackList;
	
	/**
	 * Démarre le serveur avec toutes ses initialisations
	 */
	public void demarerServeur(){
		/* On essaye de connecter le serveur sur le numéro de port demandé */
		try{
			/* On initialise la liste des utilisateurs inscrits */
			this.initUtilisateurs();
			/* On initialise la liste des canaux sauvegardés */
			this.initCanaux();
			/* on initialise la liste des utilisateurs bannis */
			this.initBlackList();
			/* On crée une liste vide des utilisateurs connectés */
			this.listeConnectes = new ArrayList<Utilisateur>();
			/* on vide la liste des connectés sur les canaux */
			int i;
			for(i=0;i<this.listeCanaux.size();i++){
				this.listeCanaux.get(i).viderListeConnectes();
			}
			/* On initialise le socket avec le numéro de port fourni en paramètre */
			this.sockServeur = new ServerSocket(2369);
			/* On affiche un message pour informer du succés de la création de le socket */
			System.out.println("Création de le socket sur le port 2369 -> OK");
			/* On créer ensuite le thread qui va rediriger sur la classe ConnexionServeur */
			this.thConnexion = new Thread(new ConnexionServeur(this.sockServeur, this));
			/* On lance le thread */
			this.thConnexion.start();
		}
		/* Si la connexion échoue, on affiche un message pour informer que le port est indisponible */
		catch(IOException exception){
			System.out.println("ERREUR : Le port 2369 est actuellement indisponible");
			exception.printStackTrace();
		}
	}
	
	/**
	 * Accesseur sur le socket du serveur
	 * @return Socket du serveur.
	 */
	public ServerSocket getSock(){
		return this.sockServeur;
	}
	
	/* Fonctions sur la liste des utilisateurs */
	
	/**
	 * Initialise le serveur avec les données sauvegardées dans les fichiers de sauvegarde.
	 */
	public void initUtilisateurs() {
		this.listeUtilisateurs = new ArrayList<Utilisateur>();
		ObjectInputStream entree;
		try{
			entree = new ObjectInputStream(new FileInputStream("saveUtilisateurs.dat"));
			ArrayList<Utilisateur> readObject = (ArrayList<Utilisateur>) entree.readObject();
			this.listeUtilisateurs = readObject;
			entree.close();
			System.out.println("Initialisation de la liste des utilisateurs -> OK");
		}
		catch(ClassNotFoundException c)
		{
			System.out.println("ERREUR initialisation de la liste des utilisateurs -> Problème de classe");
			c.printStackTrace();
		}
		catch(FileNotFoundException f)
		{
			System.out.println("ERREUR initialisation de la liste des utilisateurs -> Fichier introuvable");
			f.printStackTrace();
		}
		catch(IOException e)
		{
			System.out.println("ERREUR initialisation de la liste des utilisateurs -> KO");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Sauvegarde la liste des utilisateurs inscrits sur l'application dans un fichier de datas. Retourne -1 en cas d'échec
	 */
	public int saveUtilisateurs(){
		ObjectOutputStream sortie;
		try{
			sortie = new ObjectOutputStream(new FileOutputStream("saveUtilisateurs.dat"));
			sortie.writeObject(this.listeUtilisateurs);
			System.out.println("Sauvegarde de la liste des utilisateurs -> OK");
			sortie.close();
			return 0;
		}
		catch (FileNotFoundException fileexcept){
			System.out.println("Fichier introuvable pour la sauvegarde des utilisateurs");
			return -1;
		}
		catch (IOException exception){
			System.out.println("Sauvegarde de la liste des utilisateurs -> KO");
			exception.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Retourne un utilisateur de la liste des utilisateurs inscrits
	 * @param idUtilisateur Identifiant de l'utilisateur recherché
	 * @return Utilisateur correspondant à l'identifiant passé en paramètre.
	 */
	public Utilisateur getUtilisateur(int idUtilisateur){
		return this.listeUtilisateurs.get(idUtilisateur);
	}
	
	/**
	 * Ajoute un utilisateur dans la liste
	 * @param id Identifiant de l'utilisateur qui va être ajouté
	 * @param utilisateur Utilisateur qui va être ajouté à la liste
	 */
	public void addUtilisateur(int id, Utilisateur utilisateur){
		this.listeUtilisateurs.add(id,utilisateur);
	}
	
	/**
	 * Retourne le nombre d'utilisateurs inscrits
	 * @return Nombre d'utilisateurs inscrits sur l'application.
	 */
	public int getNbUtilisateurs(){
		return this.listeUtilisateurs.size();
	}
	
	/**
	 * Renvoi la liste des utilisateurs
	 * @return Liste des utilisateurs
	 */
	public ArrayList<Utilisateur> getListeUtilisateurs(){
		return this.listeUtilisateurs;
	}
	
	/* Fonctions sur la liste des canaux */
	
	/**
	 * Initialise le serveur avec les canaux sauvegardés dans le fichier de sauvegarde
	 */
	public void initCanaux() {
		this.listeCanaux = new ArrayList<Canal>();
		ObjectInputStream entree;
		try{
			entree = new ObjectInputStream(new FileInputStream("saveCanaux.dat"));
			ArrayList<Canal> readObject = (ArrayList<Canal>) entree.readObject();
			this.listeCanaux = readObject;
			entree.close();
			System.out.println("Initialisation de la liste des Canaux -> OK");
		}
		catch(ClassNotFoundException c)
		{
			System.out.println("ERREUR initialisation de la liste des Canaux -> Problème de classe");
			c.printStackTrace();
		}
		catch(FileNotFoundException f)
		{
			System.out.println("ERREUR initialisation de la liste des Canaux -> Fichier introuvable");
			f.printStackTrace();
		}
		catch(IOException e)
		{
			System.out.println("ERREUR initialisation de la liste des Canaux -> KO");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Sauvegarde la liste des canaux présents sur l'application dans un fichier de datas. Retourne -1 en cas d'échec
	 */
	public int saveCanaux(){
		ObjectOutputStream sortie;
		try{
			sortie = new ObjectOutputStream(new FileOutputStream("saveCanaux.dat"));
			sortie.writeObject(this.listeCanaux);
			System.out.println("Sauvegarde de la liste des Canaux -> OK");
			sortie.close();
			return 0;
		}
		catch (FileNotFoundException fileexcept){
			System.out.println("Fichier introuvable pour la sauvegarde des Canaux");
			return -1;
		}
		catch (IOException exception){
			System.out.println("Sauvegarde de la liste des Canaux -> KO");
			exception.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Ajoute un canal à la liste des canaux
	 * @param canal Canal qui va être ajouté à la liste
	 */
	public void addCanal(Canal canal){
		this.listeCanaux.add(canal.getId(),canal);
	}
	
	/**
	 * Accesseur sur la liste des canaux
	 * @return La liste des canaux qui ont été créé sur le serveur
	 */
	public ArrayList<Canal> getListeCanaux(){
		return this.listeCanaux;
	}
	
	public Canal getCanal(int idCanal){
		int i;
		for(i=0;i<this.listeCanaux.size();i++){
			if(this.listeCanaux.get(i).getId() == idCanal){
				return this.listeCanaux.get(i);
			}
		}
		return null;
	}

	
	/* Fonctions sur la liste des utilisateurs connectés */
	
	/**
	 * Ajoute un utilisateur à la liste des connectés
	 * @param utilisateur Utilisateur qui vient de réaliser une connexion
	 */
	public void addConnecte(Utilisateur utilisateur){
		this.listeConnectes.add(utilisateur);
	}
	
	/**
	 * Supprime un utilisateur de la liste des connectés
	 * @param utilisateur Utilisateur qui vient de se déconnecter
	 */
	public void delConnecte(Utilisateur utilisateur){
		int i;
		for(i=0;i<this.listeConnectes.size();i++){
			if(this.listeConnectes.get(i).getId() == utilisateur.getId()){
				this.listeConnectes.remove(i);
			}
		}
	}
	
	/**
	 * Renvoi la liste des utilisateurs conectés
	 * @return Liste des utilisateurs connectés sur l'application.
	 */
	public ArrayList<Utilisateur> getListeConnecte(){
		return this.listeConnectes;
	}
	
	/* Fonctions sur la liste des utilisateurs bannis */
	
	/**
	 * Initialise la liste des utilisateurs bannis
	 */
	public void initBlackList() {
		this.blackList = new ArrayList<Utilisateur>();
		ObjectInputStream entree;
		try{
			entree = new ObjectInputStream(new FileInputStream("blackList.dat"));
			ArrayList<Utilisateur> readObject = (ArrayList<Utilisateur>) entree.readObject();
			this.blackList = readObject;
			entree.close();
			System.out.println("Initialisation de la black liste -> OK");
	
		}
		catch(ClassNotFoundException c)
		{
			System.out.println("ERREUR initialisation de la black liste -> Problème de classe");
			c.printStackTrace();
			
		}
		catch(FileNotFoundException f)
		{
			System.out.println("ERREUR initialisation de la black liste -> Fichier introuvable");
			f.printStackTrace();
	
		}
		catch(IOException e)
		{
			System.out.println("ERREUR initialisation de la black liste -> KO");
			e.printStackTrace();
	
		}
	}
	
	/**
	 * Sauvegarde la liste des utilisateurs bannis
	 * @return 0 si la sauvegarde a bien été réalisée, -1 si il y a une erreur avec la sauvegarde.
	 */
	public int saveBlackListe(){
		ObjectOutputStream sortie;
		try{
			sortie = new ObjectOutputStream(new FileOutputStream("saveUtilisateurs.dat"));
			sortie.writeObject(this.blackList);
			System.out.println("Sauvegarde de la liste des utilisateurs bannis -> OK");
			sortie.close();
			return 0;
		}
		catch (FileNotFoundException fileexcept){
			System.out.println("Fichier introuvable pour la sauvegarde des utilisateurs bannis");
			return -1;
		}
		catch (IOException exception){
			System.out.println("Sauvegarde de la liste des utilisateurs bannis -> KO");
			exception.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Permet de savoir si un utilisateur a été banni de l'application ou pas
	 * @param utilisateur Utilisateur pour lequel on souhaite réaliser la vérification
	 * @return True si l'utilisateur a été banni et False sinon.
	 */
	public Boolean isBanni(Utilisateur utilisateur){
		int i;
		for(i=0;i<this.blackList.size();i++){
			if(utilisateur.equals(this.blackList.get(i))){
				return true;
			}
		}
		return false;
	}
}
