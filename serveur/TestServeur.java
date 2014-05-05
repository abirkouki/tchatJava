package serveur;

import java.io.IOException;

public class TestServeur {

	public static void main(String[] args) throws IOException {
		Serveur serv = new Serveur();
		serv.demarerServeur();
		int i;
		/*for(i=0;i<serv.listeCanaux.size();i++){
		System.out.println(serv.listeCanaux.get(i).getId()+" : "+serv.listeCanaux.get(i).getTitre());
		}*/
	}

}
