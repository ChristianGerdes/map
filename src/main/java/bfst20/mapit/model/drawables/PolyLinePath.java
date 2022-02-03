package bfst20.mapit.model.drawables;

import java.util.ArrayList;
import bfst20.mapit.Drawable;
import bfst20.mapit.model.Type;
import javafx.scene.paint.Color;
import bfst20.mapit.model.osm.OSMRelation;
import javafx.scene.canvas.GraphicsContext;
import bfst20.mapit.datastructures.Tree.Rectangle;

public class PolyLinePath extends ArrayList<LinePath> implements Drawable {
    private static final long serialVersionUID = -4838798038938840050L;
    Type type;

    public PolyLinePath(OSMRelation currentRelation, Type type) {
        for (var way : currentRelation) {
            this.add(new LinePath(way, type));
        }
        this.type = type;
    }

    public void draw(GraphicsContext gc) {
        gc.beginPath();
        for (var line : this) {
            line.trace(gc);
        }
        gc.stroke();
    }

    public Type getType() {
        return type;
    }

    public float[] getCoords() {
        float[] coords = this.get(0).getCoords();

        for (LinePath linePath : this) {
            float[] tempCoords = linePath.getCoords();

            if (tempCoords[0] < coords[0]) {
                coords[0] = tempCoords[0];
            }

            if (tempCoords[1] < coords[1]) {
                coords[1] = tempCoords[1];
            }
        }

        return coords;
    }

    public float[] getDimensions() {
        float[] startEdge = this.getCoords();
        float maxX = 0f;
        float maxY = startEdge[1];

        for (LinePath linePath : this) {
            for (int i = 0; i < linePath.coords.length / 2; ++i) {
                if (linePath.coords[i * 2] > maxX) {
                    maxX = linePath.coords[i * 2];
                }

                if (Math.abs(linePath.coords[i * 2 + 1]) < Math.abs(maxY)) {
                    maxY = linePath.coords[i * 2 + 1];
                }
            }
        }

        return new float[] { maxX - startEdge[0], Math.abs(maxY - startEdge[1]) };
    }

    public void drawRect(GraphicsContext gc, double width) {
        float[] coords = this.getCoords();
        float[] dimensions = this.getDimensions();

        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(width);
        gc.strokeRect(coords[0], coords[1], dimensions[0], dimensions[1]);
    }

    public Rectangle mbr() {
        return new Rectangle(this.getCoords(), this.getDimensions());
    }
}
