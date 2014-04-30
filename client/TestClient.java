/**
 * 
 */
package client;

import java.io.IOException;
import java.util.Scanner;

public class TestClient {

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		int choix;
		System.out.println("Bienvenue dans la version test du tchat STRI");
		do{
			System.out.println("Vous devez vous connecter ou vous inscrire, que voulez vous faire : ");
			System.out.println("1 : Connexion");
			System.out.println("2 : Inscription");
			System.out.println("0 : Quitter");
			choix = sc.nextInt();
		}while(choix>2 || choix<0);
		switch (choix) {
		case 0:
			System.exit(0);
			break;
		case 1:
			
			break;
		case 2:
			ConnexionClient conCli = new ConnexionClient();
			conCli.connect(2);
			break;

		default:
			break;
		}
	}

}
