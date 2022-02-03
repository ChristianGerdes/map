package bfst20.mapit.datastructures;

import java.io.Serializable;
import java.util.Iterator;

public class Bag<Item> implements Iterable<Item>, Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 3459744394790942156L;
  private Node<Item> first; // beginning of bag
  private int n; // number of elements in bag

  // helper linked list class
  private static class Node<Item> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6173062689774110304L;
    private Item item;
    private Node<Item> next;
  }

  /**
   * Initializes an empty bag.
   */
  public Bag() {
    first = null;
    n = 0;
  }

  /**
   * Returns true if this bag is empty.
   *
   * @return {@code true} if this bag is empty; {@code false} otherwise
   */
  public boolean isEmpty() {
    return first == null;
  }

  /**
   * Returns the number of items in this bag.
   *
   * @return the number of items in this bag
   */
  public int size() {
    return n;
  }

  /**
   * Adds the item to this bag.
   *
   * @param item the item to add to this bag
   */
  public void add(Item item) {
    Node<Item> oldfirst = first;
    first = new Node<Item>();
    first.item = item;
    first.next = oldfirst;
    n++;
  }

  /**
   * Returns an iterator that iterates over the items in this bag in arbitrary
   * order.
   *
   * @return an iterator that iterates over the items in this bag in arbitrary
   *         order
   */
  public Iterator<Item> iterator() {
    return new LinkedIterator(first);
  }

  // an iterator, doesn't implement remove() since it's optional
  private class LinkedIterator implements Iterator<Item> {
    private Node<Item> current;

    public LinkedIterator(Node<Item> first) {
      current = first;
    }

    public boolean hasNext() {
      return current != null;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public Item next() {
      if (!hasNext())
        throw new RuntimeException();
      Item item = current.item;
      current = current.next;
      return item;
    }
  }
}