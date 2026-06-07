package org.example;

import org.example.GameScreen;

import javax.swing.*;
import java.io.IOException;

public class Main {
    static JFrame frame = new JFrame();
    void main() throws IOException {
        frame.setContentPane(new GameScreen(frame));
        frame.setName("a manas-manushya product");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}