package clavardage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class Panneau extends JPanel {

    JTextField fieldAdresse;
    JTextField fieldPseudo;
    JCheckBox cboxResterConnecter;
    JTextArea zoneMessages;
    JTextField fieldTexte;

    Socket socket;
    DataOutputStream dout;
    DataInputStream din;
    String UserName;
    PrintWriter writer;
    BufferedReader reader;
    boolean Connecte = false;
    boolean ResterConnecter = false;

    int timeout = 5000 * 60;

    public Panneau() {
        setLayout(new GridLayout(0, 1)); // une seule colonne

        // rangée 0
        JLabel labelAdresse = new JLabel("Adresse IP");
        fieldAdresse = new JTextField(16);
        fieldAdresse.setText("127.0.0.1");
        JLabel labelPseudo = new JLabel("Pseudo");
        fieldPseudo = new JTextField(16);
        final JCheckBox cboxResterConnecte = new JCheckBox("Rester connecté");
        cboxResterConnecte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cboxResterConnecte.isSelected())
                {
                    ResterConnecter = true;
                    zoneMessages.append("ResterConnecter");
                }
                else
                {
                    ResterConnecter = false;
                }
            }
        });
        JPanel pan0 = new JPanel();
        add(pan0);
        pan0.add(labelAdresse);
        pan0.add(fieldAdresse);
        pan0.add(labelPseudo);
        pan0.add(fieldPseudo);
        pan0.add(cboxResterConnecte);

        // rangée 1
        zoneMessages = new JTextArea(20,40);
        zoneMessages.setEditable(false);
        JScrollPane zoneDefilement = new JScrollPane(zoneMessages);
        JPanel pan1 = new JPanel();
        add(pan0);
        pan1.add(zoneDefilement);
        add(pan1);

        // rangée 2
        JLabel labelTexte = new JLabel("Votre texte");
        fieldTexte = new JTextField(40);
        JButton boutonEnvoyer = new JButton("Envoyer");
        boutonEnvoyer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Connecte)
                {
                    Envoyer();
                }
            }
        });
        JPanel pan2 = new JPanel();
        pan2.add(labelTexte);
        pan2.add(fieldTexte);
        pan2.add(boutonEnvoyer);
        add(pan2);

        // rangée 3
        JButton boutonConnexion  = new JButton("Connecter");
        boutonConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Connecte) {
                    Connexion();
                }
            }
        });

        JButton boutonQuitter = new JButton("Quitter");
        boutonQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try
                {
                    if(Connecte)
                    {
                        writer.println(UserName + " vient de se deconnecter");
                        writer.flush();
                        reader.close();
                        socket.close();
                        writer.close();
                        Connecte = false;

                    }
                    System.exit(0);
                }
                catch(IOException s)
                {

                }
            }
        });

        JPanel pan3 = new JPanel();
        pan3.add(boutonConnexion);
        pan3.add(boutonQuitter);
        add(pan3);
    }

    public void Connexion()
    {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try{
                    if(!isAllWhitespace(fieldPseudo.getText()))
                        UserName =  fieldPseudo.getText();
                    else
                        UserName = Inet4Address.getLocalHost().getHostAddress();
                    socket = new Socket(fieldAdresse.getText(), 50000);
                    writer = new PrintWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream() ));

                    if(!ResterConnecter)
                        socket.setSoTimeout(timeout);

                    reader = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream() ));
                    Connecte = true;
                    writer.println(UserName + " vient de se connecter");
                    writer.flush();
                    listen();

                }
                catch(IOException ioe)
                {
                    zoneMessages.append("Le serveur n'est pas en marche ou l'adresse est invalide" + "\n");
                   return false;
                }
                return true;
            }
        };


        worker.execute();
    }

    private void listen() {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try{
                    String s;
                    while(Connecte){
                        zoneMessages.insert(reader.readLine() + "\n", zoneMessages.getText().length());
                        zoneMessages.setCaretPosition(zoneMessages.getText().length());
                    }
                }
                catch(IOException ioe)
                {
                    writer.println(UserName + " vient de se deconnecter" + "\n");
                    writer.flush();
                    zoneMessages.insert(UserName + " vient de se deconnecter" + "\n", zoneMessages.getText().length());
                    zoneMessages.setCaretPosition(zoneMessages.getText().length());
                    writer.close();
                    reader.close();
                    Connecte = false;

                    try { socket.close();
                    }
                    catch (Exception e) { e.printStackTrace(); }
                    return false;
                }
                return true;
            }
        };
        worker.execute();
    }

    private void Envoyer() {


            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    if (!isAllWhitespace(fieldTexte.getText())) {
                        writer.println(UserName + ": " + fieldTexte.getText());
                        writer.flush();
                        fieldTexte.setText("");
                        fieldTexte.requestFocusInWindow();
                    }
                    return true;
                }
            };
            worker.execute();
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