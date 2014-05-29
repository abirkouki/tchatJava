package serveur;

import java.io.IOException;

import client.Utilisateur;

public class TestServeur {

	public static void main(String[] args) throws IOException {
		Serveur serv = new Serveur();
		serv.demarerServeur();
		int i;
		for(i=0;i<serv.getListeUtilisateurs().size();i++){
			System.out.println(serv.getListeUtilisateurs().get(i).getLogin());
		}
		/*Utilisateur user = serv.listeUtilisateurs.get(0);
		Canal canal = new Canal(0, "Par défaut", user);
		serv.listeCanaux.add(canal);
		serv.saveCanaux();
		int i;*/
		/*for(i=0;i<serv.listeCanaux.size();i++){
		System.out.println(serv.listeCanaux.get(i).getId()+" : "+serv.listeCanaux.get(i).getTitre());
		}*/
	}

}
