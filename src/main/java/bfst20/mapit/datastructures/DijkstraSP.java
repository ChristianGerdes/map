package bfst20.mapit.datastructures;

import static bfst20.mapit.util.utilFunctions.distanceInKmBetweenCoordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.directions.RouteType;
import bfst20.mapit.model.directions.VehicleType;

/**
 * DijkstraSP
 */

public class DijkstraSP {
  private double[] distTo; // distTo[v] = distance of shortest s->v path
  private Road[] edgeTo; // edgeTo[v] = last edge on shortest s->v path
  private IndexMinPQ<Double> pq; // priority queue of vertices
  private HashMap<Long, Integer> idToIndex; // map from OSMNode id to array index
  private List<Road> relaxedRoads; // Used for displaying the relaxed roads
  private OSMNode endOfRoute;
  private SortedArrayList<OSMNode> idToNode;
  private VehicleType vehicleType;
  private RouteType routeType;

  /*
   * *
   * 
   * @param G
   * 
   * @param startOfRoute
   * 
   * @param endOfRoute
   * 
   * @param idToIndex
   */
  public DijkstraSP(EdgeWeightedDigraph G, VehicleType vehicleType, RouteType routeType, OSMNode startOfRoute,
      OSMNode endOfRoute, HashMap<Long, Integer> idToIndex, SortedArrayList<OSMNode> idToNode) {
    if (vehicleType != VehicleType.CAR && routeType == RouteType.FASTEST) {
      throw new IllegalArgumentException("Can't choose fastest road if not in a car.");
    }
    long time = -System.nanoTime();
    this.vehicleType = vehicleType;
    this.idToIndex = idToIndex;
    this.relaxedRoads = new ArrayList<>();
    this.endOfRoute = endOfRoute;
    this.idToNode = idToNode;
    this.routeType = routeType;

    try {
      int startVertexIndex = idToIndex.get(startOfRoute.id);
      int endVertexIndex = idToIndex.get(endOfRoute.id);

      distTo = new double[G.numOfVertices()];
      edgeTo = new Road[G.numOfVertices()];

      // intialize the distTo array for every vertex
      for (int v = 0; v < G.numOfVertices(); v++) {
        distTo[v] = Double.POSITIVE_INFINITY;
      }

      distTo[startVertexIndex] = 0.0;

      // relax vertices in order of distance from s
      pq = new IndexMinPQ<Double>(G.numOfVertices());
      pq.insert(startVertexIndex, distTo[startVertexIndex]);
      while (!pq.isEmpty()) {
        // gets the index with the shortest distance to the starting point
        int v = pq.delMin();
        // calls relax for each road connected to that vertex (intersection)
        for (Road e : G.adj(v)) {
          // used for debugging purposes
          relaxedRoads.add(e);

          relax(e);
          // Checks if the endVertex has been found and terminates the search
          int w = idToIndex.get(e.getEndVertex());
          if (w == endVertexIndex) {
            time += System.nanoTime();
            System.out.printf("Dijkstra (A*) completed in: %.3fms\n", time / 1e6);
            return;
          }
        }
      }
    } catch (Exception e) {
      // throws error if the OSM id doesn't have a corresponding index
      throw new IllegalArgumentException("Id not found");
    }
  }

  /**
   * 
   * @return a list of all the roads relaxed during the algorithm it's used for
   *         debugging purposes
   */
  public List<Road> getRelaxedRoads() {
    return relaxedRoads;
  }

  private void relax(Road e) {
    // retrieves the index of the start and end vertices of the road
    int startVertex = idToIndex.get(e.getStartVertex());
    int endVertex = idToIndex.get(e.getEndVertex());

    // gets the last OSMNode in the current road
    // it's used to calculate heuristic used in A*
    OSMNode endNodeInRoad = idToNode.get(e.getEndVertex());

    // Checks if the endVertex of the road is getting us further away from the
    // distance to the source + the weight of the roads
    // (usually just the length of the road)
    // and we are allowed to traverse it
    if (distTo[endVertex] > distTo[startVertex] + e.getWeight(routeType, vehicleType)
        && e.isTraversingAllowed(vehicleType)) {
      // the distance from the source to endvertex is now the distance to the
      // startVertex + the weight of the road
      distTo[endVertex] = distTo[startVertex] + e.getWeight(routeType, vehicleType);
      // now we now that the shortest path to the end vertex is through the road e;
      edgeTo[endVertex] = e;

      if (pq.contains(endVertex)) {
        // if in pq update the distTo
        // the heuristic will ALWAYS be smaller if getting closer to the target
        // and therefore the best guesses on the shortest path will be evaluated first
        // in the priority queue
        pq.decreaseKey(endVertex, distTo[endVertex] + calcHeuristics(endNodeInRoad));
      } else {
        // else insert
        pq.insert(endVertex, distTo[endVertex] + calcHeuristics(endNodeInRoad));
      }
    }
  }

  private void validateVertex(int v) {
    int V = distTo.length;
    if (v < 0 || v >= V) {
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }
  }

  /**
   * 
   * @param v index of vertex
   * @return if the given vertex has been relaxed
   */
  public boolean hasPathTo(int v) {
    validateVertex(v);
    return distTo[v] < Double.POSITIVE_INFINITY;
  }

  // Heuristic for A*
  /**
   * If the routeType == SHORTEST then it returns the remaining distance in KM
   * else it returns the distance divided by 130, because it is the fastest
   * speedlimit in DK
   * 
   * @param currentVertex
   * @return a heuristic of how long there is from the currentVertex to the end of
   *         the route
   */
  private double calcHeuristics(OSMNode currentVertex) {
    // returns a he
    double h = distanceInKmBetweenCoordinates(currentVertex.lat, currentVertex.lon, endOfRoute.lat, endOfRoute.lon);
    return routeType == RouteType.SHORTEST ? h : h / 130.0;
  }

  /**
   * 
   * @param v index of the end vertex
   * @return Iterable of the shortest path to the given point
   */
  public Iterable<Road> pathTo(int v) {
    if (!hasPathTo(v)) {
      return null;
    }
    Stack<Road> path = new Stack<Road>();
    for (Road e = edgeTo[v]; e != null; e = edgeTo[idToIndex.get(e.getStartVertex())]) {
      path.push(e);
    }
    return path;
  }

}