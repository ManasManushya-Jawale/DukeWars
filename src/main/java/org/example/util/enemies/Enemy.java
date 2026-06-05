package org.example.util.enemies;

import org.example.GameScreen;
import org.example.util.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Random;
public abstract class Enemy {
    BufferedImage image;
    public Rectangle rect;

    float fx, fy;

    int damage;
    float speed;
    public int health = 100;

    GameScreen parent;

    public Enemy(String resourcePath, int damage, float speed, GameScreen parent) {
        try {
            this.image = ImageIO.read(getClass().getResource(resourcePath).openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.damage = damage;
        this.speed = speed;
        this.parent = parent;
        rect = new Rectangle(parent.getWidth(), new Random().nextInt(0, 600), image.getWidth(), image.getHeight());
        fx = parent.getWidth();
        fy = rect.y;
    }

    public void draw(Graphics g, ImageObserver self) {
        g.drawImage(image, rect.x, rect.y, self);
    }

    public abstract void update(Rectangle duke);
    public abstract void spawnChild(Rectangle duke, double spawn_Delta);
}