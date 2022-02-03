package bfst20.mapit.datastructures;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.drawables.roads.RoadType;
import bfst20.mapit.model.directions.Direction;
import bfst20.mapit.model.directions.DirectionInformation;

public class DirectionsProvider implements Iterable<DirectionInformation> {
  private List<Road> route;
  public Queue<DirectionInformation> directions;
  private boolean isInRoundabout = false;
  private int exits = 0;
  private EdgeWeightedDigraph graph;
  private Direction onRampDirection;
  private double rampLength = 0;

  public DirectionsProvider(List<Road> route, EdgeWeightedDigraph g) {
    Collections.reverse(route);
    this.route = route;
    this.graph = g;
    this.directions = new Queue<>();
    generateDirections();
  }

  public boolean isEmpty() {
    return directions.isEmpty();
  }

  private void generateDirections() {
    double currentLength = 0;
    for (int i = 0; i < route.size() - 1; i++) {
      var previousRoad = route.get(i);
      var nextRoad = route.get(i + 1);
      var dir = getDirection(previousRoad, nextRoad, currentLength);
      if (dir != null) {
        directions.enqueue(dir);
        currentLength = 0;

      } else if (isInRoundabout && nextRoad.isRoundabout()) {
        var intersectionNodeId = nextRoad.getStartVertex();

        int numberOfRoads = 0;
        boolean exit = false;
        for (Road r : graph.adj(intersectionNodeId)) {
          numberOfRoads++;
          if (!r.isRoundabout() && isValidExit(r.getRoadType())) {
            if (r.oneway && r.getStartVertex() == intersectionNodeId && r.isForward()) {
              exit = true;
            } else if (!r.oneway) {
              exit = true;
            }
          }
        }
        if (numberOfRoads >= 3 && exit) {
          exits++;
        }

      } else if (isInRoundabout && !nextRoad.isRoundabout()) {
        exits++;
        directions.enqueue(new DirectionInformation(Direction.ROUNDABOUT, nextRoad.getName(), exits, currentLength, previousRoad.getCoords()));
        exits = 0;
        currentLength = 0;
      } else {
        currentLength += previousRoad.getLength();
      }
    }

    var lastRoad = route.get(route.size() - 1);
    directions.enqueue(
        new DirectionInformation(Direction.END, lastRoad.getName(), lastRoad.getLength() + currentLength, lastRoad.getCoords()));
  }

  private boolean isValidExit(RoadType roadType) {
    switch (roadType) {
      case CYCLEWAYROAD:
      case PATH:
        return false;
      default:
        return true;
    }
  }

  private DirectionInformation getDirection(Road previousRoad, Road nextRoad, double currentLength) {
    isInRoundabout = false;

    if (previousRoad.isRoundabout()) {
      isInRoundabout = true;
      return null;
    }
    if (nextRoad.isRoundabout()) {
      return null;
    }
    // formula https://stackoverflow.com/a/31334882
    Point intersectionPoint = new Point(previousRoad.coords[previousRoad.coords.length - 2], previousRoad.coords[previousRoad.coords.length - 1]);
    Point lastInPrevRoad = new Point(previousRoad.coords[previousRoad.coords.length - 4], previousRoad.coords[previousRoad.coords.length - 3]);
    Point firstInNextRoad = new Point(nextRoad.coords[2], nextRoad.coords[3]);

    double angle1 = Math.atan2(firstInNextRoad.getY() - intersectionPoint.getY(),
        firstInNextRoad.getX() - intersectionPoint.getX());
    double angle2 = Math.atan2(lastInPrevRoad.getY() - intersectionPoint.getY(),
        lastInPrevRoad.getX() - intersectionPoint.getX());

    double result = angle1 - angle2;
    // if the angle in radians is negative add 2PI
    double radians = result > 0 ? result : result + 2 * Math.PI;
    double degrees = Math.toDegrees(radians);

    // The angle is clockwise
    Direction direction = null;
    if (degrees >= 160 && degrees <= 200) {
      direction = Direction.STRAIGHT;
    } else if (degrees < 60) {
      direction = Direction.SHARP_LEFT;
    } else if (degrees > 130 && degrees < 180) {
      direction = Direction.WEAK_LEFT;
    } else if (degrees < 160) {
      direction = Direction.LEFT;
    } else if (degrees < 230) {
      direction = Direction.WEAK_RIGHT;
    } else if (degrees > 230 && degrees < 300) {
      direction = Direction.RIGHT;
    } else {
      direction = Direction.SHARP_RIGHT;
    }

    if (previousRoad.getRoadType() == RoadType.MOTORWAY_LINKROAD) {
      if (nextRoad.getRoadType() != RoadType.MOTORWAY_LINKROAD && nextRoad.getRoadType() != RoadType.MOTORWAYROAD) {
        // GOES OFF THE MOTORWAY
        double tempLength = rampLength;
        rampLength = 0;
        return new DirectionInformation(Direction.OFFRAMP, nextRoad.getName(),
            previousRoad.getLength() + currentLength + tempLength, true, previousRoad.getCoords());

      } else if (nextRoad.getRoadType() == RoadType.MOTORWAY_LINKROAD) {
        // CONTINUES ON RAMP

        return null;
      } else if (nextRoad.getRoadType() == RoadType.MOTORWAYROAD) {
        // GOES ON THE MOTORWAY
        rampLength = previousRoad.getLength();
        return new DirectionInformation(onRampDirection, nextRoad.getName(), currentLength, previousRoad.getCoords());
      }
    } else if (previousRoad.getRoadType() != RoadType.MOTORWAY_LINKROAD
        && nextRoad.getRoadType() == RoadType.MOTORWAY_LINKROAD) {

      // GOES ON THE ONRAMP
      onRampDirection = direction;
      return null;

    }

    // if the road continues on the same road we return null, and adds the distance
    // to the next DirectionInformation
    if (previousRoad.getName().equals(nextRoad.getName()) && (degrees > 130 && degrees < 230)) {
      return null;
    }
    return new DirectionInformation(direction, nextRoad.getName(),
        previousRoad.getLength() + currentLength, previousRoad.getCoords());
  }

  @Override
  public Iterator<DirectionInformation> iterator() {
    return new DirectionsProviderIterator<DirectionInformation>(directions);
  }

  private class DirectionsProviderIterator<T> implements Iterator<DirectionInformation> {

    Queue<DirectionInformation> route;

    public DirectionsProviderIterator(Queue<DirectionInformation> route) {
      this.route = route;
    }

    @Override
    public boolean hasNext() {
      return !route.isEmpty();
    }

    @Override
    public DirectionInformation next() {
      return route.dequeue();
    }

  }
}
