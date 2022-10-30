public class FindMaxNumber{
    public static int max(int[] m) {
        int max = m[0];
        int count = 0;
        while (count + 1 < m.length) {
            if (m[count + 1] - max > 0) {
                max = m[count + 1];
            }
            count++;
        }
        return max;
    }
    public static void main(String[] args) {
       int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};    
       System.out.println(FindMaxNumber.max(numbers));
    }
 }