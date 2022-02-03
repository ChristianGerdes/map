package bfst20.mapit.model.io;

import java.util.HashMap;
import java.io.Serializable;
import bfst20.mapit.model.TreeType;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.datastructures.Tree.Tree;
import bfst20.mapit.datastructures.SortedArrayList;
import bfst20.mapit.datastructures.EdgeWeightedDigraph;

public class SavedFile implements Serializable {
	private static final long serialVersionUID = -7528920990109379193L;

	private Tree[] trees;
	private float[] bounds;
	private EdgeWeightedDigraph graph;
	private HashMap<Long, Integer> idToIndex;
	private SortedArrayList<OSMNode> idToNode;

	private HashMap<Long, String> wayIdToRoadname;

	public Tree[] getTrees() {
		if (this.trees == null) {
			this.trees = new Tree[TreeType.values().length];

			for (int i = 0; i < TreeType.values().length; i++) {
				this.trees[i] = new Tree();
			}
		}

		return this.trees;
	}

	public void setTrees(Tree[] trees) {
		this.trees = trees;
	}

	public float[] getBounds() {
		if (bounds == null) {
			this.bounds = new float[4];
		}

		return bounds;
	}

	public void setBounds(float[] bounds) {
		this.bounds = bounds;
	}

	public void setGraph(EdgeWeightedDigraph graph) {
		this.graph = graph;
	}

	public EdgeWeightedDigraph getGraph() {
		return graph;
	}

	public HashMap<Long, Integer> getIdToIndexHashMap() {
		return idToIndex;
	}

	public void setIdToIndexHashMap(HashMap<Long, Integer> idToIndex) {
		this.idToIndex = idToIndex;
	}

	public SortedArrayList<OSMNode> getIdToNodeList() {
		return idToNode;
	}

	public void setIdToNode(SortedArrayList<OSMNode> idToNode) {
		this.idToNode = idToNode;
	}

	public HashMap<Long, String> getWayIdToRoadname() {
		return wayIdToRoadname;
	}

	public void setWayIdToRoadname(HashMap<Long, String> wayIdToRoadname) {
		this.wayIdToRoadname = wayIdToRoadname;
	}

}