package bfst20.mapit.model.drawables.roads;

import static bfst20.mapit.util.utilFunctions.distanceInKmBetweenCoordinates;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import bfst20.mapit.Drawable;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.datastructures.Tree.Rectangle;
import bfst20.mapit.model.Type;
import bfst20.mapit.model.directions.RouteType;
import bfst20.mapit.model.directions.VehicleType;
import bfst20.mapit.model.osm.OSMWay;
import bfst20.mapit.util.utilFunctions;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class Road implements Drawable, Serializable, Cloneable, Comparable<Road> {
	private static final long serialVersionUID = 5348049537896854521L;
	public float[] coords;
	private RoadType roadType;
	private boolean isDashed;
	private double length = 0;
	private double speedLimit;
	private boolean roundabout;

	long startVertex;
	long endVertex;
	public long id;
	private boolean forward;
	public boolean oneway;
	// Used for road names
	private HashMap<Long, String> wayIdToRoadname;

	private float[] dimensions;
	private float[] smallestPoint;

	public Road(OSMWay way, RoadType type, double speedLimit, boolean oneway, boolean roundabout,
			HashMap<Long, String> wayIdToRoadname) {
		this.wayIdToRoadname = wayIdToRoadname;
		coords = new float[way.size() * 2];
		for (int i = 0; i < way.size(); ++i) {
			coords[i * 2] = way.get(i).lon;
			coords[i * 2 + 1] = way.get(i).lat;
		}

		this.startVertex = way.first().id;
		this.endVertex = way.last().id;
		this.roadType = type;
		this.id = way.id;
		this.length = measureLength(coords);
		this.speedLimit = speedLimit;
		this.forward = true;
		this.roundabout = roundabout;
		if (roundabout) {
			this.oneway = true;
		} else {
			this.oneway = oneway;
		}
		this.isDashed = RoadType.isDashed(type);
		this.dimensions = utilFunctions.calculateDimensions(coords);
		this.smallestPoint = utilFunctions.calculateSmallestPoint(coords);
	}

	private float[] reverseCoords(float[] currentCoords) {
		float[] reversedCoords = new float[currentCoords.length];
		int j = currentCoords.length - 2;
		for (int i = 0; i <= j; i += 2) {
			reversedCoords[j] = currentCoords[i];
			reversedCoords[j + 1] = currentCoords[i + 1];
			reversedCoords[i] = currentCoords[j];
			reversedCoords[i + 1] = currentCoords[j + 1];
			j -= 2;
		}
		return reversedCoords;
	}

	// Returned reversed road
	public Road(Road road) {
		this.wayIdToRoadname = road.wayIdToRoadname;
		this.coords = reverseCoords(road.coords);
		this.length = road.length;
		this.startVertex = road.endVertex;
		this.endVertex = road.startVertex;
		this.speedLimit = road.speedLimit;
		this.roadType = road.roadType;
		this.id = road.id;
		this.forward = false;
		this.oneway = road.oneway;
		this.roundabout = road.roundabout;
		this.smallestPoint = road.smallestPoint;
		this.dimensions = road.dimensions;
	}

	public Road getCopy() {
		try {
			return (Road) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isRoundabout() {
		return roundabout;
	}

	public double getLength() {
		return length;
	}

	public String getName() {
		return wayIdToRoadname.containsKey(id) ? wayIdToRoadname.get(id) : "This road has no name";
	}

	public double getWeight(RouteType routeType, VehicleType vehicleType) {
		double deferWeight = 0;
		if ((roadType == RoadType.TRACK || roadType == RoadType.SERVICEROAD) && vehicleType == VehicleType.CAR) {
			deferWeight = getLength();
		}
		switch (routeType) {
			case SHORTEST:
				return getLength() + deferWeight;
			case FASTEST:
				return (getLength() + deferWeight) / getSpeedLimit();
			default:
				throw new IllegalArgumentException("No route of that type");
		}
	}

	public double getSpeedLimit() {
		return speedLimit;
	}

	public boolean isTraversingAllowed(VehicleType v) {
		if (v == VehicleType.PEDESTRIAN && roadType != RoadType.MOTORWAYROAD && roadType != RoadType.MOTORWAY_LINKROAD) {
			return true;
		}
		if (!forward && oneway) {
			return false;
		}
		VehicleType vehicle = RoadType.getAllowedVehicle(roadType);
		return vehicle == null ? true : v == vehicle;
	}

	private double measureLength(float[] coords) {
		double totalDistance = 0;
		if (coords.length == 4) {
			double lon1 = coords[0];
			double lat1 = coords[1];
			double lon2 = coords[2];
			double lat2 = coords[3];
			totalDistance += distanceInKmBetweenCoordinates(-lat1, lon1 / 0.56f, -lat2, lon2 / 0.56f);
		} else {
			for (int i = 0; i < (coords.length / 2) - 1; i++) {
				double lon1 = coords[i * 2];
				double lat1 = coords[i * 2 + 1];
				double lon2 = coords[i * 2 + 2];
				double lat2 = coords[i * 2 + 3];
				totalDistance += distanceInKmBetweenCoordinates(-lat1, lon1 / 0.56f, -lat2, lon2 / 0.56f);
			}
		}
		return totalDistance;
	}

	public void draw(GraphicsContext gc) {
		if (!isDashed) {
			gc.beginPath();
			trace(gc);
			gc.stroke();
		} else {
			double pixelwidth = 1 / Math.sqrt(Math.abs(gc.getTransform().determinant()));
			gc.setLineDashes(pixelwidth * 5);
			traceDashed(gc);
			gc.setLineDashOffset(0);
			gc.setLineDashes(0);
		}

		gc.setLineCap(StrokeLineCap.ROUND);
		gc.setLineJoin(StrokeLineJoin.ROUND);
	}

	public void draw(GraphicsContext gc, int from, int to) {
		float[] temp = Arrays.copyOfRange(coords, from, to);

		gc.beginPath();
		gc.moveTo(temp[0], temp[1]);

		for (int i = 2; i < temp.length; i += 2) {
			gc.lineTo(temp[i], temp[i + 1]);
		}

		gc.stroke();
	}

	public void drawInDifferentColors(GraphicsContext gc) {
		for (int i = 2; i < coords.length; i += 2) {
			gc.setStroke(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
			gc.beginPath();
			gc.moveTo(coords[i - 2], coords[i - 1]);
			gc.lineTo(coords[i], coords[i + 1]);
			gc.stroke();
		}
	}

	public Type getType() {
		return RoadType.getType(roadType);
	}

	public RoadType getRoadType() {
		return roadType;
	}

	public void trace(GraphicsContext gc) {
		gc.moveTo(coords[0], coords[1]);
		for (int i = 2; i < coords.length; i += 2) {
			gc.lineTo(coords[i], coords[i + 1]);
		}
	}

	public void traceFromIndex(GraphicsContext gc, int index) {
		gc.moveTo(coords[index], coords[index + 1]);
		for (int i = index + 2; i < coords.length; i += 2) {
			gc.lineTo(coords[i], coords[i + 1]);
		}
	}

	private void traceDashed(GraphicsContext gc) {
		for (int i = 2; i < coords.length; i += 2) {
			gc.strokeLine(coords[i - 2], coords[i - 1], coords[i], coords[i + 1]);
		}
	}

	public long getStartVertex() {
		return startVertex;
	}

	public long getEndVertex() {
		return endVertex;
	}

	public boolean isForward() {
		return forward;
	}

	public float[] getCoords() {
		return smallestPoint;
	}

	public float[] getDimensions() {
		return dimensions;
	}

	// used for debugging
	public void lightUp(GraphicsContext gc) {
		gc.save();
		gc.setStroke(Color.rgb(244, 67, 54, 0.7));
		gc.beginPath();
		trace(gc);
		gc.stroke();
		gc.restore();
	}

	public void drawRect(GraphicsContext gc, double width) {
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(width);
		gc.strokeRect(smallestPoint[0], smallestPoint[1], dimensions[0], dimensions[1]);
	}

	public void setRoadType(RoadType type) {
		this.roadType = type;
	}

	public Road getCopyAsRoute() {
		try {
			Road road;
			road = (Road) this.clone();
			road.setRoadType(RoadType.ROUTE);
			return road;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Rectangle mbr() {
		return new Rectangle(this.getCoords(), this.getDimensions());
	}

	@Override
	public int compareTo(Road that) {
		return (int) (this.id - that.id);
	}

	public boolean isTheSameRoadSegment(Road that) {
		if (this.endVertex == that.endVertex && this.startVertex == that.startVertex
				|| this.startVertex == that.endVertex && this.endVertex == that.startVertex
				|| this.endVertex == that.startVertex && this.startVertex == that.endVertex) {
			return true;
		} else {
			return false;
		}
	}
}