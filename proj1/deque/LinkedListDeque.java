package deque;

public class LinkedListDeque<T> implements Deque<T> {

    private static class LinkedNode<T> {
        T item;
        LinkedNode<T> pre;
        LinkedNode<T> nxt;

        public LinkedNode(T item) {
            this.item = item;
        }

        public LinkedNode(T item, LinkedNode<T> pre, LinkedNode<T> nxt) {
            this.item = item;
            this.pre = pre;
            this.nxt = nxt;
        }

    }

    private int size;

    private final LinkedNode<T> head;
    private final LinkedNode<T> tail;

    public LinkedListDeque() {
        size = 0;
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
        for (LinkedNode<T> node = head.nxt; node != tail; node = node.nxt) {
            System.out.println(node.item);
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size -= 1;
        LinkedNode<T> node = head.nxt;
        head.nxt = node.nxt;
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
            for (int i = 0; i < index; i++) {
                node = node.pre;
            }
            return node.item;
        }
    }
}
