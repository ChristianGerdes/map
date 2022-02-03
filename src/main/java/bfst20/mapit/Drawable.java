package bfst20.mapit;

import bfst20.mapit.model.Type;
import javafx.scene.canvas.GraphicsContext;
import bfst20.mapit.datastructures.Tree.Rectangle;

public interface Drawable {
    public void draw(GraphicsContext gc);

    public void drawRect(GraphicsContext gc, double width);

    public Type getType();

    public float[] getCoords();

    public float[] getDimensions();

    public Rectangle mbr();
}
