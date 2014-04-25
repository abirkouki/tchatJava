package serveur;

import java.io.*;
import java.net.*;
public class Serv extends Object {
  public static void main (String args[]) {
	Requete req= new Requete();;
	
    ServerSocket socketEcoute;
    Socket socketService;
    InputStream entreeSocket;
    OutputStream sortieSocket;
    BufferedReader bufferRead;
    PrintStream bufferWrite;
    try {
      // creation du socket d ecoute (port numero 7)
      socketEcoute = new ServerSocket(4242);
      while (true) {
        // attente d une demande de connexion
        socketService = socketEcoute.accept();
        System.out.println("Nouvelle connexion : " + socketService);
        // recuperation des flux d entree/sortie de la socket de service
        entreeSocket = socketService.getInputStream();
        sortieSocket = socketService.getOutputStream();
        try {
          bufferRead = new BufferedReader(new InputStreamReader(socketService.getInputStream()));          
          req.decoupe(bufferRead.readLine());
          Traitement t = new Traitement(req.fichier, req.version);
          System.out.println("fichier : "+req.fichier);
          if(req.nom.equals("GET"))
	    t.reponse(socketService);
	  else{ 
	    if (req.nom.equals("PUT"))
	      t.ajout(socketService);
	  }
          //b = entreeSocket.read();  
          //sortieSocket.write(b);
          // while
          System.out.println("Fin de connexion");
          
        } // try
        catch (IOException ex)
        {
          // fin de connexion
          System.out.println("Fin de connexion : "+ex);
          ex.printStackTrace();
        }
        socketService.close();
      } // while (true)
    } // try
    catch (Exception ex)
    {
      // erreur de connexion
      System.err.println("Une erreur est survenue : "+ex);
      ex.printStackTrace();
    }
  } // main
} // class