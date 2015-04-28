package clavardage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class Panneau extends JPanel {

    JTextField fieldAdresse;
    JTextField fieldPseudo;
    JCheckBox cboxResterConnecter;
    JTextArea zoneMessages;
    JTextField fieldTexte;

    public Socket socket;
    DataOutputStream dout;
    DataInputStream din;
    String UserName;
    PrintWriter writer;
    BufferedReader reader;
    boolean Connecte = false;

    public Panneau() {
        setLayout(new GridLayout(0, 1)); // une seule colonne

        // rangée 0
        JLabel labelAdresse = new JLabel("Adresse IP");
        fieldAdresse = new JTextField(16);
        JLabel labelPseudo = new JLabel("Pseudo");
        fieldPseudo = new JTextField(16);
        JCheckBox cboxResterConnecte = new JCheckBox("Rester connecté");
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
                Envoyer();
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
                Connexion();
            }
        });

        JButton boutonQuitter = new JButton("Quitter");
        boutonQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
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
                    UserName =  fieldPseudo.getText();
                    socket = new Socket(fieldAdresse.getText(), 50000);
                    writer = new PrintWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream() ));

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
                    for(int i = 0; i < 1000; i++){
                        zoneMessages.insert(reader.readLine() + "\n", zoneMessages.getText().length());
                        zoneMessages.setCaretPosition(zoneMessages.getText().length());
                        Thread.sleep(1000);
                    }
                    writer.close();
                    reader.close();
                    try                 { socket.close();      }
                    catch (Exception e) { e.printStackTrace(); }
                    System.err.println("Closed client socket");
                }
                catch(IOException ioe)
                {
                    System.err.println(ioe.getMessage());
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
                writer.println(UserName+ ": " + fieldTexte.getText());
                writer.flush();
                fieldTexte.setText("");
                fieldTexte.requestFocusInWindow();
                return true;
            }
        };


        worker.execute();
    }
}