package org.example.util;

public class Vector2 {
    public int x = 0;
    public int y = 0;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveX(int dx) {
        x += dx;
    }

    public void moveY(int dy) {
        y += dy;
    }

    public void move(int dx, int dy) {
        moveX(dx);
        moveY(dy);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
