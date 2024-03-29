package deque;

import java.util.ArrayList;

public class ArrayDeque<T> implements Deque<T> {

    /**
     * Index for the last element.
     */
    private int head;


    /**
     * Index for insert next.
     */
    private int tail;

    private T[] items;

    public ArrayDeque() {
        head = tail = 50;
        items = (T[]) new Object[100];
    }

    /**
     * Resize to CAPACITY and move to middle.
     *
     * @param capacity
     */
    private void resize(int capacity) {
        T[] x = (T[]) new Object[capacity];
        int size = size();
        int start = (capacity - size) / 2;
        for (int i = 0; i < size; i++) {
            x[start + i] = items[head + i];
        }

        head = start;
        tail = start + size;
        items = x;
    }

    @Override
    public void addFirst(T item) {
        if (head == 0) {
            resize(size() * 2);
        }
        items[head - 1] = item;
        head -= 1;

    }

    @Override
    public void addLast(T item) {
        if (tail == items.length) {
            resize(size() * 2);
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
        for (int i = head; i < tail; i++) {
            System.out.print(items[i] + "  ");
        }
    }

    @Override
    public T removeFirst() {
        if (size() == 0) {
            return null;
        }
        if (size() >= 100 && size() < items.length / 4) {
            resize(size() * 2);
        }
        head += 1;
        return items[head - 1];
    }

    @Override
    public T removeLast() {
        if (size() == 0) {
            return null;
        }
        if (size() >= 100 && size() < items.length / 4) {
            resize(size() * 2);
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
}
