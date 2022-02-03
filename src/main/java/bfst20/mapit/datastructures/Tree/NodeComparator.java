package bfst20.mapit.datastructures.Tree;

import java.util.List;
import java.io.Serializable;
import java.util.Comparator;
import bfst20.mapit.datastructures.Point;

public class NodeComparator implements Comparator<Node>, Serializable {
    private static final long serialVersionUID = -788508415337932294L;

    Point point;

    NodeComparator() {}

    NodeComparator(Point point) {
        this.point = point;
    }

    public int compare(Node node1, Node node2) {
        return Double.compare(node1.distance(point), node2.distance(point));
    }

    public static Comparator<Node> overlapAreaThenAreaIncreaseThenAreaComparator(Rectangle r, List<Node> list) {
        return new Comparator<Node>() {
            public int compare(Node leaf1, Node leaf2) {
                int value = Double.compare(overlapArea(r, list, leaf1), overlapArea(r, list, leaf2));
                if (value == 0) {
                    value = Double.compare(areaIncrease(r, leaf1), areaIncrease(r, leaf2));
                    if (value == 0) {
                        value = Double.compare(area(r, leaf1), area(r, leaf2));
                    }
                }
                return value;
            }
        };
    }

    private static float overlapArea(Rectangle r, List<Node> list, Node g) {
        Rectangle gPlusR = g.mbr.add(r);

        float m = 0;
        for (Node other : list) {
            if (other != g) {
                m += gPlusR.intersectionArea(other.mbr);
            }
        }
        return m;
    }

    private static double areaIncrease(Rectangle r, Node g) {
        Rectangle gPlusR = g.mbr.add(r);

        return gPlusR.area() - g.mbr.area();
    }

    private static double area(Rectangle r, Node g1) {
        return g1.mbr.add(r).area();
    }

    public static Comparator<Node> areaIncreaseThenAreaComparator(Rectangle r) {
        return new Comparator<Node>() {
            public int compare(Node g1, Node g2) {
                int value = Double.compare(areaIncrease(r, g1), areaIncrease(r, g2));

                if (value == 0) {
                    value = Double.compare(area(r, g1), area(r, g2));
                }

                return value;
            }
        };
    }
}