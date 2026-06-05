package org.example.util.enemies;

import jdk.jfr.consumer.RecordedThread;
import org.example.GameScreen;
import org.example.util.Vector2;

import java.awt.*;
import java.util.Random;

public class PySnake extends Enemy {
    public PySnake(GameScreen parent) {
        super("/sprites/Python_Snake.png", 40, 5, parent);
        health = 50;

        rect.y = new Random().nextInt(parent.dukeRect.y-50, parent.dukeRect.y+50);
    }

    @Override
    public void update(Rectangle duke) {
        rect.x -= Math.round(speed);
    }

    @Override
    public void spawnChild(Rectangle duke, double delta) {

    }
}
