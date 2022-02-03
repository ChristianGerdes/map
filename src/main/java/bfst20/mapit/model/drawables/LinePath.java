package bfst20.mapit.model.drawables;

import java.io.Serializable;
import bfst20.mapit.Drawable;
import bfst20.mapit.model.Type;
import javafx.scene.paint.Color;
import bfst20.mapit.model.osm.OSMWay;
import bfst20.mapit.util.utilFunctions;
import javafx.scene.canvas.GraphicsContext;
import bfst20.mapit.datastructures.Tree.Rectangle;

public class LinePath implements Drawable, Serializable {
    private static final long serialVersionUID = 5348049537896854521L;
    public float[] coords;
    private float[] dimensions;
    private float[] smallestPoint;
    private Type type;

    public LinePath(OSMWay way, Type type) {
        coords = new float[way.size() * 2];
        for (int i = 0; i < way.size(); ++i) {
            coords[i * 2] = way.get(i).lon;
            coords[i * 2 + 1] = way.get(i).lat;
        }
        this.type = type;
        this.dimensions = utilFunctions.calculateDimensions(coords);
        this.smallestPoint = utilFunctions.calculateSmallestPoint(coords);
    }

    public void draw(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.stroke();
    }

    public Type getType() {
        return type;
    }

    public void trace(GraphicsContext gc) {
        int step = 2;
        if (type == Type.ISLAND) {
            double pixelwidth = 1 / Math.sqrt(Math.abs(gc.getTransform().determinant()));
            step = (int) Math.round(pixelwidth * 25000) * 2;
            if (step < 2) {
                step = 2;
            } else if (step > 80) {
                step = 80;
            } else if (step >= coords.length - 2) {
                step = 4;
            }
        }
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2; i < coords.length; i += step) {
            gc.lineTo(coords[i], coords[i + 1]);
        }
    }

    public float[] getCoords() {
        return smallestPoint;
    }

    public float[] getDimensions() {
        return dimensions;
    }

    public void drawRect(GraphicsContext gc, double width) {
        float[] coords = this.getCoords();
        float[] dimensions = this.getDimensions();

        gc.setStroke(Color.BLUE);
        gc.setLineWidth(width);
        gc.strokeRect(coords[0], coords[1], dimensions[0], dimensions[1]);
    }

    public Rectangle mbr() {
        return new Rectangle(this.getCoords(), this.getDimensions());
    }
}