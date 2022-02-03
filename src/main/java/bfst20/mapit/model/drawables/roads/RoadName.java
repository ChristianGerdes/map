package bfst20.mapit.model.drawables.roads;

import java.io.Serializable;
import java.util.HashMap;

import bfst20.mapit.Drawable;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.datastructures.Tree.Rectangle;
import bfst20.mapit.model.Type;
import bfst20.mapit.model.osm.OSMWay;
import bfst20.mapit.util.Fonts;
import bfst20.mapit.util.utilFunctions;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class RoadName implements Drawable, Serializable {
	private static final long serialVersionUID = 4268136413689501039L;
	private float rotation;
	// X,Y coord
	private float[] coords;
	private float[] location;
	private boolean leftToRight = true;
	private boolean roadTooShortForName = false;
	private HashMap<Long, String> wayIdToRoadname;
	private long id;
	private float[] dimensions;
	private float[] smallestPoint;

	public RoadName(OSMWay way, HashMap<Long, String> wayIdToRoadname) {
		this.wayIdToRoadname = wayIdToRoadname;
		coords = new float[way.size() * 2];
		for (int i = 0; i < way.size(); ++i) {
			coords[i * 2] = way.get(i).lon;
			coords[i * 2 + 1] = way.get(i).lat;
		}
		this.id = way.id;
		// Checks if the road goes from left to right on the map,
		// used for drawing the text in the leftmost end
		if (coords[0] > coords[2]) {
			this.leftToRight = false;
		}
		this.rotation = calcRotation(coords);
		this.location = getDrawingLocation(coords);
		this.roadTooShortForName = isTooShortForName(coords);
		this.dimensions = utilFunctions.calculateDimensions(coords);
		this.smallestPoint = utilFunctions.calculateSmallestPoint(coords);
	}

	public String getName() {
		return wayIdToRoadname.get(id);
	}

	private boolean isTooShortForName(float[] coords) {
		var textWidth = utilFunctions.getTextWidth(getName(), font());

		var roadLength = Math.abs(utilFunctions.euclideanDistance(coords[coords.length / 2 - 1],
				coords[coords.length / 2 - 2], coords[coords.length / 2 + 1], coords[coords.length / 2 + 0]));

		if (coords.length == 4 && textWidth > roadLength) {
			return true;
		} else {
			return false;
		}
	}

	private float calcRotation(float[] coords) {
		double x1, y1, x2, y2;
		// draw in the middle between the start and the end of the road
		x1 = coords[coords.length / 2 - 2];
		y1 = coords[coords.length / 2 - 1];
		x2 = coords[coords.length / 2 + 0];
		y2 = coords[coords.length / 2 + 1];

		double angle = Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI;
		// makes sure text isn't upside down
		if (angle <= -90 || angle >= 90) {
			angle += 180;
		}

		return (float) angle;
	}

	public void draw(GraphicsContext gc) {
		if (roadTooShortForName) {
			return;
		}

		drawRoadName(location[0], location[1], rotation, gc);
	}

	private float[] getDrawingLocation(float[] coords) {
		float x, y;
		if (coords.length >= 8) {
			x = coords[coords.length / 2 - 2];
			y = coords[coords.length / 2 - 1];
		} else {
			x = leftToRight ? coords[0] : coords[2];
			y = leftToRight ? coords[1] : coords[3];
		}

		return new float[] { x, y };
	}

	private void drawRoadName(double x, double y, double rotation, GraphicsContext gc) {
		gc.save();
		// pushes it down, so its in the middle'ish of the road
		gc.translate(x, y + 1.26565543e-5);
		gc.rotate(rotation);
		gc.setFill(Color.rgb(124, 124, 124));
		gc.setFont(font());
		gc.fillText(getName(), 0, 0);
		gc.restore();
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

	public Type getType() {
		return Type.ROADNAMES;
	}

	private Font font() {
		return Fonts.getInstance().roadNameFont();
	}

	public Rectangle mbr() {
		return new Rectangle(this.getCoords(), this.getDimensions());
	}
}