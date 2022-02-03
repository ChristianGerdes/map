package bfst20.mapit.model.drawables;

import java.io.Serializable;
import bfst20.mapit.Drawable;
import javafx.scene.text.Font;
import bfst20.mapit.model.Type;
import javafx.scene.paint.Color;
import bfst20.mapit.model.osm.OSMWay;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.util.utilFunctions;
import javafx.scene.canvas.GraphicsContext;
import bfst20.mapit.datastructures.Tree.Rectangle;

public class CityNames implements Drawable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String fontFamily = "Verdana";
    private String fontWeight = "BOLD";
    private float[] coords;
    private String cityName;
    private Type type;
    private float x, y;
    private float[] dimensions;
	private float[] smallestPoint;

    /**
     * Calculates the center coordinates of a way.
     * @param way
     * @param cityName
     * @param type
     */
    public CityNames(OSMWay way, String cityName, Type type) {
        float[] coords = new float[way.size() * 2];
        float avrgx = 0, avrgy = 0;
        for (int i = 0; i < way.size(); ++i) {
            coords[i * 2] = way.get(i).lon;
            avrgx += coords[i * 2];
            coords[i * 2 + 1] = way.get(i).lat;
            avrgy += coords[i * 2 + 1];
        }
        dimensions = utilFunctions.calculateDimensions(coords);
        smallestPoint = utilFunctions.calculateSmallestPoint(coords);

        this.x = avrgx / (coords.length / 2);
        this.y = avrgy / (coords.length / 2);

        this.cityName = cityName;
        this.type = type;
    }

    /**
     *
     * @param node
     * @param cityName
     * @param type
     */
    public CityNames(OSMNode node, String cityName, Type type) {
        this.x = node.getLon();
        this.y = node.getLat();
        this.cityName = cityName;
        this.type = type;
    }

    /**
     * Returns the name of a city.
     * @return the name of a city.
     */
    public String getName() {
        return this.cityName;
    }

    private Font setFont(double pixelWidth) {
        double fontSize = 0;
        switch (this.type) {
            case CITY:
                fontSize = 25 * pixelWidth;
                fontWeight = "BOLD";
                break;
            case HAMLET:
                fontSize = 12 * pixelWidth;
                fontWeight = "MEDIUM";
                break;
            case VILLAGE:
                fontSize = 15 * pixelWidth;
                fontWeight = "MEDIUM";
                break;
            case TOWN:
                fontSize = 20 * pixelWidth;
                fontWeight = "SEMI_BOLD";
                break;
            default:
                fontSize = 10 * pixelWidth;
                fontWeight = "BOLD";
                break;
        }
        return utilFunctions.createFont(fontFamily, fontWeight, fontSize);

    }

    /**
     * Determines the font size and if it should be dynamic or static depending on
     * the pixelwidth.
     * @param pixelwidth
     * @return font size.
     */
    public Font getFontSize(double pixelwidth) {
        Font font;
        if (pixelwidth > 2.269582435373004E-4 && getType() != Type.CITY && getType() != Type.TOWN) {
            font = this.setFont(2.269582435373004E-4);
        } else if (pixelwidth > 0.0020964123183703567 && getType() == Type.CITY) {
            font = this.setFont(0.0020964123183703567);
        } else if (pixelwidth > 0.0013173 && getType() == Type.TOWN) {
            font = this.setFont(0.0013173);
        } else {
            font = this.setFont(pixelwidth);
        }
        return font;
    }

    public void draw(GraphicsContext gc) {
        double pixelwidth = 1 / Math.sqrt(Math.abs(gc.getTransform().determinant()));

        // We don't draw anything when we get too close to the surface
        if (pixelwidth < 2.7497716919641208E-5)
            return;

        gc.setFill(Color.rgb(126, 133, 142));
        Font font = this.getFontSize(pixelwidth);
        gc.setFont(font);

        // Placing the text in the middle of the city
        double textWidth = utilFunctions.getTextWidth(this.cityName, font);
        gc.fillText(getName(), this.x - (textWidth / 2), this.y);
    }

    public void drawRect(GraphicsContext gc, double width) {
        // No rects
    }

    public Type getType() {
        return type;
    }

    public float[] getCoords() {
        if (smallestPoint == null) {
            return new float[] { this.x, this.y };
        }
        return smallestPoint;
    }

    public float[] getDimensions() {
        if (dimensions == null)
            return new float[] { 0f, 0f };

        return dimensions;
    }

    public Rectangle mbr() {
        return new Rectangle(this.getCoords(), this.getDimensions());
    }
}