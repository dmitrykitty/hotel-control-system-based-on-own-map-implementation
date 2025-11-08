package com.dnikitin.map;
import java.util.*;

/**
 * An AVL-Tree-based implementation of the {@link Map} interface.
 * <p>
 * This map provides guaranteed O(log n) time cost for the {@code contains},
 * {@code get}, {@code put}, and {@code remove} operations.
 * <p>
 * The map is sorted according to the natural ordering of its keys (if keys
 * implement {@link Comparable}), or by a {@link Comparator} provided at
 * map creation time.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class MyMap<K, V> implements Map<K, V> {

    // FIELDS

    /**
     * The root node of the AVL tree.
     */
    private Node<K, V> root;

    /**
     * The number of nodes contained in this map.
     */
    private int size;

    /**
     * The comparator used to maintain order in this map, or
     * null if it uses the natural ordering of its keys.
     */
    private final Comparator<? super K> comparator;

    // CONSTRUCTORS

    /**
     * Constructs a new, empty tree map, using the specified comparator.
     *
     * @param comparator the comparator that will be used to order this map.
     */
    public MyMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    /**
     * Constructs a new, empty tree map, ordered according to the
     * natural ordering of its keys.
     * <p>
     * All keys inserted into the map must implement the {@link Comparable}
     * interface.
     */
    public MyMap() {
        this.comparator = (k1, k2) -> ((Comparable<K>) k1).compareTo(k2);
    }

    // PUBLIC METHODS FROM MAP INTERFACE (java doc description provided in Map interface)


    @Override
    public boolean put(K key, V value) {
        if (key == null || value == null) {
            return false;
        }

        root = putHelper(root, key, value);
        return true;
    }


    @Override
    public boolean remove(K key) {
        if (key == null) {
            return false;
        }
        int oldSize = size;
        root = removeHelper(root, key);
        return oldSize > size;
    }


    @Override
    public V get(K key) {
        if (key == null || root == null) {
            return null;
        }

        Node<K, V> node = root;
        while (node != null) {
            int cmp = comparator.compare(key, node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public boolean contains(K key){
        return get(key) != null;
    }

    @Override
    public List<K> keys() {
        List<K> keys = new ArrayList<>();
        for(EntryIterator it = new EntryIterator(); it.hasNext();){
            keys.add(it.next().getKey());
        }
        return keys;
    }

    // PUBLIC UTILITY METHODS

    /**
     * Returns the number of key-value mappings in this map.
     * @return the number of entries in this map
     */
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    // PRIVATE UTILITY METHODS

    /**
     * Recursive helper for {@code put}. Inserts the key-value pair and
     * re-balances the tree on the way back up the call stack.
     *
     * @param node  The current node in the recursion
     * @param key   The key to insert
     * @param value The value to insert/update
     * @return The new root of the (potentially re-balanced) subtree
     */
    private Node<K, V> putHelper(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int cmp = comparator.compare(key, node.key);

        if (cmp < 0) {
            node.left = putHelper(node.left, key, value);
        } else if (cmp > 0) {
            node.right = putHelper(node.right, key, value);
        } else {
            node.value = value;
        }
        return node.balance();
    }

    /**
     * Recursive helper for {@code remove}. Removes the node for the given key
     * and re-balances the tree on the way back up the call stack.
     *
     * @param node The current node in the recursion
     * @param key  The key to remove
     * @return The new root of the (potentially re-balanced) subtree
     */
    private Node<K, V> removeHelper(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = comparator.compare(key, node.key);
        if (cmp < 0) {
            node.left = removeHelper(node.left, key);
        } else if (cmp > 0) {
            node.right = removeHelper(node.right, key);
        } else {
            size--;
            //wyszukany node jest lisciem
            if (node.left == null && node.right == null) {
                node = null;
            }
            //jest jedno dziecko (prawe)
            else if (node.left == null) {
                node = node.right;
            }
            //jest jedno dziecko (lewe)
            else if (node.right == null) {
                node = node.left;
            }
            //dwa dziecka
            else {
                Node<K, V> successor = node.right;
                while (successor.left != null) {
                    successor = successor.left;
                }
                node.key = successor.key;
                node.value = successor.value;

                node.right = removeHelper(node.right, successor.key);
                size++;
            }
        }
        if (node == null) {
            return null;
        }
        return node.balance();
    }

    // INNER CLASSES (NODE(ENTRY) AND ENTRY_ITERATOR)

    /**
     * Represents a single node (entry) within the AVL tree.
     * <p>
     * This static nested class stores the key-value pair, references to the
     * left and right children, and the height of the subtree rooted at this node.
     * The {@code height} is crucial for maintaining the AVL balance property.
     * <p>
     * It is declared as {@code static} to avoid the memory overhead of an
     * implicit reference to the outer map instance (a standard practice also
     * used by {@code java.util.TreeMap}).
     *
     * @param <K> the type of key maintained by this node
     * @param <V> the type of value associated with the key
     */
    private static final class Node<K, V> implements java.util.Map.Entry<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;
        int height;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }

        /**
         * Returns the height of a node, handling nulls safely.
         */
        private static int height(Node<?, ?> node) {
            return node == null ? 0 : node.height;
        }

        /**
         * Recalculates and updates the height of this node based on its children.
         */
        private void updateHeight() {
            int leftHeight = height(left);
            int rightHeight = height(right);
            height = Math.max(leftHeight, rightHeight) + 1;
        }

        /**
         * Calculates the balance factor (left height - right height) for this node.
         */
        private int balanceFactor() {
            return height(left) - height(right);
        }

        /**
         * Performs a left rotation on this node.
         * @return The new root of the subtree.
         */
        private Node<K, V> rotateLeft() {
            Node<K, V> newRoot = right;
            right = newRoot.left;
            newRoot.left = this;
            updateHeight();
            newRoot.updateHeight();
            return newRoot;
        }

        /**
         * Performs a right rotation on this node.
         * @return The new root of the subtree.
         */
        private Node<K, V> rotateRight() {
            Node<K, V> newRoot = left;
            left = newRoot.right;
            newRoot.right = this;
            updateHeight();
            newRoot.updateHeight();
            return newRoot;
        }

        /**
         * Performs a Left-Right (LR) rotation on this node.
         * @return The new root of the subtree.
         */
        private Node<K, V> rotateLeftRight() {
            left = left.rotateLeft();
            return rotateRight();
        }

        /**
         * Performs a Right-Left (RL) rotation on this node.
         * @return The new root of the subtree.
         */
        private Node<K, V> rotateRightLeft() {
            right = right.rotateRight();
            return rotateLeft();
        }

        /**
         * Balances the subtree rooted at this node and returns the new root.
         * This method updates the node's height and performs necessary rotations.
         * @return The new, balanced root of the subtree.
         */
        private Node<K, V> balance() {
            updateHeight();

            if (balanceFactor() > 1) {
                if (left.balanceFactor() < 0) {
                    return rotateLeftRight();
                } else {
                    return rotateRight();
                }
            }
            if (balanceFactor() < -1) {
                if (right.balanceFactor() > 0) {
                    return rotateRightLeft();
                } else {
                    return rotateLeft();
                }
            }
            return this;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    /**
     * An iterator over the map's entries, providing an in-order traversal.
     * This implementation is "lazy" and uses O(h) extra space, where h is the
     * height of the tree.
     */
    private final class EntryIterator implements Iterator<java.util.Map.Entry<K, V>>{

        //Symulacja rekurencji poprzez uzycie stosu
        private final Deque<Node<K, V>> stack = new ArrayDeque<>();

        /**
         * Creates an iterator starting at the smallest key.
         */
        public EntryIterator(){
            pushAllLeftNodes(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public java.util.Map.Entry<K, V> next() {
            if(!hasNext()){
                throw new NoSuchElementException("No more elements in the map");
            }

            Node<K, V> node = stack.pop();

            //wyszukiwanie następnika(najmniejszy klucz w prawym poddrzewie)
            if(node.right != null){
                pushAllLeftNodes(node.right);
            }
            return node;
        }

        /**
         * Pushes a node and all its left children onto the stack.
         */
        private void pushAllLeftNodes(Node<K, V> node){
            while(node != null){
                stack.push(node);
                node = node.left;
            }
        }
    }
}
