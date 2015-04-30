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
    JTextArea zoneMessages;
    JTextField fieldTexte;

    Socket socket;
    String UserName;
    PrintWriter writer;
    BufferedReader reader;
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
        zoneMessages.setLineWrap(true);
        zoneMessages.setSize(432,160);
        JScrollPane zoneDefilement = new JScrollPane(zoneMessages);
        zoneDefilement.createHorizontalScrollBar();
        zoneDefilement.createVerticalScrollBar();
        zoneDefilement.setPreferredSize(new Dimension(zoneMessages.getWidth(),zoneMessages.getHeight()));
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
                if(socket != null && !socket.isClosed())
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
        JButton boutonConnexion  = new JButton("Connecter/Deconnecter");
        boutonConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (socket == null)
                    Connexion();
                else if(socket.isClosed())
                    Connexion();
                else
                    Deconnexion();
            }
        });

        JButton boutonQuitter = new JButton("Quitter");
        boutonQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(socket != null &&!socket.isClosed())
                    Deconnexion();
                System.exit(0);
            }
        });

        JPanel pan3 = new JPanel();
        pan3.add(boutonConnexion);
        pan3.add(boutonQuitter);
        add(pan3);
    }

    private void Deconnexion() {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                writer.println(UserName + " vient de quitter la conversation.");
                writer.flush();
                reader.close();
                writer.close();
                socket.close();
                return true;
            }
        };
        worker.execute();
    }

    public void Connexion()
    {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try{
                    if(!isAllWhitespace(fieldPseudo.getText()))
                    {
                        if(fieldPseudo.getText().length() > 8)
                            UserName =  fieldPseudo.getText().substring(0,8);
                        else
                            UserName = fieldPseudo.getText();
                    }
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

                    writer.println(UserName + " vient de se connecter");
                    writer.flush();

                    Lire();
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

    private void Lire() {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                try{
                    boolean Continuer = true;
                    while(socket != null && !socket.isClosed() && socket.isConnected() && Continuer){
                        String text = reader.readLine();
                        if(text != null)
                            zoneMessages.append(text + "\n");
                        else
                        {
                           zoneMessages.append("Le serveur ne répond plus." + "\n");
                           Continuer = false;
                           Deconnexion();
                        }
                        zoneMessages.setCaretPosition(zoneMessages.getText().length());
                    }
                }
                catch(IOException ioe)
                {
                    writer.println(UserName + " vient de quitter la conversation" + "\n");
                    writer.flush();
                    Deconnexion();
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
                        if(fieldTexte.getText().length() > 80)
                            writer.println(UserName + ": " + fieldTexte.getText().substring(0,80));
                        else
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