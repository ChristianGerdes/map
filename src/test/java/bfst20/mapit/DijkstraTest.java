package bfst20.mapit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bfst20.mapit.datastructures.DijkstraSP;
import bfst20.mapit.datastructures.EdgeWeightedDigraph;
import bfst20.mapit.datastructures.SortedArrayList;
import bfst20.mapit.model.directions.RouteType;
import bfst20.mapit.model.directions.VehicleType;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.drawables.roads.RoadType;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.model.osm.OSMWay;

public class DijkstraTest {

    static EdgeWeightedDigraph graph;
    static Road road0;
    static Road road1;
    static Road road2;
    static Road road3;
    static Road road4;
    static Road road5;
    static Road road6;
    static Road road7;
    static Road road8;
    static Road FD;
    private static DijkstraSP dijkstra;
    private static HashMap<Long, Integer> idToIndex;
    private static SortedArrayList<OSMNode> idToNode;

    static OSMNode A;
    static OSMNode B;
    static OSMNode C;
    static OSMNode D;
    static OSMNode E;
    static OSMNode F;
    static OSMNode G;
    private static int vertices;

    @BeforeAll
    public static void init() {
        double speedLimit = 50;
        boolean oneway = false;
        boolean roundabout = false;
        RoadType roadType = RoadType.DEFAULTHIGHWAY;
        idToIndex = new HashMap<Long, Integer>();
        HashMap<Long, String> wayIdRoadName = new HashMap<Long, String>();
        idToNode = new SortedArrayList<>();

        A = new OSMNode(0, 1, 1);
        B = new OSMNode(1, 1, 2);
        C = new OSMNode(2, 3, 3);
        D = new OSMNode(3, 4, 4);
        E = new OSMNode(4, 4.5f, 3);
        F = new OSMNode(5, 3, 3.5f);
        G = new OSMNode(6, 10, 10);
        idToNode.add(A);
        idToNode.add(B);
        idToNode.add(C);
        idToNode.add(D);
        idToNode.add(E);
        idToNode.add(F);
        idToNode.add(G);

        long id = 0L;
        OSMWay way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(A);
        way.add(B);
        road0 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(B);
        way.add(F);
        road1 = new Road(way, RoadType.PATH, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(A);
        way.add(C);
        road2 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(E);
        way.add(C);
        road3 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(F);
        way.add(C);
        road4 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(E);
        way.add(D);
        road5 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(F);
        way.add(D);
        road6 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        graph = new EdgeWeightedDigraph((int) id, idToIndex);
        graph.addEdge(road0);
        graph.addEdge(road1);
        graph.addEdge(road2);
        graph.addEdge(road3);
        graph.addEdge(road4);
        graph.addEdge(road5);
        graph.addEdge(road6);

        vertices = (int) id;

    }

    @DisplayName("Test if has path to method works with SHORTEST")
    @Test
    void HasPathToSHORTESTTest() {
        var dijkstra = new DijkstraSP(graph, VehicleType.CAR, RouteType.SHORTEST, A, D, idToIndex, idToNode);
        assertTrue(dijkstra.hasPathTo((int) D.id));
    }

    @DisplayName("Test if has path to method works with FASTEST")
    @Test
    void HasPathToFASTESTTest() {
        var dijkstra = new DijkstraSP(graph, VehicleType.CAR, RouteType.FASTEST, A, D, idToIndex, idToNode);
        assertTrue(dijkstra.hasPathTo((int) D.id));
    }

    @DisplayName("Test we take the correct route")
    @Test
    void PathToTest() {
        var dijkstra = new DijkstraSP(graph, VehicleType.CAR, RouteType.FASTEST, A, D, idToIndex, idToNode);
        var route = dijkstra.pathTo((int) D.id);
        int[] actual = new int[] { 6, 4, 2 };
        int index = 0;
        for (Road r : route) {
            // remember the route is backwards
            assertEquals(r.id, actual[index++]);
        }
    }

    @DisplayName("Gets relaxed roads as a list")
    // relaxed in this pattern
    // relax vertex: 0, road: 2
    // relax vertex: 0, road: 0
    // relax vertex: 1, road: 1
    // relax vertex: 1, road: 0
    // relax vertex: 2, road: 4
    // relax vertex: 2, road: 3
    // relax vertex: 2, road: 2
    // relax vertex: 5, road: 6
    @Test
    void GetRelaxedRoadsTest() {
        var dijkstra = new DijkstraSP(graph, VehicleType.CAR, RouteType.SHORTEST, A, D, idToIndex, idToNode);
        List<Road> actual = dijkstra.getRelaxedRoads();
        int[] expected = new int[] { 2, 0, 1, 0, 4, 3, 2, 6 };
        for (int i = 0; i < actual.size(); i++) {
            // System.out.println(actual.get(i).id);
            assertEquals(expected[i], actual.get(i).id);
        }
    }

    @DisplayName("You can't choose the fastest route, if you are not in a car.")
    @Test
    void CantChooseThatCombinationTest() {
        try {
            var dijkstra = new DijkstraSP(graph, VehicleType.BIKE, RouteType.FASTEST, A, D, idToIndex, idToNode);
        } catch (Exception e) {
            String actual = "Can't choose fastest road if not in a car.";
            assertEquals(e.getMessage(), actual);
        }
    }

    @DisplayName("Expect IllegaArgumentException")
    @Test
    void IllegalArgumentExceptionTest() {
        OSMNode notInTheMap = new OSMNode(100L, 1, 1);
        try {
            var dijkstra = new DijkstraSP(graph, VehicleType.BIKE, RouteType.SHORTEST, A, notInTheMap, idToIndex,
                    idToNode);
        } catch (Exception e) {
            String actual = "Id not found";
            assertEquals(e.getMessage(), actual);
        }
    }

    @DisplayName("Not valid vertex")
    @Test
    void NotValidVertexTest() {
        // OSMNode notInTheMap = new OSMNode(100L, 1, 1);
        try {
            var dijkstra = new DijkstraSP(graph, VehicleType.BIKE, RouteType.SHORTEST, A, D, idToIndex, idToNode);

            dijkstra.hasPathTo(100);

        } catch (Exception e) {
            String actual = "vertex 100 is not between 0 and " + (vertices - 1);
            assertEquals(e.getMessage(), actual);
        }
    }

    @DisplayName("Not valid vertex, because it's negative")
    @Test
    void NotValidVertexNegativeTest() {
        // OSMNode notInTheMap = new OSMNode(100L, 1, 1);
        try {
            var dijkstra = new DijkstraSP(graph, VehicleType.BIKE, RouteType.SHORTEST, A, D, idToIndex, idToNode);

            dijkstra.hasPathTo(-1);

        } catch (Exception e) {
            String actual = "vertex -1 is not between 0 and " + (vertices - 1);
            assertEquals(e.getMessage(), actual);
        }
    }

    @DisplayName("No pathTo a valid vertex")
    @Test
    void NoPathToTest() {
        var dijkstra = new DijkstraSP(graph, VehicleType.BIKE, RouteType.SHORTEST, A, E, idToIndex, idToNode);
        // OSMNODE G=6
        assertNull(dijkstra.pathTo(6));
    }

}