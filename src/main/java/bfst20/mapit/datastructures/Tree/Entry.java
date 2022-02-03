package bfst20.mapit.datastructures.Tree;

import bfst20.mapit.Drawable;

public class Entry extends Node {
    private static final long serialVersionUID = 5179828279288512402L;

    public final Drawable value;

    public Entry(Rectangle mbr, Drawable value) {
        super(mbr, true);

        this.value = value;
    }
}