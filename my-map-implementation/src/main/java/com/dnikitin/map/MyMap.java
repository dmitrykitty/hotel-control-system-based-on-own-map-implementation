package com.dnikitin.map;

import java.util.*;

public class MyMap<K, V> implements Map<K, V> {

    public MyMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    public MyMap() {
        this.comparator = (k1, k2) -> ((Comparable<K>) k1).compareTo(k2);
    }

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


    /**
     * Represents a single node (entry) within the AVL tree.
     * <p>
     * his static nested class stores the key-value pair, references to the
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

        private static int height(Node<?, ?> node) {
            return node == null ? 0 : node.height;
        }

        private void updateHeight() {
            int leftHeight = height(left);
            int rightHeight = height(right);
            height = Math.max(leftHeight, rightHeight) + 1;
        }

        private int balanceFactor() {
            return height(left) - height(right);
        }

        private Node<K, V> rotateLeft() {
            Node<K, V> newRoot = right;
            right = newRoot.left;
            newRoot.left = this;
            updateHeight();
            newRoot.updateHeight();
            return newRoot;

        }

        private Node<K, V> rotateRight() {
            Node<K, V> newRoot = left;
            left = newRoot.right;
            newRoot.right = this;
            updateHeight();
            newRoot.updateHeight();
            return newRoot;
        }

        private Node<K, V> rotateLeftRight() {
            left = left.rotateLeft();
            return rotateRight();
        }

        private Node<K, V> rotateRightLeft() {
            right = right.rotateRight();
            return rotateLeft();
        }


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

    private final class EntryIterator implements Iterator<java.util.Map.Entry<K, V>>{

        private final Stack<Node<K, V>> stack = new Stack<>();

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
                throw new NoSuchElementException("No more elements");
            }

            Node<K, V> node = stack.pop();
            if(node.right != null){
                pushAllLeftNodes(node.right);
            }
            return node;
        }

        private void pushAllLeftNodes(Node<K, V> node){
            while(node != null){
                stack.push(node);
                node = node.left;
            }
        }
    }


    private Node<K, V> root;
    private int size;
    private final Comparator<? super K> comparator;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

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


    private int compare(K key1, K key2) {
        return ((Comparable<K>) key1).compareTo(key2);
    }
}
