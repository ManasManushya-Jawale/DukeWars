package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class DeathScreen extends JPanel {
    public DeathScreen(JFrame parent) {
        super();

        setLayout(new BorderLayout());
        add(new JButton("Restart"){{
            addActionListener(_ -> {
                try {
                    Main.frame.setContentPane(new GameScreen(Main.frame));
                    Main.frame.revalidate();
                    Main.frame.repaint();
                    SwingUtilities.invokeLater(Main.frame::requestFocusInWindow);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }}, BorderLayout.CENTER);
    }
}
