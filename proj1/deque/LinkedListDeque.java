package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private Node prev;
        private T item;
        private Node next;

        private Node(Node p, T i, Node n) {
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

    public int size() {
        return size;
    }

    public void printDeque() {
        Node printNode = sentinel;
        while (printNode.next.item != null) {
            printNode = printNode.next;
            System.out.print(printNode.item + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (sentinel.next.item == null) {
            return null;
        }

        T firstItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return firstItem;
    }

    public T removeLast() {
        if (sentinel.prev.item == null) {
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
        /* Check if the index is valid */
        if (index > size - 1 || index < 0) {
            return null;
        }

        /* Call recursive get function */
        int i = 0;
        Node indexNode = sentinel.next;
        T item = getRecursiveHelper(index, i, indexNode);
        return item;
    }

    /** Recursive function to find indexNode */
    private T getRecursiveHelper(int index, int i, Node node) {
        if (i == index) {
            return node.item;
        }

        i++;
        node = node.next;
        return getRecursiveHelper(index, i, node);
    }


    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private void printDequeIterator() {
        Iterator<T> seer = iterator();

        while (seer.hasNext()) {
            System.out.println(seer.next());
        }
    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private Node wizPos;

        LinkedListDequeIterator() {
            wizPos = sentinel;
        }

        public boolean hasNext() {
            return wizPos.next.item != null;
        }

        public T next() {
            wizPos = wizPos.next;
            T returnItem = wizPos.item;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (!(o instanceof LinkedListDeque)) {
            return false;
        }

        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (this.size != other.size) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!(other.get(i).equals(this.get(i)))) {
                return false;
            }
        }

        return true;
    }

    private static void main(String[] args) {
        LinkedListDeque<Integer> linkedListDeque = new LinkedListDeque<>();
        linkedListDeque.addFirst(5);
        linkedListDeque.addLast(23);
        linkedListDeque.addFirst(42);
        linkedListDeque.addLast(7);


        //* toString
        System.out.println(linkedListDeque);

        linkedListDeque.printDeque();

        linkedListDeque.printDequeIterator();

        //equals
        LinkedListDeque<Integer> linkedListDeque2 = new LinkedListDeque<>();
        linkedListDeque2.addFirst(5);
        linkedListDeque2.addLast(23);
        linkedListDeque2.addFirst(42);
        linkedListDeque2.addLast(7);

        System.out.println(linkedListDeque.equals(linkedListDeque2));
        System.out.println(linkedListDeque.equals(null));
        System.out.println(linkedListDeque.equals("fish"));
        System.out.println(linkedListDeque.equals(linkedListDeque2));
    }
}
