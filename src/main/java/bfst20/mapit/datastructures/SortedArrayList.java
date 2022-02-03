package bfst20.mapit.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.LongSupplier;

import bfst20.mapit.model.osm.OSMNode;

public class SortedArrayList<T extends LongSupplier> implements Iterable<T>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5302426201675918084L;
    private ArrayList<T> list;
    private boolean isSorted;

    public SortedArrayList(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
        isSorted = false;
    }
    public SortedArrayList() {
        list = new ArrayList<>();
        isSorted = false;
    }

    public void add(T t) {
        list.add(t);
    }

    public void addAll(List<T> otherList) {
        for (T t : otherList) {
            if (get(((OSMNode) t).id) == null) {
                list.add(t);
            }
        }
    }

    public int size() {
        return list.size();
    }

    public T get(long id) {
        if (!isSorted) {
            list.sort(Comparator.comparing(T::getAsLong));
            isSorted = true;
        }
        return binarySearch(id);
    }

    public T binarySearch(long id) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            T midElement = list.get(mid);
            long midId = midElement.getAsLong();

            if (midId < id) {
                low = mid + 1;
            } else if (midId > id) {
                high = mid - 1;
            } else {
                return midElement;
            }
        }

        return null;
    }

    public Iterator<T> iterator() {
        return new LinkedIterator(list);
    }

    private class LinkedIterator implements Iterator<T> {
        private int index = 0;
        private List<T> list;

        public LinkedIterator(List<T> list) {
            this.list = list;
        }

        public boolean hasNext() {
            return index < list.size() - 1;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public T next() {
            if (!hasNext()) {
                throw new RuntimeException();
            }
            return list.get(index++);
        }
    }

}
