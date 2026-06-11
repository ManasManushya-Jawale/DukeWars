package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static org.example.Main.*;

public class DeathScreen extends JPanel {
    public DeathScreen() {

        super(new BorderLayout());
        add(new JButton("Restart"){{
            setFocusable(false);
            addActionListener(_ -> {
                try {
                    panel = new GameScreen(frame);
                    frame.setContentPane(panel);
                    frame.revalidate();
                    frame.repaint();
                    SwingUtilities.invokeLater(frame::requestFocusInWindow);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }}, BorderLayout.CENTER);
    }
}
