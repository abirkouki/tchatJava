/**
 * 
 */
package client;

import java.io.IOException;

public class TestClient {

	public static void main(String[] args) throws IOException {
		Utilisateur user = new Utilisateur(1,"Moulto", "Flo");
		ConnexionClient conCli = new ConnexionClient(user);
		
		conCli.connect();
		conCli.sockConnexion.close();
	}

}
