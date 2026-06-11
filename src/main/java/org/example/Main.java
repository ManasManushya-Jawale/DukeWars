package org.example;

import org.example.GameScreen;

import javax.swing.*;
import java.io.IOException;

public class Main {
    static JFrame frame = new JFrame();
    public static GameScreen panel;

    static {
        try {
            panel = new GameScreen(frame);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void main() {
        frame.setContentPane(panel);
        frame.setName("a manas-manushya product");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}