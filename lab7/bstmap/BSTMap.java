package bstmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class BSTMap<Key extends Comparable<Key>, Val> implements Map61B<Key, Val> {

    private Node root;
    int size;
    Set<Key> keySet;

    public BSTMap() {
        keySet = new TreeSet<>();
    }

    private class Node {
        private Key key;
        private Val val;
        private Node left, right;

        public Node(Key key, Val val) {
            this.key = key;
            this.val = val;
        }
    }

    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        root = null;
        size = 0;
        keySet = new TreeSet<>();
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(Key key) {
        return keySet.contains(key);
    }

    /*
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public Val get(Key key) {
        if (!containsKey(key)) {
            return null;
        }
        return get(root, key);
    }

    private Val get(Node node, Key key) {
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return node.val;
        }
    }

    // @Override
    // public boolean containsKey(Object key) {
    // if (key instanceof Key) {
    // return containsKey(key);
    // }
    // return false;
    // }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(Key key, Val value) {
        if (containsKey(key)) {
            return;
        }
        keySet.add(key);
        size += 1;
        root = put(root, key, value);
    }

    private Node put(Node node, Key key, Val value) {
        if (node == null) {
            node = new Node(key, value);
        } else if (key.compareTo(node.key) < 0) {
            node.left = put(node.left, key, value);
        } else {
            node.right = put(node.right, key, value);
        }
        return node;
    }

    /*
     * Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException.
     */
    @Override
    public Set<Key> keySet() {
        return keySet;
    }

    /*
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public Val remove(Key key) {
        throw new UnsupportedOperationException();
    }

    /*
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    @Override
    public Val remove(Key key, Val value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Key> iterator() {
        return new BSTMapIterator();
    }

    private class BSTMapIterator implements Iterator<Key> {
        ArrayList<Key> res;
        int i;

        public void BSTMapIterator() {
            preOrder(root);
            i = 0;
        }

        private void preOrder(Node node) {
            if (node == null) {
                return;
            }
            if (node.left != null) {
                preOrder(node.left);
            }
            res.add(node.key);
            if (node.right != null) {
                preOrder(node.right);
            }
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return i < size;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         */
        @Override
        public Key next() {
            return res.get(i++);
        }
    }
}
