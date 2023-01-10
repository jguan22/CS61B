package deque;

public class ArrayDeque<Item> {
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

    public boolean isEmpty() {
        return size == 0;
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

}
