import java.io.*;
import java.net.*;

public class ServeurClavardage
{
	// LancerServeur
	public void lancerServeur(int Port)
	{
		//Variables Serveur
		Socket client = null;
		ServerSocket socketServeur = null;
		boolean enService = true;
	
		try
		{
			//Étape 1 : Création du socket serveur
			socketServeur = new ServerSocket (Port);
			socketServeur.setSoTimeout(1000);
			//Étape 2 : Attente de la connexion
			System.out.println("Serveur Clavardage en attente d'une connexion");
         
			// Partir le tread terminateur pour entrer q d pour arreter le serveur
			Terminateur terminateur = new Terminateur();
			Thread ter = new Thread(terminateur);
			ter.setDaemon(true);
			ter.start();
			
			// Laisser le serveur en marche tant que l'utilisateur n'a pas confirmer qu'il veut l'éteindre
			while(enService)
			{
				if(!ter.isAlive())
				   enService = false;
				else
				{
					// Connexion d'un client
				   try
				   {
					  client = socketServeur.accept();
					  client.setSoTimeout(20*1000);
					  Connexion connexion = new Connexion(client);
					  Thread t = new Thread( connexion );
					  t.setDaemon(true);
					  t.start();
				   }
				   catch(SocketTimeoutException s)
				   {
					   // Le client n'est plus actif
					   if(!s.getMessage().equals("Accept timed out"))
					   {
							client.close();
							//t.interrupt();
							System.out.println("Client deconnecte");
						}
				   }
				}
			}	
			// Fermer le client
         if(client != null)
            client.close();
		
		// Fermer les Sockets
		 socketServeur.close();
         System.exit(1);
		}
		catch (IOException ioe)
		{
			System.err.println(ioe);
			System.exit(1);
		}
	}
	
	// Main
	
}