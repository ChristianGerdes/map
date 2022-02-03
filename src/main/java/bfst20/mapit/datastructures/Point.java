package bfst20.mapit.datastructures;

import java.io.Serializable;
import javafx.scene.text.Font;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.canvas.GraphicsContext;

public class Point implements Serializable {
    private static final long serialVersionUID = 3503867501926890818L;

    private final float x;

    private final float y;

    public Point(Point2D point) {
        this.x = (float) point.getX();
        this.y = (float) point.getY();
    }

    public Point(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void drawDEBUG(GraphicsContext gc) {
        gc.setFill(Color.DARKBLUE);
        gc.fillOval(x, y, 1.6875405741623497E-5 * 2, 1.6875405741623497E-5 * 2);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 0.00004));
        gc.fillText("x: " + x + ", y: " + y, x, y);
    }

    public String toString() {
        return "Point: [x: " + x + ", y: " + y + "]";
    }
}