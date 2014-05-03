package serveur;

import java.io.IOException;

public class TestServeur {

	public static void main(String[] args) throws IOException {
		Serveur serv = new Serveur();
		serv.demarerServeur();
		//int i;
		//for(i=0;i<serv.listeUtilisateurs.size();i++){
		//	System.out.println(serv.listeUtilisateurs.get(i).getId()+" : "+serv.listeUtilisateurs.get(i).getLogin());
		//}
	}

}
