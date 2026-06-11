package org.example.util.enemies;

import jdk.jshell.execution.Util;
import org.example.GameScreen;
import org.example.util.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Random;
public abstract class Enemy {
    BufferedImage image;
    public Rectangle.Float rect;

    int damage;
    float speed;
    public int health = 100;

    GameScreen parent;

    public Enemy(String resourcePath, int damage, float speed, GameScreen parent) {
        this.image = Utils.getImage(resourcePath);
        this.damage = damage;
        this.speed = speed;
        this.parent = parent;
        rect = new Rectangle.Float(parent.getWidth(), new Random().nextInt(0, 600), image.getWidth(), image.getHeight());
    }

    public void draw(Graphics g, ImageObserver self) {
        g.drawImage(image, Math.round(rect.x), Math.round(rect.y), self);
    }

    public abstract void update(Rectangle duke);
    public abstract void spawnChild(Rectangle duke, double spawn_Delta);
}