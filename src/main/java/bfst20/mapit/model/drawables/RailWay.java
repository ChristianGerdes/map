package bfst20.mapit.model.drawables;

import java.io.Serializable;
import bfst20.mapit.Drawable;
import bfst20.mapit.model.Type;
import javafx.scene.paint.Color;
import bfst20.mapit.model.osm.OSMWay;
import bfst20.mapit.util.utilFunctions;
import javafx.scene.canvas.GraphicsContext;
import bfst20.mapit.datastructures.Tree.Rectangle;

public class RailWay implements Drawable, Serializable {
    private static final long serialVersionUID = -5463931505912383857L;

    public float[] coords;
    private float[] dimensions;
    private float[] smallestPoint;
    private Type type;

    public RailWay(OSMWay way, Type type) {
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

        double pixelwidth = 1 / Math.sqrt(Math.abs(gc.getTransform().determinant()));
        if (pixelwidth < 2.7675E-5)
            setDashedLines(gc, pixelwidth);
    }

    public void trace(GraphicsContext gc) {
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2; i < coords.length; i += 2) {
            gc.lineTo(coords[i], coords[i + 1]);
        }
    }

    public void setDashedLines(GraphicsContext gc, double pixelwidth) {
        gc.setLineWidth(0.000007062621);
        gc.setStroke(Color.rgb(252, 252, 252));
        gc.setLineWidth(6.075756E-6);
        gc.setLineDashes(pixelwidth * 15);
        traceDashed(gc);
        gc.setLineDashOffset(0);
        gc.setLineDashes(0);
    }

    private void traceDashed(GraphicsContext gc) {
        for (int i = 2; i < coords.length; i += 2) {
            gc.strokeLine(coords[i - 2], coords[i - 1], coords[i], coords[i + 1]);
        }
    }

    public Type getType() {
        return type;
    }

    public void drawRect(GraphicsContext gc, double width) {
        float[] coords = this.getCoords();
        float[] dimensions = this.getDimensions();

        gc.setStroke(Color.RED);
        gc.setLineWidth(width);
        gc.strokeRect(coords[0], coords[1], dimensions[0], dimensions[1]);
    }

    public float[] getCoords() {
        return smallestPoint;
    }

    public float[] getDimensions() {
        return dimensions;
    }

    public Rectangle mbr() {
        return new Rectangle(this.getCoords(), this.getDimensions());
    }
}