package clavardage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Client {
    private static void creerEtAfficherIug() {
        JFrame frame = new JFrame("Client de clavardage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = frame.getContentPane();
        Panneau panneau = new Panneau();
        c.add(panneau);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                creerEtAfficherIug();
            }
        });
    }
}