package bfst20.mapit.model.drawables.points;

import java.io.Serializable;

import bfst20.mapit.Drawable;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.datastructures.Tree.Rectangle;
import bfst20.mapit.model.AutoComplete;
import bfst20.mapit.model.Type;
import bfst20.mapit.model.io.IconsLoader;
import bfst20.mapit.util.utilFunctions;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class POI implements Drawable, AutoComplete, Serializable {
    private static final long serialVersionUID = 1897768527058640927L;
    protected float x;
    protected float y;
    protected float[] sizes = { 0.007f, 0.08f };
    private double red, green, blue;
    public String name;
    private boolean needsBackgroundFill = false;
    private boolean isLightmode = true;

    public POI(float x, float y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;

        this.red = Math.random();
        this.green = Math.random();
        this.blue = Math.random();
    }

    /*
     * Sets the size for the font and img based on the render distance.
     *
     * @ param sizes - has two default values - for medium and large render distance
     * and one dynamic for short render distance
     */
    private double setSize(double renderDistance) {
        if (renderDistance > 0.004771) {
            return 0.08f;
        }
        return 30 * renderDistance;
    }

    private Image matchPOIicon() {
        if (getName().matches("(?i:.*HOME|HJEM.*)")) {
            needsBackgroundFill = false;
            return IconsLoader.getInstance().getIcon("home", "UI", "svg");
        }

        if (getName().matches("(?i:.*WORK|ARBEJDE|OFFICE.*)")) {
            needsBackgroundFill = false;
            return IconsLoader.getInstance().getIcon("work", "UI", "svg");
        }

        if (getName().matches("(?i:.*SCHOOL|UNIVERSITY|SKOLE.*)")) {
            needsBackgroundFill = false;
            return IconsLoader.getInstance().getIcon("university", "UI", "svg");
        }

        if (!isLightmode) {
            needsBackgroundFill = true;
            return IconsLoader.getInstance().getIcon("POI_DARK", "UI", "svg");
        }

        needsBackgroundFill = true;
        return IconsLoader.getInstance().getIcon("POI_UI", "UI", "svg");
    }

    public void draw(GraphicsContext gc) {
        double pixelwidth = 1 / Math.sqrt(Math.abs(gc.getTransform().determinant()));
        if (pixelwidth < 1.6897716919641208E-6)
            return;

        double imgSize = setSize(pixelwidth);
        Color color = isLightmode ? Color.rgb(0, 0, 0) : Color.rgb(255, 214, 9);
        var img = matchPOIicon();
        var font = utilFunctions.createFont("Verdana", "BOLD", imgSize * 0.3);
        double textWidth = utilFunctions.getTextWidth(getName(), font);

        Paint prevFill = gc.getFill();
        gc.setFill(color);
        gc.setFont(font);
        gc.fillText(getName(), (x - (textWidth / 2)), y - imgSize - (imgSize / 12));

        if (needsBackgroundFill) {
            gc.setFill(Color.color(red, green, blue));
            gc.fillRect(x - (imgSize / 3.4), y - (imgSize - imgSize / 6), imgSize / 1.7, imgSize / 2);
            gc.setFill(prevFill);
        }
        gc.drawImage(img, x - (imgSize / 2), y - (imgSize), imgSize, imgSize);
    }

    public String getName() {
        return name;
    }

    public void setPOI(String newPOI) {
        name = newPOI;
    }

    public void drawRect(GraphicsContext gc, double width) {
    }

    public Type getType() {
        return null;
    }

    public float[] getCoords() {
        return new float[] { this.x, this.y };
    }

    public float[] getDimensions() {
        return new float[] { 0f, 0f };
    }

    public float getLon() {
        return this.x;
    }

    public float getLat() {
        return this.y;
    }

    public boolean getLightmode() {
        return isLightmode;
    }

    public void setLightmode(boolean isLightmode) {
        this.isLightmode = isLightmode;
    }

    public String toString() {
        return this.name;
    }

    public Rectangle mbr() {
        return new Rectangle(this.getCoords(), this.getDimensions());
    }
}