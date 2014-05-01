package serveur;

import java.io.IOException;

public class TestServeur {

	public static void main(String[] args) throws IOException {
		Serveur serv = new Serveur();
		serv.demarerServeur();
		//System.out.println("utilsiateur : "+serv.listeUtilisateurs.get(1).getId()+" "+serv.listeUtilisateurs.get(1).getNom()+" "+serv.listeUtilisateurs.get(1).getPrenom()+" "+serv.listeUtilisateurs.get(1).getLogin());
		/*String ch = "florian/prieto/moulto/marielove11";
		String[] chDecomp = ch.split("/");
		System.out.println(chDecomp[0]);*/
	}

}
