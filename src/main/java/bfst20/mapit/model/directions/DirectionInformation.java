package bfst20.mapit.model.directions;

import java.text.DecimalFormat;

public class DirectionInformation {
	private Direction direction;
	private String roadName;
	private double roadLength;
	private int exits;
	private boolean isOffRamp;
	private float[] coords;

	/**
	 * For normal directions
	 *
	 * @param direction
	 * @param roadName
	 * @param roadLength
	 * @param coords
	 */
	public DirectionInformation(Direction direction, String roadName, double roadLength, float[] coords) {
		this.roadName = roadName.equals("This road has no name") ? "unknown" : roadName;
		this.direction = direction;
		this.roadLength = roadLength;
		this.coords = coords;
	}

	public DirectionInformation(Direction direction, String roadName, double roadLength, boolean isOffRamp, float[] coords) {
		this.roadName = roadName;
		this.direction = direction;
		this.roadLength = roadLength;
		this.isOffRamp = isOffRamp;
		this.coords = coords;
	}

	/**
	 * Last direction
	 */
	public DirectionInformation(Direction direction, String roadName, double roadLength) {
		this.roadName = roadName;
		this.direction = direction;
		this.roadLength = roadLength;
	}

	/**
	 * For roundabouts
	 */
	public DirectionInformation(Direction direction, String roadName, int exits, double roadLength, float[] coords) {
		this.roadLength = roadLength;
		this.direction = direction;
		this.roadName = roadName;
		this.exits = exits;
		this.coords = coords;
	}

	public Direction getDirection() {
		return direction;
	}

	public double getLength() {
		return roadLength;
	}

	public float[] getCoords(){
		return coords;
	}

	public String getFormattedDistance() {
		String roundedDistance = "";
		String unit = "";

		if (this.roadLength > 1.0) {
			DecimalFormat decimalFormat = new DecimalFormat(".#");
			roundedDistance = decimalFormat.format(this.roadLength);
			unit = "km";
		} else {
			DecimalFormat decimalFormat = new DecimalFormat("#");
			roundedDistance = decimalFormat.format(this.roadLength * 1000);
			unit = "m";
		}

		return roundedDistance + " " + unit;
	}

	public String getFormattedDirection() {
		if (isOffRamp) {
			return "Exit onto " + roadName;
		}

		switch (direction) {
			case ROUNDABOUT:
				return "Take the " + exits + ". exit onto " + roadName;
			case END:
				return "You have reached your destination.";
			case STRAIGHT:
				return "Continue onto " + roadName;
			case WEAK_LEFT:
				return "Take a slight left turn onto " + roadName;
			case LEFT:
				return "Turn left onto " + roadName;
			case SHARP_LEFT:
				return "Take a sharp left turn onto " + roadName;
			case WEAK_RIGHT:
				return "Take a slight right turn onto " + roadName;
			case RIGHT:
				return "Turn right onto " + roadName;
			case SHARP_RIGHT:
				return "Take a sharp right turn onto " + roadName;
			default:
				throw new IllegalArgumentException("Direction does not exist");
		}
	}
}