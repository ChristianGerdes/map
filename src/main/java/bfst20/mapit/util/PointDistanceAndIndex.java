package bfst20.mapit.util;

import bfst20.mapit.datastructures.Point;

public class PointDistanceAndIndex {
	public double distance;
	public Point point;
	public int index;

	public PointDistanceAndIndex(Point p, double distance, int index) {
		this.point = p;
		this.distance = distance;
		this.index = index;
	}
}