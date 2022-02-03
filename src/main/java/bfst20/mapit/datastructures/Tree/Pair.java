package bfst20.mapit.datastructures.Tree;

import java.util.List;

public class Pair {
    private final Node node1;
    private final Node node2;

    private double areaSum = -1;
    private final double marginSum;

    public Pair(List<Node> list1, List<Node> list2, boolean leaf) {
        this.node1 = new Node(list1, leaf);

        for (Node child : node1.children) {
            child.setParent(node1);
        }

        this.node2 = new Node(list2, leaf);
        for (Node child : node2.children) {
            child.setParent(node2);
        }

        this.marginSum = this.node1.mbr.perimeter() + this.node2.mbr.perimeter();
    }

    public Node node1() {
        return node1;
    }

    public Node node2() {
        return node2;
    }

    public double areaSum() {
        if (areaSum == -1) {
            areaSum = this.node1.mbr.area() + this.node2.mbr.area();
        }

        return areaSum;
    }

    public double marginSum() {
        return marginSum;
    }
}