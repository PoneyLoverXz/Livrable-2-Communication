import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Connexion implements Runnable
{
	// Variables Client
	public Socket Client;
    public String Alias;
    public static ArrayList<Socket> Clients = new ArrayList<Socket>();
	
	// Concstructeur
	public Connexion(Socket client)
	{
		// Regarder le nombre de client connecter pour ne pas exceder la capacité de 5
	  Client = client;
	  if(Clients.size() > 4)
	  {
		System.out.println("Le client ne peut se connecter");
		Client = null;
	  }
	  else
		Clients.add(Client);
	}
	
	// Run
	public void run ()
	{
		try
		{
			if(Client != null)
			{
				// Variables de flux
				System.out.println("Client connecte");
				PrintWriter writer = null;
				BufferedReader reader = null;
				
				//Étape 3 : Obtention des flux
				writer = new PrintWriter(
							new OutputStreamWriter(
								Client.getOutputStream() ));
									
				reader = new BufferedReader(
							new InputStreamReader(
								Client.getInputStream() ));
									
				boolean fini = false;
				String ligne = null;
					
				 // Entrer l'Alias
				 writer.println("Entrez votre nom : ");
				 writer.flush();
				 Alias = reader.readLine();
				 if(Alias.length() > 8)
					Alias = Alias.substring(0,8);
				 else if(Alias.length() <= 0 )
					Alias = Client.getRemoteSocketAddress().toString();
				 Distribuer(Alias + " se joint a la conversation");
				 
				 // Attendre la lecture ou l'écriture tant que le client est connecté
				while (!fini)
				{
					ligne = reader.readLine();
					if(ligne != null)
					{
						// Distribuer la ligne si elle est valide
					   if(ligne.length() == 0)
					   {
						  System.out.println("client deconnecte");
						  fini = true;
						  Distribuer(Alias + " vient de quitter la conversation");
						  Clients.remove(Client);
					   }
					   else
					   {
						  if(ligne.length() >= 80)
							 ligne = ligne.substring(0,80);
						  
						  if(!isAllWhitespace(ligne))
							 Distribuer(Alias + " : " + ligne);
					   }
					}
					else
					{
					   System.out.println("client deconnecte");
					   fini = true;
					   Clients.remove(Client);
					}
				}	
				
				//Étape 5 : Fermeture des flux
				writer.close();
				reader.close();
				Client.close();
			}
		}
		catch(IOException ioe)
		{
			// Exception si le client n'est plus en train d'écrire
			if(!ioe.getMessage().equals("Read timed out"))
				System.out.println(ioe);
			else 
			{
				try{
				Client.close();
				}catch(IOException e){}
				Distribuer(Alias + " vient de quitter la conversation");
				Clients.remove(Client);
				System.out.println("Client deconnecte");
			}
		}
	}
   
   // Distribuer
   synchronized void Distribuer(String Ligne)
   {
	   // rechercher tous les clients pour afficher la ligne dans leurs fenetre
      for(int i = 0; i < Clients.size(); i++)
      {
         try
         {
            Socket c = (Socket)Clients.get(i);
            
			if(c != null)
			{
				PrintWriter w = null;
				
				w = new PrintWriter(
					new OutputStreamWriter(
						c.getOutputStream() ));
						
				if(w != null)
				{
				   w.println(Ligne);  
				   w.flush();
				}
            }
         }
         catch(IOException ioe)
         {
            //System.out.println(ioe);
         }   
      }
   }
   
   boolean isAllWhitespace(String s) 
   {
      boolean isAllWhitespace = true;
      for (int i = 0; i < s.length(); ++i) 
      {  
         if (!Character.isWhitespace(s.charAt(i)))
            isAllWhitespace = false;
      }
      return isAllWhitespace;
   }
}