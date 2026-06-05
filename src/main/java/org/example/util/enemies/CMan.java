package org.example.util.enemies;

import org.example.GameScreen;
import org.example.util.Projectile;
import org.w3c.dom.css.Rect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CMan extends Enemy {
    class CManProjectile extends Projectile {
        public double angle = 0;
        public float pfy=0, pfx=0;
        public float rotation = 0;

        public BufferedImage image;
        public Point target;

        public double timePassed = 0;

        public CManProjectile(double angle, Point target) {
            super(50);
            try {
                this.image = ImageIO.read(getClass().getResource("/sprites/Projectiles.png").openStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            rect.setSize(image.getWidth(), image.getHeight());

            this.angle = angle;
            this.target = target;
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

    public CMan(GameScreen parent) {
        super("/sprites/CMan.png", 1, 0.5f, parent);
    }

    @Override
    public void draw(Graphics g, ImageObserver self) {

        Graphics2D g2d = ((Graphics2D) g);
        AffineTransform transform = new AffineTransform();
        transform.translate(rect.x, rect.y);
        transform.rotate(getAngle(rect.getLocation(), duke.getLocation())-135);
//        transform.translate((double) -rect.width / 2, (double) -rect.height / 2);

        g2d.drawImage(image, transform, self);

        for (CManProjectile projectile : projectiles) {
            AffineTransform transform1 = new AffineTransform();
            transform1.translate(projectile.rect.x, projectile.rect.y);
            transform1.translate((double) -projectile.rect.width / 2, (double) -projectile.rect.height / 2);
            transform1.rotate(projectile.rotation);
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

    public void crashOut(Rectangle duke) {

            // Target Duke's center (adjust offsets to match Duke's sprite size)
        float targetX = duke.x + 69;
        float targetY = duke.y;

        float dx = targetX - fx;
        float dy = targetY - fy;

        fx += dx * 1 / 100 * speed * 1.5f;
        fy += dy * 1 / 100 * speed * 1.5f;
        rect.setLocation(Math.round(fx), Math.round(fy));
    }

    public void normalAttacks(Rectangle duke) {

        double dist = Math.sqrt(
                Math.pow((duke.x - rect.x), 2) +
                        Math.pow((duke.y - rect.y), 2)
        );

        if (dist >= 400) {
            // Target Duke's center (adjust offsets to match Duke's sprite size)
            float targetX = duke.x + 69;
            float targetY = duke.y;

            float dx = targetX - fx;
            float dy = targetY - fy;

            fx += dx * 1 / 100 * speed;
            fy += dy * 1 / 100 * speed;
            rect.setLocation(Math.round(fx), Math.round(fy));
        }

        for (CManProjectile projectile : projectiles) {
            float pdx = projectile.target.x - projectile.pfx;
            float pdy = projectile.target.y - projectile.pfy;

            projectile.pfx += pdx * 1/100 * projectile.speed;
            projectile.pfy += pdy * 1/100 * projectile.speed;
            projectile.rect.setLocation(Math.round(projectile.pfx), Math.round(projectile.pfy));
            projectile.rotation += ((float) Math.toRadians(1));
            projectile.timePassed += parent.spawner_delta;

            if (projectile.rect.intersects(duke)) {
                parent.kill();
            }
        }
    }

    double time = .5;

    @Override
    public void spawnChild(Rectangle duke, double spawn_Delta) {
        elapsed_Time += spawn_Delta;
        time_passed += spawn_Delta;

        if (elapsed_Time >= time) {
            CManProjectile p = new CManProjectile(getAngle(rect.getLocation(), duke.getLocation()), duke.getLocation());
            p.pfx = rect.x+ (float) rect.width /2;
            p.pfy = rect.y+ (float) rect.height /2;
            p.speed = 2.5f;
            projectiles.add(p);
            elapsed_Time = 0;
        }
    }
}
