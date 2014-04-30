package serveur;

import java.io.IOException;

public class TestServeur {

	public static void main(String[] args) throws IOException {
		Serveur serv = new Serveur();
		serv.demarerServeur();
		//System.out.println("utilsiateur : "+serv.listeUtilisateurs.get(0).getId()+" "+serv.listeUtilisateurs.get(0).getNom()+" "+serv.listeUtilisateurs.get(0).getPrenom()+" "+serv.listeUtilisateurs.get(0).getLogin());
		/*String ch = "florian/prieto/moulto/marielove11";
		String[] chDecomp = ch.split("/");
		System.out.println(chDecomp[0]);*/
	}

}
