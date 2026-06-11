package org.example.util.enemies;

import org.example.GameScreen;
import org.example.util.Projectile;
import org.example.util.Utils;
import org.w3c.dom.css.Rect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CMan extends Enemy {
    class CManProjectile extends Projectile {

        public BufferedImage image;
        public Point target;

        public double timePassed = 0;
        public int dumb;

        public CManProjectile(Point target, int dumbLim) {
            super(50);
            this.image = Utils.getImage("/sprites/Projectiles.png");
            rect.width = image.getWidth(); rect.height = image.getHeight();
            dumb = new Random().nextInt(-dumbLim, dumbLim);

            this.target = new Point(target.x,
                    target.y);
        }

        @Override
        public void update() {
        }
    }

    public List<CManProjectile> projectiles = new ArrayList<>();
    private double elapsed_Time = 0;

    public double time_passed = 0;
    public boolean crashout = false;

    Point duke;
    int dumb;
    int distLim;

    public CMan(GameScreen parent) {
        super("/sprites/CMan.png", 1, 0.5f, parent);
        dumb = new Random().nextInt(0, 90);
        distLim = new Random().nextInt(100, 500);
    }

    @Override
    public void draw(Graphics g, ImageObserver self) {

        Graphics2D g2d = ((Graphics2D) g);
        AffineTransform transform = new AffineTransform();
        transform.translate(rect.x, rect.y);
        transform.rotate(getAngle(rect.getBounds().getLocation(), duke.getLocation())-135);

        g2d.drawImage(image, transform, self);

        for (CManProjectile projectile : projectiles) {
            AffineTransform transform1 = new AffineTransform();
            transform1.translate(projectile.rect.x, projectile.rect.y);
            transform1.translate((double) -projectile.rect.width / 2, (double) -projectile.rect.height / 2);
            transform1.scale(2, 2);
            g2d.drawImage(projectile.image, transform1, self);
        }
    }

    public static double getAngle(Point from, Point to) {
        return Math.atan2(to.y - from.y, to.x - from.x);
    }

    @Override
    public void update(Rectangle duke) {
        this.duke = duke.getLocation();

        if (crashout) crashOut(duke);
        else normalAttacks(duke);

        projectiles.removeIf(p ->
                p.rect.x < 0 || p.rect.x > parent.getWidth() || p.rect.y < 0 || p.rect.y > parent.getHeight() ||
                p.timePassed > 1);
    }

    boolean previousState = false;
    public void crashOut(Rectangle duke) {
        if (crashout != previousState) {
            while (bullets >= 1) {
                spawnProjectile(duke);
                bullets--;
            }
        }

        float targetX = duke.x + 69;
        float targetY = duke.y;

        float dx = targetX - rect.x;
        float dy = targetY - rect.y;

        rect.x += dx * 1 / 100 * speed * 1.5f;
        rect.y += dy * 1 / 100 * speed * 1.5f;

        previousState = true;

        for (CManProjectile projectile : projectiles) {

            projectile.timePassed += parent.spawner_delta;

            Utils.aPathFind(projectile.target, projectile.speed*2, projectile.rect, projectile.dumb);

            if (projectile.rect.intersects(duke)) {
                parent.kill();
            }
        }
    }

    public void normalAttacks(Rectangle duke) {

        double dist = Math.sqrt(
                Math.pow((duke.x - rect.x), 2) +
                        Math.pow((duke.y - rect.y), 2)
        );

        if (dist >= distLim) {
            Utils.pathFind(this.duke, speed, rect);
        }

        for (CManProjectile projectile : projectiles) {

            projectile.timePassed += parent.spawner_delta;

            Utils.aPathFind(projectile.target, projectile.speed*2, projectile.rect, projectile.dumb);

            if (projectile.rect.intersects(duke)) {
                parent.kill();
            }
        }
    }

    double time = 1.5;
    int bullets = 15;

    @Override
    public void spawnChild(Rectangle duke, double spawn_Delta) {
        elapsed_Time += spawn_Delta;
        time_passed += spawn_Delta;

        if (elapsed_Time >= time && bullets >= 1) {
            elapsed_Time = 0;
            spawnProjectile(duke);

            bullets--;
        }
    }

    public void spawnProjectile(Rectangle duke) {
        CManProjectile p = new CManProjectile(duke.getLocation(), dumb);
        p.rect.x = rect.x;
        p.rect.y = rect.y;
        p.speed = 1.5f;
        projectiles.add(p);
    }
}
