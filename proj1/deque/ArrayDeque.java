package deque;

import java.util.Iterator;

public class ArrayDeque<Item> implements Deque<Item> {
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (Item []) new Object[8];
        nextFirst = 0;
        nextLast = 1;
        size = 0;
    }

    private void resize(int capacity) {
        Item[] temp = (Item []) new Object[capacity];
        for (int i = 0; i < size; i++) {
            int index = getIndex(i);
            temp[i] = items[index];
        }
        items = temp;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    public void checkSize() {
        if (size == items.length) {
            resize(size * 2);
        } else if (size < items.length / 4 && items.length > 8) {
            resize(items.length / 4);
        }
    }

    public void addFirst(Item item) {
        checkSize();

        items[nextFirst] = item;
        size++;

        if (nextFirst == 0) {
            nextFirst = items.length - 1;
        } else {
            nextFirst--;
        }
    }

    public void addLast(Item item) {
        checkSize();

        items[nextLast] = item;
        size++;

        if (nextLast == items.length - 1) {
            nextLast = 0;
        } else {
            nextLast++;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            int index = getIndex(i);
            System.out.print(items[index] + " ");
        }

        System.out.println();
    }

    public Item removeFirst() {
        if (size == 0) {
            return null;
        }

        int index = getIndex(0);
        Item item = items[index];

        items[index] = null;
        nextFirst = index;

        size--;
        checkSize();

        return item;
    }

    public Item removeLast() {
        if (size == 0) {
            return null;
        }

        int index = getIndex(size - 1);
        Item item = items[index];

        items[index] = null;
        nextLast = index;

        size--;
        checkSize();

        return item;
    }

    public Item get(int index) {
        int i = getIndex(index);
        return items[i];
    }

    public int getIndex(int index) {
        int i = nextFirst + 1 + index;
        if (i > items.length - 1) {
            i = i - items.length;
        }
        return i;
    }

    public Iterator<Item> iterator() {
        return new ArrayDequeIterator();
    }

    public void printDequeIterator() {
        Iterator<Item> seer = iterator();

        while (seer.hasNext()) {
            System.out.println(seer.next());
        }
    }

    public class ArrayDequeIterator implements Iterator<Item> {
        private int wizPos;

        public ArrayDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public Item next() {
            Item returnItem = items[wizPos];
            wizPos += 1;
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

        if (!(o instanceof ArrayDeque)) {
            return false;
        }

        ArrayDeque<Item> other = (ArrayDeque<Item>) o;
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

    public static void main(String[] args) {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(5);
        arrayDeque.addLast(23);
        arrayDeque.addFirst(42);
        arrayDeque.addLast(7);


        //* toString
        System.out.println(arrayDeque);

        arrayDeque.printDeque();

        arrayDeque.printDequeIterator();

        //equals
        ArrayDeque<Integer> arrayDeque2 = new ArrayDeque<>();
        arrayDeque2.addFirst(5);
        arrayDeque2.addLast(23);
        arrayDeque2.addFirst(42);
        arrayDeque2.addLast(7);

        System.out.println(arrayDeque.equals(arrayDeque2));
        System.out.println(arrayDeque.equals(null));
        System.out.println(arrayDeque.equals("fish"));
        System.out.println(arrayDeque.equals(arrayDeque));

    }

}
