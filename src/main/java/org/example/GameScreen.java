package org.example;

import org.example.util.enemies.Enemy;
import org.example.util.Projectile;
import org.example.util.Vector2;
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

public class GameScreen extends JPanel implements MouseMotionListener, KeyListener {
    private long lastTime;

    static record Range(int start, int end) {
        public boolean contains(int val) {
            return val >= start && val <= end;
        }
    }

    private static final float LERP_FACTOR       = 0.1f;
    private static final float MIN_SPEED         = 0.5f;
    private static final int   DUKE_ANCHOR_X     = 69;
    private static final int   DUKE_ANCHOR_Y     = 37;
    private static final int   UPDATE_INTERVAL   = 4;
    private static int   SPAWN_INTERVAL    = 2000;

    float timer = 0;

    public BufferedImage duke;
    public BufferedImage background;

    public Vector2 posDuke   = new Vector2(100, 100);
    public Point targetPos = new Point(100, 100);
    public Point previousPos = new Point(100, 100);

    private float fx = 100, fy = 100;
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
        addMouseMotionListener(this);
        addKeyListener(this);

        bar.setBounds(10, 10, 250, 50);
        bar.setValue(40);
        add(bar);

        this.parent = parent;

        duke       = ImageIO.read(getClass().getResource("/sprites/DukeOnShip.png").openStream());
        background = ImageIO.read(getClass().getResource("/sprites/Background.png").openStream());

        dukeRect = new Rectangle(posDuke.x, posDuke.y, duke.getWidth(), duke.getHeight());

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
                p.update();                      // move first
                return p.rect.x > getWidth();     // cull off-screen
            });
            projectiles.removeAll(removeQueue);
            removeQueue.clear();

            dukeRect.setLocation(posDuke.x, posDuke.y);

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
                int val = rand.nextInt(0, 2000);

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
        parent.setContentPane(new DeathScreen());
        parent.revalidate();
        update_Timer.stop();
    }
    private static final float MOVE_SPEED = 2f;

    private void updateDuke() {
        if (up)    angle = Math.toRadians(-90);
        if (down)  angle = Math.toRadians(90);
        if (left)  angle = Math.toRadians(180);
        if (right) angle = Math.toRadians(0);

        if (up && right)  angle = Math.toRadians(-45);
        if (up && left)   angle = Math.toRadians(-135);
        if (down && right) angle = Math.toRadians(45);
        if (down && left)  angle = Math.toRadians(135);

        float dx = 0, dy = 0;

        if (a) dx -= MOVE_SPEED;
        if (d) dx += MOVE_SPEED;
        if (w) dy -= MOVE_SPEED;
        if (s) dy += MOVE_SPEED;

        if (dx != 0 && dy != 0) {
            dx *= 0.7071f;
            dy *= 0.7071f;
        }

        posDuke.x += (int) dx;
        posDuke.y += (int) dy;

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
        t.translate(posDuke.x() + dukeRect.width / 2f, posDuke.y() + dukeRect.height / 2f); // center point

        t.rotate(angle);
        t.translate(-dukeRect.width / 2f, -dukeRect.height / 2f);

        g2d.setTransform(t);
        g2d.drawImage(duke, 0, 0, this);
        g2d.setTransform(new AffineTransform());
//        g.fillRect(dukeRect.x, dukeRect.y, dukeRect.width, dukeRect.height);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    boolean w, a, s, d, up, down, left, right;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> w = true;
            case KeyEvent.VK_A -> a = true;
            case KeyEvent.VK_S -> s = true;
            case KeyEvent.VK_D -> d = true;

            case KeyEvent.VK_UP -> up = true;
            case KeyEvent.VK_DOWN -> down = true;
            case KeyEvent.VK_LEFT -> left = true;
            case KeyEvent.VK_RIGHT -> right = true;
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && timer > 100) {
            projectiles.add(new Projectile(100){{
                rect.setLocation(posDuke.x+173, posDuke.y+37);
            }});
            timer = 0;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> w = false;
            case KeyEvent.VK_A -> a = false;
            case KeyEvent.VK_S -> s = false;
            case KeyEvent.VK_D -> d = false;

            case KeyEvent.VK_UP -> up = false;
            case KeyEvent.VK_DOWN -> down = false;
            case KeyEvent.VK_LEFT -> left = false;
            case KeyEvent.VK_RIGHT -> right = false;
        }
    }
}