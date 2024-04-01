package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;


    /**
     * Index for the last element.
     */
    private int head;


    /**
     * Index for insert next.
     */
    private int tail;

    private final int MIN_CAPACITY = 4;

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int idx = head;

        @Override
        public boolean hasNext() {
            return idx < tail;
        }

        @Override
        public T next() {
            idx += 1;
            return items[idx - 1];
        }
    }

    public ArrayDeque() {
        head = tail = MIN_CAPACITY / 2;
        items = (T[]) new Object[MIN_CAPACITY];
    }

    /**
     * Resize to CAPACITY and move to middle.
     *
     * @param capacity Modifies size of ITEMS[] to CAPACITY.
     */
    private void resize(int capacity) {
        T[] x = (T[]) new Object[capacity];
        int size = size();
        int start = (capacity - size) / 2;
        System.arraycopy(items, head, x, start, size);

        head = start;
        tail = start + size;
        items = x;
    }

    @Override
    public void addFirst(T item) {
        if (head == 0) {
            resize(Math.max(size() * 2, MIN_CAPACITY));
        }
        items[head - 1] = item;
        head -= 1;

    }

    @Override
    public void addLast(T item) {
        if (tail == items.length) {
            resize(Math.max(size() * 2, MIN_CAPACITY));
        }
        items[tail] = item;
        tail += 1;
    }


    @Override
    public int size() {
        return tail - head;
    }

    @Override
    public void printDeque() {
        for (T item : this) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size() == 0) {
            return null;
        }
        if (size() >= MIN_CAPACITY && size() < items.length / 4) {
            resize(Math.max(size() * 2, MIN_CAPACITY));
        }
        head += 1;
        return items[head - 1];
    }

    @Override
    public T removeLast() {
        if (size() == 0) {
            return null;
        }
        if (size() >= MIN_CAPACITY && size() < items.length / 4) {
            resize(Math.max(size() * 2, MIN_CAPACITY));
        }
        tail -= 1;
        return items[tail];
    }

    @Override
    public T get(int index) {
        if (index > size()) {
            return null;
        }
        return items[head + index];
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof Deque) {
            Deque<T> od = (Deque<T>) o;
            if (size() != od.size()) {
                return false;
            }
            for (int i = 0; i < size(); i++) {
                if (!get(i).equals(od.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
