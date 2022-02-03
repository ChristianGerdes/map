package bfst20.mapit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import bfst20.mapit.datastructures.Point;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.drawables.roads.RoadType;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.model.osm.OSMWay;
import bfst20.mapit.util.ClosestPoint;

public class ClosestDistanceTest {
  @Test
  void ClosestPointOnRoadWith3Coords() {
    var queryPoint = new Point(0.6, 2);

    var nodeA = new OSMNode(0, 1, 1);
    var nodeB = new OSMNode(1, 1.2f, 2.2f);
    var nodeC = new OSMNode(2, 3, 3);

    var way = new OSMWay(0);
    way.addAll(List.of(nodeA, nodeB, nodeC));
    var road = new Road(way, RoadType.DEFAULTHIGHWAY, 50, false, false, null);
    var actual = ClosestPoint.getClosestPoint(queryPoint, road);

    float expectedX = 1.1513515f;
    float expectedY = 1.908108f;
    // it's because its using the haversine formula, the euclidean distance is 0.55
    double expectedDist = 62.15294113719859;
    System.out.println(actual.point.getX());
    System.out.println(actual.point.getY());
    System.out.println(actual.distance);
    assertEquals(expectedX, actual.point.getX());
    assertEquals(expectedY, actual.point.getY());
    assertEquals(expectedDist, actual.distance);
  }

  @Test
  void ClosestPointOnRoadWith2Coords() {
    var queryPoint = new Point(0, 0);

    var nodeA = new OSMNode(0, 1, 1);
    var nodeC = new OSMNode(2, 3, 3);

    var way = new OSMWay(0);
    way.addAll(List.of(nodeA, nodeC));
    var road = new Road(way, RoadType.DEFAULTHIGHWAY, 50, false, false, null);
    var actual = ClosestPoint.getClosestPoint(queryPoint, road);

    float expectedX = 1;
    float expectedY = 1;
    assertEquals(expectedX, actual.point.getX());
    assertEquals(expectedY, actual.point.getY());
  }

}