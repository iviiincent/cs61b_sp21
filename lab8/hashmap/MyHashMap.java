package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon
 * remove().
 *
 * @author Vincent Ma
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final double DEFAULT_MAX_LOADER = 0.75;

    private final int initialSize;
    private int size = 0;
    private final double maxLoad;
    private Collection<Node>[] buckets;

    /**
     * Constructors
     */
    public MyHashMap() {
        this.initialSize = DEFAULT_INITIAL_CAPACITY;
        this.maxLoad = DEFAULT_MAX_LOADER;
        buckets = createTable(DEFAULT_INITIAL_CAPACITY);

    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        this.maxLoad = DEFAULT_MAX_LOADER;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.maxLoad = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     * <p>
     * Default data struct: LinkedList.
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < table.length; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        buckets = createTable(initialSize);
        size = 0;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(K key) {
        return getNode(key) != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        Node node = getNode(key);
        return node == null ? null : node.value;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return size;
    }

    private Node getNode(K k) {
        int index = getIndex(k);
        for (Node node : buckets[index]) {
            if (k.equals(node.key)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value) {
        Node node = getNode(key);
        if (node == null) {
            // Adds new node to table.
            if (willBeOverLoaded()) {
                resize(buckets.length * 2);
            }
            buckets[getIndex(key)].add(createNode(key, value));
            size += 1;
        } else {
            // Changed its value.
            node.value = value;
        }
    }

    /**
     * Returns a Set view of the keys contained in this map.
     */
    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (K k : this) {
            set.add(k);
        }
        return set;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     */
    @Override
    public V remove(K key) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }

        V val = node.value;
        buckets[getIndex(key)].remove(node);
        size -= 1;
        return val;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     */
    @Override
    public V remove(K key, V value) {
        Node node = getNode(key);
        if (node == null || value != node.value) {
            return null;
        }

        buckets[getIndex(key)].remove(node);
        size -= 1;
        return value;
    }

    /**
     * Resize current table from size to toSize.
     *
     * @param toSize Table's size after resizing.
     */
    private void resize(int toSize) {
        Collection<Node>[] table = createTable(toSize);
        NodeIterator nodeIterator = new NodeIterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            int bucketIndex = getIndex(node.key, table.length);
            table[bucketIndex].add(node);
        }
        buckets = table;
    }

    /**
     * Checks if the table is overloaded if adding node.
     */
    private boolean willBeOverLoaded() {
        return (double) (size + 1) / buckets.length > maxLoad;
    }

    private int getIndex(K k) {
        return getIndex(k, buckets.length);
    }

    private int getIndex(K k, int len) {
        return Math.floorMod(k.hashCode(), len);
    }

    @Override
    public Iterator<K> iterator() {
        return new KIterator();
    }

    private class NodeIterator implements Iterator<Node> {

        private final Iterator<Collection<Node>> tableIterator = Arrays.stream(buckets).iterator();
        private Iterator<Node> bucketIterator = tableIterator.next().iterator();
        private int cnt = 0;

        @Override
        public boolean hasNext() {
            return cnt < size;
        }

        @Override
        public Node next() {
            cnt += 1;
            if (bucketIterator.hasNext()) {
                return bucketIterator.next();
            }
            while (true) {
                Collection<Node> curBucket = tableIterator.next();
                if (!curBucket.isEmpty()) {
                    bucketIterator = curBucket.iterator();
                    break;
                }
            }

            return bucketIterator.next();
        }
    }

    private class KIterator implements Iterator<K> {
        private final NodeIterator nodeIterator = new NodeIterator();

        @Override
        public boolean hasNext() {
            return nodeIterator.hasNext();
        }

        @Override
        public K next() {
            return nodeIterator.next().key;
        }
    }
}
