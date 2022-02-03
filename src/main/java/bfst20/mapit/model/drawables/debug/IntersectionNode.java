package bfst20.mapit.model.drawables.debug;

import bfst20.mapit.Drawable;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.datastructures.Tree.Rectangle;
import bfst20.mapit.model.Type;
import bfst20.mapit.model.osm.OSMNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class IntersectionNode extends OSMNode implements Drawable {
  	private static final long serialVersionUID = -874026050665238848L;

	public IntersectionNode(OSMNode node) {
		super(node.id, node.lon, node.lat);
	}

	public void draw(GraphicsContext gc) {
		gc.setFill(Color.rgb(0, 0, 0, 1));
		gc.fillOval(super.lon, super.lat, 1.6875405741623497E-5, 1.6875405741623497E-5);
		gc.setFont(Font.font("Verdana", FontWeight.BOLD, 0.00004));
		gc.fillText("NodeId: " + String.valueOf(super.id) + ". Lat: " + super.lat + ", Lon: " + super.lon, super.lon, super.lat);
	}

	public Type getType() {
		return Type.INTERSECTIONNODE;
	}

	public float[] getCoords() {
		return new float[] { super.lon, super.lat };
	}

	public float[] getDimensions() {
		return new float[] { 0.0f, 0.0f };
	}

	public void drawRect(GraphicsContext gc, double width) {
		gc.setFill(Color.BLUE);
		gc.fillOval(super.lon, super.lat, 1.6875405741623497E-5 * 2, 1.6875405741623497E-5 * 2);
	}

	public Rectangle mbr() {
		return new Rectangle(this.getCoords(), this.getDimensions());
	}
}