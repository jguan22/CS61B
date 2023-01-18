package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {
        private final K key;
        private V value;
        private Node left;
        private Node right;
        private int size;

        public Node(K k, V v, int s) {
            key = k;
            value = v;
            size = s;
        }
    }
    private Node root;

    @Override
    public void clear() {
        root = null;
        root.size = 0;
    }

    // search the key from the root
    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Need to include the key");
        }
        return get(key) != null;
    }

    // compare the key to the key of the node


    @Override
    public V get(K key) {
        return get(key, root);
    }

    // compare the key to the key of the node
    private V get(K key, Node node) {
        if (key == null) {
            throw new IllegalArgumentException("Need to include the key");
        }
        // if the node is null, return null
        if (node == null) {
            return null;
        }

        // keep searching if node is not null
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            return get(key, node.right);
        } else if (cmp < 0) {
            return get(key, node.left);
        }
        return node.value;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(Node node) {
        return node.size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Need to include the key");
        }
        //
        if (value == null) {
            return;
        }

        root = put(key, value, root);
    }

    private Node put(K key, V value, Node node) {
        // if node does not exist, create a new node //
        if (node == null) {
            return new Node(key, value, 1);
        }

        // find the node and put value in
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            return put(key, value, node.right);
        } else if (cmp < 0) {
            return put(key, value, node.left);
        } else {
            node.value = value;
        }
        node.size = size(node.left) +size(node.right) + 1;
        return node;
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> keySet = new HashSet<>();
        addKey(root, keySet);
        return keySet;
    }

    private void addKey(Node node, Set<K> keySet) {
        if (node == null) {
            return;
        }

        keySet.add(node.key);
        addKey(node.left, keySet);
        addKey(node.right, keySet);
    }
    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Need to include the key");
        }

        V value = get(key);
        root = remove(key, root);
        return value;
    }

    private Node remove(K key, Node node) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            return remove(key, node.right);
        } else if (cmp < 0) {
            return remove(key, node.left);
        } else {
            // if target node only has one child or no child
            // hook the child to the parent node
            if (node.right == null) {
                return node.left;
            }
            if (node.left == null) {
                return node.right;
            }
            // if target node has more than one descendant
            // find the min descendant on the right and link it to the parent node
            Node temp = node;
            node = min(temp.right);
            node.right = remove(node.key, temp.right);
            node.left = temp.left;
        }
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    // find the min descendant and return the key
    public K min() {
        return min(root).key;
    }

    private Node min(Node node) {
        if (node.left == null) {
            return node;
        }
        return min(node.left);
    }

    @Override
    public V remove(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Need to include the key");
        }

        if (value.equals(get(key))) {
            root = remove(key, root);
            return value;
        }
        return null;
    }

    public void printInOrder() {
        print(root);
    }

    private void print(Node node) {
        // recursive condition
        if (node == null) {
            return;
        }

        // starting from the left
        print(node.left);
        // print itself once there is no left
        System.out.print(node.value.toString() + " ");
        // search its right
        print(node.right);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
