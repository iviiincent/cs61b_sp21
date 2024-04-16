package bstmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private Node root;
    private int size;
    private Set<K> keySet = new TreeSet<>();

    public BSTMap() {
    }

    private class Node {
        private K key;
        private V val;
        private Node left, right;

        public Node(K key, V val) {
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
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    /*
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        }
        return get(root, key);
    }

    private V get(Node node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return node.val;
        }
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (containsKey(key)) {
            return;
        }
        keySet.add(key);
        size += 1;
        root = put(root, key, value);
    }

    private Node put(Node node, K key, V value) {
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
     */
    @Override
    public Set<K> keySet() {
        return keySet;
    }

    /*
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7.
     */
    @Override
    public V remove(K key) {
        V getVal = get(key);
        if (getVal != null) {
            size -= 1;
            keySet.remove(key);
            root = remove(root, key);
        }
        return getVal;
    }

    /*
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value.
     */
    @Override
    public V remove(K key, V value) {
        V getVal = get(key);
        if (getVal != value || getVal == null) {
            return null;
        }
        size -= 1;
        keySet.remove(key);
        remove(root, key);
        return value;
    }

    private Node remove(Node node, K key) {
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
            return node;
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
            return node;
        }

        // Deletes node NODE
        if (node.left == null && node.right == null) {
            return null;
        } else if (node.left == null) {
            return node.right;
        } else if (node.right == null) {
            return node.left;
        } else {
            Node nextNode = node.right;
            while (nextNode.left != null) {
                nextNode = nextNode.left;
            }
            node.key = nextNode.key;
            node.val = nextNode.val;
            node.right = remove(node.right, nextNode.key);
            return node;
        }
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }

    public void printInOrder() {
        for (K key : this) {
            System.out.print(key + " ");
        }
        System.out.println();
    }

    private class BSTMapIterator implements Iterator<K> {
        ArrayList<K> res = new ArrayList<>(size);
        int i;

        public BSTMapIterator() {
            preOrder(root);
            i = 0;
        }

        private void preOrder(Node node) {
            if (node == null) {
                return;
            }
            preOrder(node.left);
            res.add(node.key);
            preOrder(node.right);
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
            return i < res.size();
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         */
        @Override
        public K next() {
            return res.get(i++);
        }
    }
}
