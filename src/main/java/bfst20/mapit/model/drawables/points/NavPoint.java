package bfst20.mapit.model.drawables.points;

import java.util.logging.Level;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class NavPoint extends POI implements Cloneable {
    private static final long serialVersionUID = 8621109336607104713L;
    private Image img;

    public NavPoint(float lon, float lat, Image img) {
        super(lon, lat, null);
        this.img = img;
    }

    public void draw(GraphicsContext gc) {
        double size = (1 / Math.sqrt(Math.abs(gc.getTransform().determinant())));
        if (size < 1.3E-6)
            return;
        size *= 30;
        gc.drawImage(img, x - (size / 2), y - (size), size, size);
    }

    public void setImage(Image img) {
        this.img = img;
    }

}