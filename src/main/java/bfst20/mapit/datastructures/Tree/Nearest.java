package bfst20.mapit.datastructures.Tree;

import java.io.Serializable;

public class Nearest implements Serializable {
    private static final long serialVersionUID = -3198018482770205881L;

    public final Node node;

    public final double distance;

    Nearest(Node node, double distance) {
        this.node = node;
        this.distance = distance;
    }
}