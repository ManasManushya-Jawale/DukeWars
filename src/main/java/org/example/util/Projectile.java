package org.example.util;

import java.awt.*;
import java.util.ArrayList;

public class Projectile {
    public int damage;
    public float speed = 1;

    public Rectangle rect = new Rectangle(0, 0, 48, 16);

    public Projectile(int damage) {
        this.damage = damage;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }

    public void update() {
        rect.x += 1;
    }
}
