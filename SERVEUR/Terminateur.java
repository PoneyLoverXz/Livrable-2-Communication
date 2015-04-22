import java.io.*;

public class Terminateur implements Runnable
{
   public void run()
   {
      try
      {
		  // reader
         BufferedReader reader;
      
         reader = new BufferedReader( 
									new InputStreamReader( System.in ) );
		
		// Laisser en marche le serveur tant que l'utilisateur n'a pas appuyé sur Q
         while(!reader.readLine().trim().toUpperCase().equals("Q"))
         {
         
         }			
      
      }
      catch(IOException ioe)
      {
		System.out.println(ioe);
      }
   }
}