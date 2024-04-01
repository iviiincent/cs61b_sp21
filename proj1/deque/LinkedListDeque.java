package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private static class LinkedNode<T> {
        T item;
        LinkedNode<T> pre;
        LinkedNode<T> nxt;

        LinkedNode(T item) {
            this.item = item;
        }

        LinkedNode(T item, LinkedNode<T> pre, LinkedNode<T> nxt) {
            this.item = item;
            this.pre = pre;
            this.nxt = nxt;
        }

    }

    private class LinkedListDequeIterator implements Iterator<T> {

        LinkedNode<T> node = head.nxt;

        @Override
        public boolean hasNext() {
            return node != tail;
        }

        @Override
        public T next() {
            LinkedNode<T> cur = node;
            node = node.nxt;
            return cur.item;
        }
    }

    private int size = 0;

    private final LinkedNode<T> head;
    private final LinkedNode<T> tail;

    public LinkedListDeque() {
        head = new LinkedNode<>(null);
        tail = new LinkedNode<>(null);
        head.nxt = tail;
        tail.pre = head;
    }

    @Override
    public void addFirst(T item) {
        size += 1;
        LinkedNode<T> node = new LinkedNode<>(item, head, head.nxt);
        node.nxt.pre = node;
        head.nxt = node;
    }

    @Override
    public void addLast(T item) {
        size += 1;
        LinkedNode<T> node = new LinkedNode<>(item, tail.pre, tail);
        node.pre.nxt = node;
        tail.pre = node;
    }

    @Override
    public int size() {
        return size;
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
        if (size == 0) {
            return null;
        }
        size -= 1;
        LinkedNode<T> node = head.nxt;
        head.nxt = node.nxt;
        head.nxt.pre = head;
        return node.item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        LinkedNode<T> node = tail.pre;
        tail.pre = node.pre;
        tail.pre.nxt = tail;
        return node.item;
    }

    @Override
    public T get(int index) {
        if (index > size) {
            return null;
        } else if (index < size / 2) {
            // Search from head.
            LinkedNode<T> node = head.nxt;
            for (int i = 0; i < index; i++) {
                node = node.nxt;
            }
            return node.item;
        } else {
            // Search from tail.
            LinkedNode<T> node = tail.pre;
            index = size - index - 1;
            for (int i = 0; i < index; i++) {
                node = node.pre;
            }
            return node.item;
        }
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
            if (size != od.size()) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!get(i).equals(od.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public T getRecursive(int index) {
        return getRecursive(index, head.nxt);
    }

    private T getRecursive(int index, LinkedNode<T> node) {
        if (node == tail) {
            return null;
        }
        if (index == 0) {
            return node.item;
        }
        return getRecursive(index - 1, node.nxt);
    }
}
