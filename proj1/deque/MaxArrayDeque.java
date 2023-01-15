package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item> {
    private Comparator<Item> cmp;

    public MaxArrayDeque(Comparator<Item> c) {
        cmp = c;
    }

    public Item max(){
        if (isEmpty()) {
            return null;
        }

        Item maxItem = this.get(0);
        for (int i = 1; i < this.size(); i++) {
            if (cmp.compare(this.get(i), maxItem) > 0) {
                maxItem = this.get(i);
            }
        }
        return maxItem;
    }

    public Item max(Comparator<Item> c){
        if (isEmpty()) {
            return null;
        }

        Item maxItem = this.get(0);
        for (int i = 1; i < this.size(); i++) {
            if (cmp.compare(this.get(i), maxItem) > 0) {
                maxItem = this.get(i);
            }
        }
        return maxItem;
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
