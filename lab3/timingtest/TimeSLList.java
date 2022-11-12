package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        SLList L = new SLList<>();
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCount = new AList<>();

        int ops = 10000;
        int N = 500;
        for (int i = 0; i <= 128000; i++) {
            L.addLast(i);
            if (i == N * 2) {
                Stopwatch sw = new Stopwatch();
                for (int j = 0; j < ops; j++){
                    L.getLast();
                }
                N = N * 2;
                Ns.addLast(N);
                times.addLast(sw.elapsedTime());
                opCount.addLast(ops);
            }
        }

        printTimingTable(Ns, times, opCount);

    }

}
