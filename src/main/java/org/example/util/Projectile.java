package org.example.util;

import java.awt.*;

public class Projectile {
    public int damage;
    public float speed = 1;

    public Rectangle.Float rect = new Rectangle.Float(0, 0, 48, 16);

    public Projectile(int damage) {
        this.damage = damage;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(Math.round(rect.x), Math.round(rect.y), Math.round(rect.width), Math.round(rect.height)) ;
    }

    public void update() {
        rect.x += 1;
    }
}
