import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Connexion implements Runnable
{
	// Variables Client
	public Socket Client;
    public static ArrayList<Socket> Clients = new ArrayList<Socket>();
	public static int ClientMax = 2;
	
	// Concstructeur
	public Connexion(Socket client)
	{
		// Regarder le nombre de client connecter pour ne pas exceder la capacit� de 5
	  Client = client;
        if(Clients.size() > ClientMax)
        {
            Client = null;
        }
        else
        {
            Clients.add(Client);
        }
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
				
				//�tape 3 : Obtention des flux
				writer = new PrintWriter(
							new OutputStreamWriter(
								Client.getOutputStream() ));
									
				reader = new BufferedReader(
							new InputStreamReader(
								Client.getInputStream() ));
									
				boolean fini = false;
				String ligne = null;
				 
				 // Attendre la lecture ou l'�criture tant que le client est connect�
				while (!fini)
				{
					ligne = reader.readLine();
					if(ligne != null)
					{
						  if(ligne.length() >= 80)
							 ligne = ligne.substring(0,80);
						  
						  if(!isAllWhitespace(ligne))
							 Distribuer(ligne);
							}
					else
					{
					   System.out.println("client deconnecte");
					   fini = true;
					   Clients.remove(Client);
					}
				}	
				
				//�tape 5 : Fermeture des flux
				writer.close();
				reader.close();
				Client.close();
			}
		}
		catch(IOException ioe)
		{
				try{
				Client.close();
				}catch(IOException e){}
				Clients.remove(Client);
				System.out.println("Client deconnecte");
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