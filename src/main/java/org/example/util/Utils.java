package org.example.util;

import org.example.GameScreen;
import org.example.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Utils {
    public static GameScreen panel;
    public static void setPanel(GameScreen p) {
        panel = p;
    }

    public static void pathFind(Point target, float speed, Rectangle.Float rect) {
        float targetX = target.x;
        float targetY = target.y;

        float dx = (float) (targetX - rect.getX());
        float dy = (float) (targetY - rect.getY());

        rect.x += (dx * (1/100f) * speed);
        rect.y += (dy * (1/100f) * speed);
    }

    public static void aPathFind(Point target, float speed, Rectangle.Float rect, int dumb) {
        float dx = target.x - rect.x;
        float dy = target.y - rect.y;

        double angle = Math.atan2(dy, dx) + Math.toRadians(dumb); // angle toward target in radians

        rect.x += (float) (Math.cos(angle) * speed);
        rect.y += (float) (Math.sin(angle) * speed);
    }

    public static void setAngle(float angle) {
        panel.angle = Math.toRadians(angle);
    }

    public static void setKeyBind(String name, String[] key, Runnable action) {
        for (String s : key) {
            panel.im.put(KeyStroke.getKeyStroke(s), name);
        }
        panel.am.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    public static BufferedImage getImage(String resourcePath) {
        try {
            return ImageIO.read(Utils.class.getResource(resourcePath).openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
