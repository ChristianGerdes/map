package bfst20.mapit.datastructures.Tree;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Collections;
import bfst20.mapit.Drawable;
import java.util.PriorityQueue;
import bfst20.mapit.datastructures.Point;

/**
 * R*-Tree with STR-packing
 * Inspired by https://github.com/davidmoten/rtree
 *
 * http://dbs.mathematik.uni-marburg.de/publications/myPapers/1990/BKSS90.pdf
 *
 */
public class Tree implements Serializable {
    private static final long serialVersionUID = -981898166997378786L;
    private int maxEntries = 25;
    private int minEntries = 2;
    public int size = 0;
    public Node root;

    public Tree() {
        this.root = this.makeNewRoot(true);
    }

    public Tree(ArrayList<Drawable> drawables) {
        this.root = this.makeNewRoot(true);

        this.performPacking(drawables);
    }

    /**
     * Insert drawable into tree
     *
     * @param drawable
     */
    public void insert(Drawable drawable) {
        this.insert(drawable.mbr(), drawable);
    }

    /**
     * Insert a drawable with a given mbr (Minimal bounding rectangle)
     * into a the optimal leaf node. The optimal leaf node is determined by
     * overlap, area increase and then total area. Optimal non-leaf nodes are found based
     * on area increase and then total area not taking overlap into account.
     *
     *
     * @param mbr       The rectangle containing the drawable
     * @param value     The drawable to insert
     */
    private void insert(Rectangle mbr, Drawable value) {
        // Make a new entry with the drawable
        Entry entry = new Entry(mbr, value);

        // Choose leaf-subtree
        Node leaf = this.chooseSubtree(this.root, entry);

        // Add the entry to the leaf
        leaf.add(entry);

        // In case the node overflows, split the leaf upwards
        if (leaf.children.size() > this.maxEntries) {
            this.split(leaf);
        }

        // Increase the tree size
        this.size++;
    }

    /**
     * Search through tree for all drawables within a given range
     *
     * @param range         A rectangle describing the search range
     * @param results       A list of results
     * @return              A list of all found drawables
     */
    public ArrayList<Drawable> search(Rectangle range, ArrayList<Drawable> results) {
        this.search(range, this.root, results);

        return results;
    }

    /**
     * Perform the search starting from the root. Searching is done by looking at intersections
     * between our range and mbr of both non-leaf nodes and leaf nodes including entries (Entry).
     *
     * @param range         Our search range rectangle
     * @param node          The given node examined
     * @param results       List of results
     */
    private void search(Rectangle range, Node node, ArrayList<Drawable> results) {
        // If the entire node is inside the range, then just add all drawables recursively
        if (range.contains(node.mbr)) {
            this.addAllEntries(node, results);

            return;
        }

        // Loop the the children
        for (Node child : node.children) {
            // Check if the range intersects the child's mbr
            if (range.intersects(child.mbr)) {

                // If the node is a leaf, then add the child
                // Otherwise perform search on the child (non-leaf node)
                if (node.leaf) {
                    results.add(((Entry) child).value);
                } else {
                    this.search(range, child, results);
                }
            }
        }
    }

    /**
     * Find the nearest neighbour from a given point
     *
     * @param point     Point to query from
     * @return The nearest drawable found
     */
    public Nearest nearest(Point point) {
        return this.nearest(point, this.root);
    }

    /**
     * Perform nearest neighbour on a node.
     *
     * 1. Sort the node's children based on shortest distance from point to their mbr.
     * 2. Take the first (closest) child (currentNode) of the sorted list (queue)
     * 3. If the currentNode is a leaf, return its closest child
     * 4. For non-leaf nodes, perform nearest method recursively on them returning a candicate for nearest neighbour
     * 5. Then in a loop, check if any of children from step 1 are closer than our candicate
     * 6. Perform nearest method on child, if one was closer
     * 7. Check if the found distance if smaller than our candicate - if so update the candicate
     *
     * @param point
     * @param node
     * @return
     */
    private Nearest nearest(Point point, Node node) {
        if (node.children.size() == 0) {
            return new Nearest(node, node.distance(point));
        }

        // Sort the children based on distance from point to their mbr
        PriorityQueue<Node> queue = new PriorityQueue<Node>(node.children.size(), new NodeComparator(point));
        queue.addAll(node.children);

        // Pull the first one of the queue
        Node currentNode = queue.poll();

        // If leaf node, return its closest child
        if (currentNode.leaf) {
            Node entry = Collections.min(currentNode.children, new NodeComparator(point));

            return new Nearest(entry, entry.distance(point));
        }

        // If non-leaf node, perform nearest on the current node
        Nearest bestNode = this.nearest(point, currentNode);
        // double bestDistance = bestNode.distance(point);

        // Check if any children are closer than our bestNode
        while ( ! queue.isEmpty() && queue.peek().distance(point) <= bestNode.distance) {
            // Perform nearest on examined child
            Nearest examinedNearest = this.nearest(point, queue.poll());

            // Check if distance is shorter than bestDistance
            if (examinedNearest.distance <= bestNode.distance) {
                bestNode = examinedNearest;
            }
        }

        return bestNode;
    }

