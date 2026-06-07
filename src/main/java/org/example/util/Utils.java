package org.example.util;

import java.awt.*;

public class Utils {

    public static void pathFind(Point target, float speed, Rectangle.Float rect) {
        float targetX = target.x;
        float targetY = target.y;

        float dx = (float) (targetX - rect.getX());
        float dy = (float) (targetY - rect.getY());

        rect.x += (dx * (1/100f) * speed);
        rect.y += (dy * (1/100f) * speed);
    }
}
