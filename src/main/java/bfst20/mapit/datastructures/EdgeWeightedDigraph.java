package bfst20.mapit.datastructures;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import bfst20.mapit.model.drawables.roads.Road;

/**
 * EdgeWeightedDigraph
 */
@SuppressWarnings("unchecked")
public class EdgeWeightedDigraph implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 933701562475403192L;

  private final int vertices; // number of vertices in this digraph
  private int edges; // number of edges in this digraph
  private Bag<Road>[] adj; // adj[v] = adjacency list for vertex v
  private int[] indegree; // indegree[v] = indegree of vertex v
  private Map<Long, Integer> idToIndex;

  public EdgeWeightedDigraph(int numOfVertices, HashMap<Long, Integer> idToIndex) {
    if (numOfVertices < 0)
      throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
    this.idToIndex = idToIndex;
    this.vertices = numOfVertices;
    this.edges = 0;
    this.indegree = new int[numOfVertices];
    this.adj = (Bag<Road>[]) new Bag[numOfVertices];
    for (int v = 0; v < numOfVertices; v++) {
      adj[v] = new Bag<Road>();
    }
  }

  private void validateVertex(int v) {
    if (v < 0 || v >= vertices)
      throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (vertices - 1));
  }

  // maps the long OSM ID to an index created when XML is parsed
  private int getIndexFromId(long id) {
    if (idToIndex.containsKey(id)) {
      return idToIndex.get(id);
    } else {
      throw new IllegalArgumentException(id + " was not found.");
    }
  }

  public void addEdge(Road road) {
    int startVertex = getIndexFromId(road.getStartVertex());
    int endVertex = getIndexFromId(road.getEndVertex());
    // contructor that returns a the same road but with endvertex, startvertex and
    // the coordinat array reversed.
    Road reversedRoad = new Road(road);
    validateVertex(startVertex);
    validateVertex(endVertex);
    // adds the roads to the adjacency lists
    adj[startVertex].add(road);
    adj[endVertex].add(reversedRoad);
    // increment the indegree
    indegree[endVertex]++;
    indegree[startVertex]++;
    // add 2 to edges because we are inserting the road forwards and backwards
    edges += 2;
  }

  /**
   * @return a list of all edges (roads) in the graph
   */
  public Iterable<Road> edges() {
    Bag<Road> list = new Bag<Road>();
    for (int v = 0; v < vertices; v++) {
      for (Road e : adj(v)) {
        list.add(e);
      }
    }
    return list;
  }

  public int numOfVertices() {
    return vertices;
  }

  public int numOfEdges() {
    return edges;
  }

  /**
   * 
   * @param nodeId
   * @return all adjacent roads for a given nodeId
   */
  public Iterable<Road> adj(long nodeId) {
    int v = getIndexFromId(nodeId);
    validateVertex(v);
    return adj[v];
  }

  /**
   * 
   * @param index
   * @return all adjacent roads for a given index
   */

  public Iterable<Road> adj(int index) {
    validateVertex(index);
    return adj[index];
  }

}