    /**
     * Build RTree from a complete list of drawables by sorting before inserting.
     *
     * 1. Sort all nodes by their center x value
     * 2. Devide all nodes into slides of the same size
     * 3. Sort each slice of nodes by their center y value
     * 4. Devide all slices into another set of slices of the same size
     * 5. Insert drawables from the sorted list
     *
     * @param drawables     List of drawables to insert
     */
    public void performPacking(List<Drawable> drawables) {
        float leafs = (float) drawables.size() / this.maxEntries;
        int verticalSlices = (int) Math.ceil(Math.sqrt(leafs));
        int maxNodes = (int) Math.ceil((float) drawables.size() / verticalSlices);

        // Sort drawables by center x
        Collections.sort(drawables, new MidComparator((short) 0));

        ArrayList<ArrayList<Drawable>> slices = new ArrayList<>();

        int count = 0;
        int maxCount = maxNodes;

        for (int i = 0; i < verticalSlices; i++) {
            // Insert slice into slices
            ArrayList<Drawable> slice = i == verticalSlices - 1
                ? new ArrayList<>(drawables.subList(count, drawables.size()))
                : new ArrayList<>(drawables.subList(count, maxCount));

            slices.add(slice);

            count += maxNodes;
            maxCount += (maxNodes);
        }

        for (int i = 0; i < slices.size(); i++) {
            // Sort slice drawables by center y
            Collections.sort(slices.get(i), new MidComparator((short) 1));

            for (int j = 0; j < slices.get(i).size(); j++) {
                Drawable drawable = slices.get(i).get(j);

                // Insert the drawable into the tree
                this.insert(drawable.mbr(), drawable);
            }
        }
    }

    /**
     * Clear the tree by creatig a new root
     */
    public void clear() {
        this.size = 0;
        this.root = this.makeNewRoot(true);
    }

    private static class MidComparator implements Comparator<Drawable> {
        private short dimension;

        public MidComparator(short dim) {
            dimension = dim;
        }

        public int compare(Drawable drawable1, Drawable drawable2) {
            return Float.compare(
                drawable1.getCoords()[dimension] + drawable1.getDimensions()[dimension] / 2,
                drawable2.getCoords()[dimension] + drawable2.getDimensions()[dimension] / 2
            );
        }
    }

    private void addAllEntries(Node node, ArrayList<Drawable> results) {
        for (Node child : node.children) {
            if (node.leaf) {
                results.add(((Entry) child).value);
            } else {
                this.addAllEntries(child, results);
            }
        }
    }

    /**
     * Split a node in the tree
     *
     * @param node      Node to split
     */
    private void split(Node node)
    {
        // Let our splitter find an optimal split so
        // overlap, area increase and total area is reduced
        // Return a pair containing the other nodes as children
        Pair pair = new Splitter().split(node, node.children, this.minEntries);

        // Sync children by setting their parent
        pair.node1().syncChildren();
        pair.node2().syncChildren();

        // In case the splitted node was the root
        // Create a new root and set the node's parent to the new root
        if (node.parent == null) {
            this.root = this.makeNewRoot(false);

            node.parent = this.root;
        }

        // Add our pair as children to the splitted node
        node.parent.add(pair.node1());
        node.parent.add(pair.node2());

        // Remove the node from it's parent
        node.parent.remove(node);

        // Since the parent has gained one new child (2 nodes added and one removed)
        // Split the parent if it overflows
        if (node.parent.children.size() > this.maxEntries) {
            this.split(node.parent);
        }
    }

    /**
     * Find the best subtree to add the new entry to
     *
     * @param node      Examined node
     * @param entry     Entry to ddd
     * @return
     */
    private Node chooseSubtree(Node node, Entry entry)
    {
        if (node.leaf) {
            return node;
        }

        // Find min overlap, then area increase and then total area
        if (node.children.get(0).leaf) {
            return Collections.min(node.children, NodeComparator.overlapAreaThenAreaIncreaseThenAreaComparator(entry.mbr, node.children));
        }

        // Find min area increase then total area
        Node pointer = Collections.min(node.children, NodeComparator.areaIncreaseThenAreaComparator(entry.mbr));

        return this.chooseSubtree(pointer, entry);
    }

    private Node makeNewRoot(boolean leaf) {
        return new Node(
            new Rectangle(
                (float) Math.sqrt(Float.MAX_VALUE),
                (float) Math.sqrt(Float.MAX_VALUE),
                (float) Math.sqrt(Float.MIN_VALUE),
                (float) Math.sqrt(Float.MIN_VALUE)
            ),
            leaf
        );
    }
}