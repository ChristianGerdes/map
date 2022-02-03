package bfst20.mapit.datastructures.Tree;

import java.util.List;
import java.io.Serializable;
import java.util.LinkedList;
import bfst20.mapit.datastructures.Point;

public class Node implements Serializable {
    private static final long serialVersionUID = 8708856743479022765L;
    public Node parent;
    public boolean leaf;
    public Rectangle mbr;
    public List<Node> children = new LinkedList<Node>();

    public Node(Rectangle mbr, boolean leaf) {
        this.mbr = mbr;
        this.leaf = leaf;
    }

    public Node(List<Node> children, boolean leaf) {
        this.add(children);

        this.leaf = leaf;
    }

    public void add(Node node) {
        this.children.add(node);
        node.parent = this;

        this.recalculateMBR();
    }

    public void add(List<Node> nodes) {
        for (Node node : nodes) {
            this.children.add(node);
            node.parent = this;
        }

        this.recalculateMBR();
    }

    public void remove(Node node) {
        this.children.remove(node);

        this.recalculateMBR();
    }

    public void recalculateMBR() {
        if (this.children.size() == 0) {
            return;
        }

        this.mbr = new Rectangle(this.children);

        if (this.parent != null) {
            this.parent.recalculateMBR();
        }
    }

    public void syncChildren() {
        for (Node child : this.children) {
            child.setParent(this);
        }
    }

    public void setParent(Node node) {
        this.parent = node;
    }

    public double distance(Point point) {
        return this.minDist(
            new double[] { point.getX(), point.getY(), },
            new double[] {
                this.mbr.x1(), // 0 - x (top-left)
                this.mbr.y1(), // 1 - y (top-left)
                this.mbr.x2(), // 2 - x (top-right)
                this.mbr.y1(), // 3 - y (top-right)
                this.mbr.x1(), // 4 - x (bottom-left)
                this.mbr.y2(), // 5 - y (bottom-left)
                this.mbr.x2(), // 6 - x (bottom-right)
                this.mbr.y2(), // 7 - y (bottom-right)
            }
        );
    }

    private double minDist(double[] point, double[] coords) {
        // Left side between y
        if (point[0] < coords[0] && point[1] > coords[1] && point[1] < coords[5]) {
            return coords[0] - point[0];
        }

        // Right side between y
        if (point[0] > coords[2] && point[1] > coords[1] && point[1] < coords[5]) {
            return point[0] - coords[2];
        }

        // Top side between x
        if (point[1] < coords[1] && point[0] > coords[0] && point[0] < coords[2]) {
            return Math.abs(point[1]) - Math.abs(coords[1]);
        }

        // Bot side between x
        if (point[1] > coords[5] && point[0] > coords[0] && point[0] < coords[2]) {
            return Math.abs(coords[5]) - Math.abs(point[1]);
        }

        // Corner top left
        if (point[0] < coords[0] && point[1] < coords[1]) {
            double a = coords[0] - point[0];
            double b = Math.abs(point[1]) - Math.abs(coords[1]);

            return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
        }

        // Corner top right
        if (point[0] > coords[2] && point[1] < coords[1]) {
            double a = point[0] - coords[2];
            double b = Math.abs(point[1]) - Math.abs(coords[3]);

            return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
        }

        // Corner bottom left
        if (point[0] < coords[0] && point[1] > coords[5]) {
            double a = coords[0] - point[0];
            double b = Math.abs(coords[5]) - Math.abs(point[1]);

            return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
        }

        // Corner bottom right
        if (point[0] > coords[2] && point[1] > coords[5]) {
            double a = point[0] - coords[6];
            double b = Math.abs(coords[7]) - Math.abs(point[1]);

            return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
        }

        return 0;
    }
}