public class TriangleDrawer{
    public static void drawTriangle(int N) {
        int size = N;
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

    public static void main(String[] args) {
        TriangleDrawer.drawTriangle(10);
    }
}