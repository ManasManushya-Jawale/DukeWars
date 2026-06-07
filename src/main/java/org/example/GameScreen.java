package org.example;

import org.example.util.enemies.Enemy;
import org.example.util.Projectile;
import org.example.util.enemies.CMan;
import org.example.util.enemies.PySnake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameScreen extends JPanel implements KeyListener {
    private long lastTime;

    record Range(int start, int end) {
        public boolean contains(int val) {
            return val >= start && val <= end;
        }
    }

    private final int   UPDATE_INTERVAL   = 4;
    private int   SPAWN_INTERVAL    = 2000;

    float timer = 0;

    public BufferedImage duke;
    public BufferedImage background;

    public Rectangle dukeRect;

    public List<Enemy> enemies = new CopyOnWriteArrayList<>();
    public List<Projectile> projectiles = new ArrayList<>();
    public List<Projectile> removeQueue = new ArrayList<>();

    public JFrame parent;

    Timer update_Timer;
    Timer spawn_Timer;

    public double spawner_delta = 0;

    Random rand = new Random();
    Range CSpawnRange = new Range(0, 800);
    Range PySpawnRange = new Range(801, 999);

    JProgressBar bar = new JProgressBar(0, 100);

    public GameScreen(JFrame parent) throws IOException {
        super(null);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        bar.setBounds(10, 10, 250, 50);
        bar.setValue(40);
        add(bar);

        this.parent = parent;

        duke       = ImageIO.read(getClass().getResource("/sprites/DukeOnShip.png").openStream());
        background = ImageIO.read(getClass().getResource("/sprites/Background.png").openStream());

        dukeRect = new Rectangle(100, 100, duke.getWidth(), duke.getHeight());

        update_Timer = new Timer(UPDATE_INTERVAL, e -> {

            updateDuke();
            for (int i=0;i<enemies.size();i++) {
                Enemy enemy = enemies.get(i);
                enemy.update(dukeRect);
                enemy.spawnChild(dukeRect, spawner_delta);

                if (enemy.rect.intersects(dukeRect)) {
                    kill();
                    return;
                }

                for (Projectile projectile : projectiles) {
                    if (i==0) {
                        if (projectile.rect.x > getParent().getWidth()) {
                            projectiles.remove(projectile);
                        }
                    }
                    if (enemy.rect.intersects(projectile.rect)) {
                        enemy.health -= projectile.damage;
                        removeQueue.add(projectile);

                        SPAWN_INTERVAL= (int) (SPAWN_INTERVAL * .99f);
                        spawn_Timer.setDelay(SPAWN_INTERVAL);
                    }

                }

                if (enemy instanceof CMan cman) {
                    if (cman.time_passed > 5) {
                        cman.crashout = true;
                    }
                }
            }
            enemies.removeIf(en -> en.health <= 0 || en.rect.x < 0);
            projectiles.removeIf(p -> {
                p.update();
                return p.rect.x > getWidth();
            });
            projectiles.removeAll(removeQueue);
            removeQueue.clear();

            dukeRect.setLocation(dukeRect.x, dukeRect.y);

            timer++;
            backX--;

            if (backX <= -80) {
                backX = 0;
            }

            long now = System.nanoTime();
            // Calculate delta time in seconds
            spawner_delta = (now - lastTime) / (double) 1_000_000_000;
            lastTime = now;

            repaint();
        });
        update_Timer.start();

        spawn_Timer = new Timer(SPAWN_INTERVAL, e -> {

            if (parent != null) {
                int val = rand.nextInt(0, 1000);

                if (CSpawnRange.contains(val)) {
                    enemies.add(new CMan(this));
                }

                else if (PySpawnRange.contains(val)) {
                    enemies.add(new PySnake(this));
                }
            }

        });
        spawn_Timer.start();
    }

    public void kill() {
        parent.setContentPane(new DeathScreen(parent));
        parent.revalidate();
        update_Timer.stop();
        spawn_Timer.stop();
    }
    private static final float MOVE_SPEED = 2f;

    private void updateDuke() {

        float dx = 0, dy = 0;

        if (a) dx -= MOVE_SPEED;
        if (d) dx += MOVE_SPEED;
        if (w) dy -= MOVE_SPEED;
        if (s) dy += MOVE_SPEED;

        if (dx != 0 && dy != 0) {
            dx *= 0.7071f;
            dy *= 0.7071f;
        }

        dukeRect.x += (int) dx;
        dukeRect.y += (int) dy;

    }

    int backX = 0;
    double angle = 0;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelW = getWidth();
        int panelH = getHeight();

        Graphics2D g2d = ((Graphics2D) g);

        for (int row = 0; row < panelH / 60 + 4; row++) {
            for (int col = 0; col < panelW / 80 + 4; col++) {
                g.drawImage(background, col * 80 + backX, row * 60, this);
            }
        }

        for (Enemy enemy : enemies) enemy.draw(g, this);
        for (Projectile projectile : projectiles) projectile.draw(g);

        g2d.setTransform(new AffineTransform());  // reset first

        AffineTransform t = new AffineTransform();
        t.translate(dukeRect.x+ dukeRect.width / 2f, dukeRect.y + dukeRect.height / 2f); // center point

        t.rotate(angle);
        t.translate(-dukeRect.width / 2f, -dukeRect.height / 2f);

        g2d.setTransform(t);
        g2d.drawImage(duke, 0, 0, this);
        g2d.setTransform(new AffineTransform());
//        g.fillRect(dukeRect.x, dukeRect.y, dukeRect.width, dukeRect.height);
    }

    boolean w, a, s, d;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> {
                w = true;
                angle = Math.toRadians(-90);
            }
            case KeyEvent.VK_A -> {
                a = true;
                angle = Math.toRadians(180);
            }
            case KeyEvent.VK_S -> {
                s = true;
                angle = Math.toRadians(90);
            }
            case KeyEvent.VK_D -> {
                d = true;
                angle = Math.toRadians(0);
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && timer > 100) {
            projectiles.add(new Projectile(100){
                public float dx = 0, dy = 0;

                {
                    rect.x = dukeRect.x+173; rect.y = dukeRect.y+37;
                    dx = (float) Math.cos(angle);
                    dy = (float) Math.sin(angle);
                }

                @Override
                public void update() {
                    rect.x += Math.round(dx) * 3;
                    rect.y += Math.round(dy) * 3;
                }
            });
            timer = 0;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> w = false;
            case KeyEvent.VK_A -> a = false;
            case KeyEvent.VK_S -> s = false;
            case KeyEvent.VK_D -> d = false;
        }
    }
}