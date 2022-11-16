package deque;

public class LinkedListDeque<T> {
    private class Node {
        private Node prev;
        private T item;
        private Node next;

        private Node(Node p, T i, Node n){
            prev = p;
            item = i;
            next = n;
        }
    }

    private int size;
    private Node sentinel;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        sentinel.next = new Node(sentinel, item, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size++;
    }

    public void addLast(T item) {
        sentinel.prev = new Node(sentinel.prev, item, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size++;
    }

    public boolean isEmpty() {
        if (sentinel.next == null) {
            return true;
        }
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque() {
        Node printNode = sentinel;
        while (printNode.next != null) {
            printNode = printNode.next;
            System.out.print(printNode.item + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (sentinel.next == null) {
            return null;
        }

        T firstItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return firstItem;
    }

    public T removeLast() {
        if (sentinel.prev == null) {
            return null;
        }

        T lastItem = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return lastItem;
    }

    public T get(int index) {
        Node indexNode = sentinel;
        int i = 0;
        while (indexNode.next != null) {
            indexNode = indexNode.next;
            if (i == index) {
                return indexNode.item;
            }
            i++;
        }
        return null;
    }

    public T getRecursive(int index) {
        /** Check if the index is valid */
        if (index > size - 1 || index < 0) {
            return null;
        }

        /** Call recursive get function */
        int i = 0;
        Node indexNode = sentinel.next;
        T item = getRecursiveHelper(index, i, indexNode);
        return item;
    }

    /** Recursive function to find indexNode */
    public T getRecursiveHelper(int index, int i, Node node) {
        if (i == index) {
            return node.item;
        }

        i++;
        node = node.next;
        return getRecursiveHelper(index, i, node);
    }
}
