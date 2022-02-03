package bfst20.mapit.model.drawables.cartographic;

import java.io.Serializable;
import bfst20.mapit.Drawable;
import javafx.scene.text.Font;
import bfst20.mapit.model.Type;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.model.io.IconsLoader;
import javafx.scene.canvas.GraphicsContext;
import bfst20.mapit.datastructures.Tree.Rectangle;

public class AmenityNode implements Drawable, Serializable {
	private static final long serialVersionUID = -3179524757872692931L;

	public String name;

	public float lon;

	public float lat;

	private double fontSize = 0.0001;

	public AmenityNode(OSMNode node, String name) {
		this.name = name;
		this.lon = node.lat;
		this.lat = node.lon;
	}

	private String formattedName() {
		String output = "";
		String[] nameArr = name.split("_");

		for (int i = 0; i < nameArr.length; i++) {
			output += nameArr[i].substring(0, 1).toUpperCase() + nameArr[i].substring(1) + " ";
		}

		return output.substring(0, output.length() - 1);
	}

	public void draw(GraphicsContext gc) {
		double pixelwidth = 1 / Math.sqrt(Math.abs(gc.getTransform().determinant()));
		double iconRenderDistance = 4.0 * 10e-7;

		if (pixelwidth < iconRenderDistance) {
			Image icon = IconsLoader.getInstance().getIcon(name, "amenities", "svg");

			if (icon != null) {
				gc.drawImage(icon, lon, lat, fontSize, fontSize);

			} else {
				// FALLBACK, draw the first letter in the amenity
				gc.setFill(Color.rgb(1, 67, 97, 0.9));
				gc.fillOval(lon, lat, fontSize * 1.6, fontSize * 1.6);
				gc.setFill(Color.rgb(229, 236, 239, 1));
				gc.setFont(Font.font("Verdana", FontWeight.BOLD, fontSize));
				gc.fillText(formattedName().substring(0, 1), lon + fontSize * 0.4, lat + fontSize * 1.2);
			}
		}
	}

	public Type getType() {
		return Type.AMENITY;
	}

	public float[] getCoords() {
		return new float[] { this.lat, this.lon };
	}

	public float[] getDimensions() {
		return new float[] { 0f, 0f };
	}

	public void drawRect(GraphicsContext gc, double width) {
		float[] coords = this.getCoords();
		float[] dimensions = this.getDimensions();

		gc.setStroke(Color.RED);
		gc.setLineWidth(width);
		gc.strokeRect(coords[0], coords[1], dimensions[0], dimensions[1]);
	}

	public Rectangle mbr() {
		return new Rectangle(this.getCoords(), this.getDimensions());
	}
}
