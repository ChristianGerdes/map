package bfst20.mapit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bfst20.mapit.model.directions.VehicleType;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.drawables.roads.RoadType;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.model.osm.OSMWay;

public class RoadTest {

  private static Road road;

  @BeforeAll
  public static void init() {
    double speedLimit = 50;
    boolean oneway = true;
    boolean roundabout = false;
    RoadType roadType = RoadType.DEFAULTHIGHWAY;
    HashMap<Long, String> wayIdRoadName = new HashMap<Long, String>();
    wayIdRoadName.put(0L, "TEST NAME");
    float[] coords = new float[] { 1, 2, 3, 4 };
    long id = 0L;
    OSMWay way = new OSMWay(id);
    OSMNode node0 = new OSMNode(0, coords[0], coords[1]);
    OSMNode node1 = new OSMNode(0, coords[2], coords[3]);
    way.add(node0);
    way.add(node1);
    road = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);
  }

  // @AfterEach
  // public static void cleanUp() {
  // road = null;
  // }

  @DisplayName("Reverse Coordinates Method")
  @Test
  void reversedCoordsTest() {
    float[] correctReversed = new float[] { 3, 4, 1, 2 };
    float[] reversedCoords = new Road(road).coords;
    assertArrayEquals(correctReversed, reversedCoords);
  }

  @DisplayName("Test the .clone() interface")
  @Test
  void getCopyAsRouteTest() {
    var copyOfRoad = road.getCopyAsRoute();

    assertEquals(RoadType.ROUTE, copyOfRoad.getRoadType());
  }

  @Test
  void isTraversingAllowedTest() {
    // returns false
    road.setRoadType(RoadType.MOTORWAYROAD);
    boolean actual0 = road.isTraversingAllowed(VehicleType.PEDESTRIAN);
    assertFalse(actual0);
    // returns true
    road.setRoadType(RoadType.MOTORWAYROAD);
    boolean actual1 = road.isTraversingAllowed(VehicleType.CAR);
    assertTrue(actual1);

    /**
     * returns false
     * 
     * going the wrong direction on a onewayroad
     */
    boolean actual2 = new Road(road).isTraversingAllowed(VehicleType.CAR);
    assertFalse(actual2);
  }

  @Test
  void getNameTest() {
    assertEquals("TEST NAME", road.getName());
  }

}