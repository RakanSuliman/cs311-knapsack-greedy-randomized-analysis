import java.io.*;
import java.util.Arrays;

// This is a Code implementation of 0-1 Knapsack problem + DP [Dynamic Programming]
public class ZeroOneKnap {

    static class Result {
        int[] choose;
        int totalV, totalW;
        Result(int[] c, int v, int w) {choose=c; totalV=v; totalW=w;}
    }

    static class GItem{
        int idx, v, w; double ratio;
        GItem(int idx, int v, int w){
            this.idx = idx; this.v = v; this.w = w;
            this.ratio = (w==0) ? (v>0 ? Double.POSITIVE_INFINITY:0.0) : (double) v/w;
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

//        Reading headers[First row]: n W
        String first = noneEmpty(br);
        if (first == null) {
            System.err.println("Input is empty");
            return;
        }
        String[] fh = first.trim().split("\\s+");
        if (fh.length < 2) {
            System.err.println("First row must be: <n> <W>");
            return;
        }
        int n, w;
        try {
            n = Integer.parseInt(fh[0]);
            w = Integer.parseInt(fh[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid n/W");
            return;
        }

//        Reading the items
        int[] val = new int[n], wt = new int[n];
        for (int i = 0; i < n; i++) {
            String line = noneEmpty(br);
            if (line == null) {
                System.err.println("Expected " + n + " items, and got " + i);
                return;
            }
            String[] t = line.trim().split("\\s+");
            if (t.length < 2) {
                System.err.println("Invalid format, must be: <value> <weight>");
                return;
            }
            try {
                val[i] = Integer.parseInt(t[0]);
                wt[i] = Integer.parseInt(t[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid format or item on line " + (i + 1));
                return;
            }
            if (val[i] < 0 || wt[i] < 0) {
                System.err.println("Non-negative values only");
                return;
            }
        }
        Result dp = runDP(val, wt, w);
        System.out.println("DP (Optimal)");
//        printBits(dp.choose);
        System.out.println("#Value = " + dp.totalV + " #Weight " + dp.totalW + " #Capacity " + w);

        Result greedy1 = runGreedy(val,wt,w,1); // Default case
        System.out.println("Greedy 1 (Highest v/w ratio)");
//        printBits(greedy1.choose);
        System.out.println("#Value = " + greedy1.totalV + " #Weight " + greedy1.totalW + " #Capacity " + w);

        Result greedy2 = runGreedy(val,wt,w,2);
        System.out.println("Greedy 2 (Highest value first)");
//        printBits(greedy2.choose);
        System.out.println("#Value = " + greedy2.totalV + " #Weight " + greedy2.totalW + " #Capacity " + w);

        Result greedy3 = runGreedy(val,wt,w,3);
        System.out.println("Greedy 3 (Lowest value first)");
//        printBits(greedy3.choose);
        System.out.println("#Value " + greedy3.totalV + " #Weight " + greedy3.totalW + " #Capacity " + w);
    }

//        --DP (Optimal)
        private static Result runDP(int[] val, int[] wt, int W) {
            int n = val.length;
            int[] dp = new int[W + 1];
            boolean[][] keep = new boolean[n][W + 1];

            for (int i = 0; i < n; i++) {
                int w1 = wt[i], v = val[i];
                for (int j = W; j >= w1; j--) {
                    int cand = dp[j - w1] + v;
                    if (cand > dp[j]) {
                        dp[j] = cand;
                        keep[i][j] = true;
                    }
                }
            }

            int[] choose = new int[n];
            int c = W, totalW = 0, totalV = 0;
            for (int i = n - 1; i >= 0; i--) {
                int w1 = wt[i], v = val[i];
                if (c >= w1 && keep[i][c]) {
                    choose[i] = 1;
                    c -= w1;
                    totalW += w1;
                    totalV += v;
                } else {
                    choose[i] = 0;

                }
            }
            return new Result(choose, totalV, totalW);
        }
//        -- Greedy for 0/1
//    mode: 1=ratio desc, 2=value desc, 3=value asc

    private static Result runGreedy(int[] val, int[] wt, int W, int mode) {
        int n = val.length;
        GItem[] items = new GItem[n];
        for (int i = 0; i < n; i++) {items[i] = new GItem(i, val[i], wt[i]);}

        Arrays.sort(items, (a,b)->{
            switch (mode) {
                case 2: // Value Desc
                    int vd = Integer.compare(b.v, a.v);
                    return (vd !=0) ? vd: Integer.compare(a.w, b.w);
                case 3: // Value asc
                    int va =  Integer.compare(a.v, b.v);
                    return (va != 0) ? va: Integer.compare(a.w, b.w);
                default: // Ratio desc
                    int rc = Double.compare(b.ratio, a.ratio);
                    if (rc != 0) return rc;
                    int vt = Integer.compare(b.v, a.v);
                    return (vt != 0) ? vt: Integer.compare(a.w, b.w);
            }
        });
        int[] choose = new int[n];
        int totalW = 0, totalV = 0;

        for (GItem it:  items) if (it.w == 0 && it.v > 0) {choose[it.idx] = 1; totalV += it.v;}
        for  (GItem it:  items) {
            if (it.w ==0) continue;
            if (totalW + it.w <= W) {choose[it.idx] = 1; totalW += it.w; totalV += it.v;}
        }
        return new Result(choose, totalV, totalW);
    }

// Helpers
private static void printBits(int [] choose) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < choose.length; i++) {if (i>0) sb.append(" "); sb.append(choose[i]);}
            System.out.println(sb.toString());
}
    private static String noneEmpty(BufferedReader br) throws IOException {
        for (String s; (s=br.readLine())!=null;) {
            s = s.trim();
            if(!s.isEmpty()) return s;
        }
        return null;
    }
    }



