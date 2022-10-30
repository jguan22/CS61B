public class DrawATriangle {
    public static void main(String[] args) {
        int size = 5;
        int col = 0;
        int row = 0;
        while (row < size) {
            while(col <= row) {
                System.out.print("*");
                col = col + 1;
            }
            System.out.println("");
            col = 0;
            row = row + 1;
        }
    }
}
