package bfst20.mapit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bfst20.mapit.datastructures.SortedArrayList;
import bfst20.mapit.model.osm.OSMNode;

public class SortedArrayListTest {
  public static SortedArrayList<OSMNode> list;
  public static OSMNode node0;
  public static OSMNode node1;
  public static OSMNode node2;
  public static OSMNode node3;
  public static OSMNode node4;

  @BeforeAll
  public static void init() {
    list = new SortedArrayList<>();
    node0 = new OSMNode(0, 1, 1);
    node1 = new OSMNode(1, 2, 1);
    node2 = new OSMNode(2, 3, 1);
    node3 = new OSMNode(3, 4, 1);
    node4 = new OSMNode(5, 5, 1);
    list.add(node0);
    list.add(node1);
    list.add(node2);
    list.add(node3);
    list.add(node4);
  }

  @DisplayName("BinarySearch test")
  @Test
  void binarySearchTest() {
    var expected0 = list.get(0);
    var expected1 = list.get(1);
    var expected2 = list.get(2);
    var expected3 = list.get(3);
    // add node to sort the list again
    list.add(new OSMNode(4, 0, 0));
    var expected4 = list.get(5);
    assertEquals(node0, expected0);
    assertEquals(node1, expected1);
    assertEquals(node2, expected2);
    assertEquals(node3, expected3);
    assertEquals(node4, expected4);
  }
}