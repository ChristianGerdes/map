package bfst20.mapit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.datastructures.Tree.Tree;
import bfst20.mapit.model.Type;
import bfst20.mapit.model.drawables.LinePath;
import bfst20.mapit.model.osm.OSMNode;
import bfst20.mapit.model.osm.OSMWay;

public class TreeTest {
    @Test
    void creating_a_default_tree() {
        LinePath linePath1 = this.createLinePath(0f, 0f, 0f, 10f);
        LinePath linePath2 = this.createLinePath(20f, 0f, 20f, 10f);

        Tree tree = new Tree();
        tree.insert(linePath1);
        tree.insert(linePath2);

        assertEquals(2, tree.size);
        assertTrue(tree.root.leaf);
        assertEquals(2, tree.root.children.size());
    }

    @Test
    void creating_a_tree_using_STRPacking() {
        LinePath linePath1 = this.createLinePath(0f, 0f, 0f, 10f);
        LinePath linePath2 = this.createLinePath(20f, 0f, 20f, 10f);
        ArrayList<Drawable> entries = new ArrayList<>();
        entries.add(linePath1);
        entries.add(linePath2);

        Tree tree = new Tree(entries);

        assertEquals(2, tree.size);
        assertTrue(tree.root.leaf);
        assertEquals(2, tree.root.children.size());
    }

    @Test
    void clearing_a_tree() {
        LinePath linePath1 = this.createLinePath(0f, 0f, 0f, 10f);
        LinePath linePath2 = this.createLinePath(20f, 0f, 20f, 10f);
        ArrayList<Drawable> entries = new ArrayList<>();
        entries.add(linePath1);
        entries.add(linePath2);

        Tree tree = new Tree(entries);
        tree.clear();

        assertEquals(0, tree.size);
        assertTrue(tree.root.leaf);
        assertEquals(0, tree.root.children.size());
    }

    @Test
    void performing_nearest_neighbour_on_two_drawables_1() {
    // Tree tree = new Tree(3);

    // LinePath linePath1 = this.createLinePath(0f, 0f, 0f, 10f);
    // LinePath linePath2 = this.createLinePath(20f, 0f, 20f, 10f);

    // tree.insert(linePath1);
    // tree.insert(linePath2);

    // tree = tree.pack();

    // Drawable nearest = tree.nearest(new Point(5f, 5f));

    // assertTrue(nearest == linePath1);
    }

    // @Test
    // void performing_nearest_neighbour_on_two_drawables_2() {
    //     Tree tree = new Tree(3);

    //     LinePath linePath1 = this.createLinePath(0f, 0f, 0f, 10f);
    //     LinePath linePath2 = this.createLinePath(20f, 0f, 20f, 10f);

    //     tree.insert(linePath1);
    //     tree.insert(linePath2);

    //     Drawable nearest = tree.nearest(new Point(15f, 5f));

    //     assertTrue(nearest == linePath2);
    // }

    // @Test
    // void performing_nearest_neighbour_on_deep_tree() {
    //     Tree tree = new Tree(3);

    //     LinePath linePath1 = this.createLinePath(0f, 0f, 0f, 10f);
    //     LinePath linePath2 = this.createLinePath(0f, 10f, 0f, 20f);
    //     LinePath linePath3 = this.createLinePath(0f, 20f, 0f, 30f);
    //     LinePath linePath4 = this.createLinePath(0f, 30f, 0f, 40f);
    //     LinePath linePath5 = this.createLinePath(0f, 40f, 0f, 50f);
    //     LinePath linePath6 = this.createLinePath(0f, 50f, 0f, 60f);
    //     LinePath linePath7 = this.createLinePath(0f, 61f, 0f, 70f);
    //     LinePath linePath8 = this.createLinePath(20f, 0f, 20f, 10f);
    //     LinePath linePath9 = this.createLinePath(20f, 10f, 20f, 20f);
    //     LinePath linePath10 = this.createLinePath(20f, 20f, 20f, 30f);
    //     LinePath linePath11 = this.createLinePath(20f, 30f, 20f, 40f);
    //     LinePath linePath12 = this.createLinePath(20f, 40f, 20f, 50f);
    //     LinePath linePath13 = this.createLinePath(20f, 50f, 20f, 60f);
    //     LinePath linePath14 = this.createLinePath(20f, 60f, 20f, 70f);

    //     tree.insert(new Drawable[] { linePath1, linePath2, linePath3, linePath4,
    //     linePath5, linePath6, linePath7, linePath8,
    //     linePath9, linePath10, linePath11, linePath12, linePath13, linePath14 });

    //     tree = tree.pack();

    //     Drawable nearest = tree.nearest(new Point(5f, 55f));

    //     System.out.println(nearest.getCoords()[1]);

    //     assertTrue(nearest == linePath6);
    // }

    private LinePath createLinePath(float x1, float y1, float x2, float y2) {
        OSMNode osmNode1 = new OSMNode(1, x1, y1);
        OSMNode osmNode2 = new OSMNode(1, x2, y2);

        OSMWay osmWay1 = new OSMWay();
        osmWay1.add(osmNode1);
        osmWay1.add(osmNode2);

        return new LinePath(osmWay1, Type.NORMALROADS);
    }
}
