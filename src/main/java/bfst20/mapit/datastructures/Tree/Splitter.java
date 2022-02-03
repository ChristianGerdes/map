package bfst20.mapit.datastructures.Tree;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class Splitter {
    private final Comparator<Pair> comparator;

    public Splitter() {
        this.comparator = new Comparator<Pair>() {
            public int compare(Pair p1, Pair p2) {
                // check overlap first then areaSum
                int value = Double.compare(overlap(p1), overlap(p2));
                if (value == 0) {
                    return Double.compare(p1.areaSum(), p2.areaSum());
                } else {
                    return value;
                }
            }
        };
    }

    public Pair split(Node node, List<Node> items, int minEntries) {
        List<Pair> pairs = null;
        float lowestMarginSum = Float.MAX_VALUE;
        List<Node> list = null;

        for (SortType sortType : SortType.values()) {
            if (list == null) {
                list = new ArrayList<Node>(items);
            }

            Collections.sort(list, comparator(sortType));

            List<Pair> p = getPairs(minEntries, node, list);

            float marginSum = marginValueSum(p);

            if (marginSum < lowestMarginSum) {
                lowestMarginSum = marginSum;
                pairs = p;
                list = null;
            }
        }

        return Collections.min(pairs, comparator);
    }

    private static float marginValueSum(List<Pair> list) {
        float sum = 0;
        for (Pair p : list) {
            sum += p.marginSum();
        }

        return sum;
    }

    public List<Pair> getPairs(int minSize, Node node, List<Node> list) {
        List<Pair> pairs = new ArrayList<Pair>(list.size() - 2 * minSize + 1);
        for (int i = minSize; i < list.size() - minSize + 1; i++) {
            List<Node> list1 = list.subList(0, i);
            List<Node> list2 = list.subList(i, list.size());
            Pair pair = new Pair(list1, list2, node.leaf);
            pairs.add(pair);
        }

        return pairs;
    }

    private static Comparator<Node> comparator(SortType sortType) {
        switch (sortType) {
            case X_LOWER:
                return INCREASING_X_LOWER;
            case X_UPPER:
                return INCREASING_X_UPPER;
            case Y_LOWER:
                return INCREASING_Y_LOWER;
            case Y_UPPER:
                return INCREASING_Y_UPPER;
            default:
                throw new IllegalArgumentException("Unknown SortType " + sortType);
        }
    }

    private static final Comparator<Node> INCREASING_X_LOWER = new Comparator<Node>() {
        public int compare(Node n1, Node n2) {
            return Double.compare(n1.mbr.x1(), n2.mbr.x1());
        }
    };

    private static final Comparator<Node> INCREASING_X_UPPER = new Comparator<Node>() {
        public int compare(Node n1, Node n2) {
            return Double.compare(n1.mbr.x2(), n2.mbr.x2());
        }
    };

    private static final Comparator<Node> INCREASING_Y_LOWER = new Comparator<Node>() {
        public int compare(Node n1, Node n2) {
            return Double.compare(n1.mbr.y1(), n2.mbr.y1());
        }
    };

    private static final Comparator<Node> INCREASING_Y_UPPER = new Comparator<Node>() {
        public int compare(Node n1, Node n2) {
            return Double.compare(n1.mbr.y2(), n2.mbr.y2());
        }
    };

    private static double overlap(Pair pair) {
        return pair.node1().mbr.intersectionArea(pair.node2().mbr);
    }

    enum SortType {
        X_LOWER, X_UPPER, Y_LOWER, Y_UPPER;
    }
}