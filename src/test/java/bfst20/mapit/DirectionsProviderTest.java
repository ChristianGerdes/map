package bfst20.mapit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bfst20.mapit.datastructures.DirectionsProvider;
import bfst20.mapit.model.directions.Direction;
import bfst20.mapit.model.directions.DirectionInformation;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.drawables.roads.RoadType;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.model.osm.OSMWay;

public class DirectionsProviderTest {

  private static List<Road> roads;

  @BeforeAll
  public static void init() {
    roads = new ArrayList<>();
    double speedLimit = 50;
    boolean oneway = true;
    boolean roundabout = false;
    RoadType roadType = RoadType.DEFAULTHIGHWAY;
    HashMap<Long, String> wayIdRoadName = new HashMap<Long, String>();
    for (int i = 0; i < 10; i++) {
      float[] coords;
      if (i % 2 == 0) {
        coords = new float[] { i + 1, i + 2, i + 3, i + 4 };
      } else {
        coords = new float[] { i + 1, i - 2, i + 3, i - 4 };
      }
      long id = i;
      wayIdRoadName.put(id, "This is a road " + id);
      OSMWay way = new OSMWay(id);
      OSMNode node0 = new OSMNode((long) Math.floor(Math.random() * 3000000), coords[0], coords[1]);
      OSMNode node1 = new OSMNode((long) Math.floor(Math.random() * 3000000), coords[2], coords[3]);
      way.add(node0);
      way.add(node1);
      var road = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);
      roads.add(road);
    }

  }

  @Test
  public void GenerateDirectionsTest() {
    var directionsProvider = new DirectionsProvider(roads, null);
    int index = 0;

    for (DirectionInformation d : directionsProvider) {
      if (index == 8) {
        // the last direction you have reached is your destination
        break;
      }
      if (index % 2 == 0) {
        assertEquals(Direction.SHARP_RIGHT, d.getDirection());
      } else {
        assertEquals(Direction.SHARP_LEFT, d.getDirection());
      }
      index++;
    }
  }
}