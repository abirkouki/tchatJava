package serveur;

public class TestServeur {

	public static void main(String[] args) {
		Serveur serv = new Serveur();
		ConnexionServeur conServ = new ConnexionServeur(serv);
		
		conServ.connect();

	}

}
