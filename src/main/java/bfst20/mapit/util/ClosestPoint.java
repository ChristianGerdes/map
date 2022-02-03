package bfst20.mapit.util;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import bfst20.mapit.Drawable;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.model.drawables.roads.Road;

//https://math.stackexchange.com/questions/2193720/find-a-point-on-a-line-segment-which-is-the-closest-to-other-point-not-on-the-li
public class ClosestPoint {
	private static final Point zeroVector = new Point(0, 0);

	/**
	 *
	 * @param queryPoint The queryPoint
	 * @param A          The end point of the lineSegment
	 * @param B          The start point of the lineSegment
	 * @return The closest point
	 */
	private static Point closestPointOnLineSegment(Point queryPoint, Point A, Point B) {
		Point v = new Point(B.getX() - A.getX(), B.getY() - A.getY());
		Point u = new Point(A.getX() - queryPoint.getX(), A.getY() - queryPoint.getY());
		float vu = v.getX() * u.getX() + v.getY() * u.getY();
		float vv = (float) (Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2));
		float t = -vu / vv;
		if (t >= 0 && t <= 1) {
			return vectorToSegment(t, zeroVector, A, B);
		}

		float g0 = sqDiag(vectorToSegment(0, queryPoint, A, B));
		float g1 = sqDiag(vectorToSegment(1, queryPoint, A, B));

		return g0 <= g1 ? A : B;
	}

	private static Point vectorToSegment(float t, Point queryPoint, Point A, Point B) {
		float x = (1 - t) * A.getX() + t * B.getX() - queryPoint.getX();
		float y = (1 - t) * A.getY() + t * B.getY() - queryPoint.getY();

		return new Point(x, y);
	}

	private static float sqDiag(Point P) {
		return (float) (Math.pow(P.getX(), 2) + Math.pow(P.getY(), 2));
	}

	private static double distanceBetweenPoints(Point p1, Point p2) {
		return utilFunctions.distanceInKmBetweenCoordinates(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	/**
	 *
	 * @param queryPoint The query point
	 * @param road       The road, to find the closest point on
	 * @return The closest point, the distance to it and the index in the coords
	 *         array
	 */
	public static PointDistanceAndIndex getClosestPoint(Point queryPoint, Road road) {
		Point closestPoint = null;
		double closestDistance = Double.POSITIVE_INFINITY;
		// it's the index in coords array where the closest point is found
		int index = -1;
		float[] coords = road.coords;

		float x1;
		float y1;
		float x2;
		float y2;

		// if the a straight line the we cannot use a for loop
		if (coords.length == 4) {
			x1 = coords[0];
			y1 = coords[1];
			x2 = coords[2];
			y2 = coords[3];

			Point closestPointOnLine = closestPointOnLineSegment(queryPoint, new Point(x1, y1), new Point(x2, y2));
			double distanceClosest = distanceBetweenPoints(queryPoint, closestPointOnLine);
			closestDistance = distanceClosest;
			closestPoint = closestPointOnLine;
			index = 2;
		} else {
			// For each coordinat pair in the road we find the closest point and the
			// distance between that point and the queryPoint
			for (int i = 0; i < (coords.length / 2) - 1; i += 2) {
				x1 = coords[i * 2 + 0];
				y1 = coords[i * 2 + 1];
				x2 = coords[i * 2 + 2];
				y2 = coords[i * 2 + 3];
				Point closestPointOnLine = closestPointOnLineSegment(queryPoint, new Point(x1, y1), new Point(x2, y2));
				double distanceClosest = distanceBetweenPoints(queryPoint, closestPointOnLine);

				// If the new distance is shorter we update the variables
				if (distanceClosest < closestDistance) {
					closestDistance = distanceClosest;
					closestPoint = closestPointOnLine;
					index = i * 2 + 2;
				}
			}
		}

		return new PointDistanceAndIndex(closestPoint, closestDistance, index);
	}

	/**
	 *
	 * @param point         The query point
	 * @param originalRoad  the first or the last road in the route
	 * @param adjacentRoads An iterator of adjacentRoads to the "originalRoad"
	 * @param isEndSegment  If it is the end of the route
	 * @return A list of line segments to be drawn
	 */
	public static List<Drawable> getLineSegments(Point point, Road originalRoad, Iterable<Road> adjacentRoads, boolean isEndSegment) {
		// start by getting the information of the original road
		var pointAndDistance = getClosestPoint(point, originalRoad);
		Road closestRoad = originalRoad;
		Point closestPoint = pointAndDistance.point;
		double closestDistance = pointAndDistance.distance;
		int index = pointAndDistance.index;

		// Check if any of the adjacent roads
		// contains a closer point than the original road
		for (Road road : adjacentRoads) {
			// Due to inserting the same road forwards and backwards in the graph, we can
			// encounter getting the same road as an adjacent road, if that is the case we
			// skip it, so we do not run the calculations again
			if (road.isTheSameRoadSegment(originalRoad)) {
				continue;
			}
			// get the closestPoint for the adjacent road
			pointAndDistance = getClosestPoint(point, road);
			// if the new point is better than current best we update the variables
			if (pointAndDistance.distance < closestDistance) {
				closestRoad = road;
				closestPoint = pointAndDistance.point;
				closestDistance = pointAndDistance.distance;
				index = pointAndDistance.index;
			}
		}
		// if the original road was the closest road we just have to cut off some of the
		// coordinates in the array and add the closest point as the end/beginning of
		// the array
		if (closestRoad.isTheSameRoadSegment(originalRoad)) {
			var copyOfRoad = originalRoad.getCopy();
			float[] cpCoords;

			// if we're getting the endsegments of the route, we want to put the closest
			// point as the last coordinate
			if (isEndSegment) {
				var oldCoords = Arrays.copyOfRange(copyOfRoad.coords, 0, index);
				cpCoords = new float[oldCoords.length + 2];
				for (int i = 0; i < oldCoords.length; i++) {
					cpCoords[i] = oldCoords[i];
				}
				cpCoords[cpCoords.length - 2] = closestPoint.getX();
				cpCoords[cpCoords.length - 1] = closestPoint.getY();

				// otherwise we put the closest point as the first coordinate
			} else {
				cpCoords = new float[copyOfRoad.coords.length - index + 2];
				cpCoords[0] = closestPoint.getX();
				cpCoords[1] = closestPoint.getY();
				var oldCoords = Arrays.copyOfRange(copyOfRoad.coords, index, copyOfRoad.coords.length);
				for (int i = 0; i < oldCoords.length; i++) {
					cpCoords[i + 2] = oldCoords[i];
				}
			}

			copyOfRoad.coords = cpCoords;
			return new ArrayList<>(List.of(copyOfRoad));

			// if the closest point is not found on the original road we want to keep the
			// original road in it's entirety and create a new road from the point where the
			// original road ends to the closest point
		} else {
			var copyOfRoad = closestRoad.getCopy();
			var oldCoords = Arrays.copyOfRange(copyOfRoad.coords, 0, index);
			float[] cpCoords = new float[oldCoords.length + 2];
			for (int i = 0; i < oldCoords.length; i++) {
				cpCoords[i] = oldCoords[i];
			}
			cpCoords[cpCoords.length - 2] = closestPoint.getX();
			cpCoords[cpCoords.length - 1] = closestPoint.getY();
			copyOfRoad.coords = cpCoords;

			return new ArrayList<>(List.of(originalRoad, copyOfRoad));
		}
	}
}
