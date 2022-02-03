package bfst20.mapit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bfst20.mapit.datastructures.EdgeWeightedDigraph;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.drawables.roads.RoadType;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.model.osm.OSMWay;

public class EdgeWeightedDigraphTest {

    static EdgeWeightedDigraph graph;
    static Road road0;
    static Road road1;
    static Road road2;
    static Road road3;
    static Road road4;
    static Road road5;

    @BeforeAll
    public static void init() {
        double speedLimit = 50;
        boolean oneway = false;
        boolean roundabout = false;
        RoadType roadType = RoadType.DEFAULTHIGHWAY;
        HashMap<Long, Integer> idToIndex = new HashMap<Long, Integer>();
        HashMap<Long, String> wayIdRoadName = new HashMap<Long, String>();

        OSMNode node0 = new OSMNode(0, 1, 1);
        OSMNode node1 = new OSMNode(1, 2, 1);
        OSMNode node2 = new OSMNode(2, 3, 1);
        OSMNode node3 = new OSMNode(3, 4, 1);
        OSMNode node4 = new OSMNode(4, 5, 1);

        long id = 0L;
        idToIndex.put(id, (int) id++);
        OSMWay way = new OSMWay(id);
        way.add(node0);
        way.add(node1);
        road0 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(node1);
        way.add(node4);
        road1 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(node0);
        way.add(node4);
        road2 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(node4);
        way.add(node2);
        road3 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(node2);
        way.add(node3);
        road4 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        way = new OSMWay(id);
        idToIndex.put(id, (int) id++);
        way.add(node4);
        way.add(node3);
        road5 = new Road(way, roadType, speedLimit, oneway, roundabout, wayIdRoadName);

        graph = new EdgeWeightedDigraph(5, idToIndex);
        graph.addEdge(road0);
        graph.addEdge(road1);
        graph.addEdge(road2);
        graph.addEdge(road3);
        graph.addEdge(road4);
        graph.addEdge(road5);

    }

    @DisplayName("Count vertices and edges in graph")
    // It is the double amount of edges since one can travel on roads in both
    // directions,
    // so the roads are added twice.
    @Test
    void VerticesAndEdgesCountTest() {

        int edges = graph.numOfEdges();
        int vertices = graph.numOfVertices();

        assertEquals(5, vertices, "5 vertices");
        assertEquals(12, edges, "12 edges ");
    }

    /*
    *  
    */
    @DisplayName("Prints list of adjacent to a specific vertex")
    @Test
    public void AdjacentVerticesTest() {
        var expectedIterator = graph.adj(4);
        Road[] actual = new Road[4];
        Road[] expected = new Road[4];
        actual[0] = road5;
        actual[1] = road3;
        actual[2] = road2;
        actual[3] = road1;

        int index = 0;
        for (Road r : expectedIterator) {
            expected[index++] = r;
        }

        for (int i = 0; i < expected.length; i++) {
            assertEquals(0, actual[i].compareTo(expected[i]));
        }
    }
}