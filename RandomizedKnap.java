import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Arrays;

public class RandomizedKnap {
    private static int TRIALS = 1000;
    private static int TOP_K = 100;
    private static long SEED = 44L;

    static class Result {
        final int[] choose;
        final int totalV, totalW;
        Result(int[] c, int v, int w) {choose = c; totalV = v; totalW = w;}
    }


    public static void main(String[] args) throws Exception {
        if (args.length > 0) try { TRIALS = Math.max(1, Integer.parseInt(args[0])); } catch (Exception ignored) {}
        if (args.length > 1) try { TOP_K  = Math.max(1, Integer.parseInt(args[1])); } catch (Exception ignored) {}
        if (args.length > 2) try { SEED   = Long.parseLong(args[2]); } catch (Exception ignored) {}
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
        int n, W;
        try {
            n = Integer.parseInt(fh[0]);
            W = Integer.parseInt(fh[1]);
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
        Result r1 = randomSamplingOnce(val,wt,W,new Random(SEED));
        System.out.println("Random Sampling");
        System.out.println("#Value= " + (r1.totalV) + " #Weight= " + (r1.totalW) + " #Capacity= " + W);
        Result r2 = monteCarloRandom(val, wt,W,TRIALS,SEED);
        System.out.println("Monte Carlo Random (BEST OF " + TRIALS + " TRIALS):");
        System.out.println("#Value= " + (r2.totalV) + " #Weight= " + (r2.totalW) + " #Capacity= " + W);
        Result r3 = monteCarloTopK(val, wt,W, TRIALS,TOP_K,SEED);
        System.out.println("Monte Carlo (TOP- " + TOP_K + " by value, best of " + TRIALS + " TRIALS):");
        System.out.println("#Value= " + (r3.totalV) + " #Weight= " + (r3.totalW) + " #Capacity= " + W);


    }

    private static Result randomSamplingOnce(int[] val, int[] wt, int W, Random rand) {
        int n =  val.length;
        int[] order = new int[n];
        for (int i = 0; i < n; i++) order[i] = i;

        for (int i = n-1; i>0; i--) {
            int j = rand.nextInt(i+1);
            int tmp = order[i];
            order[i] = order[j];
            order[j] = tmp;
        }
//        random coin flips
        return packEval(order,val,wt,W,true,rand);
    }
private static Result monteCarloRandom(int[] val, int[] wt, int W, int trials, long seed) {
       Random rand = new Random(seed);
       Result best = null;
       for (int t = 0; t<trials; t++) {
           Result r = randomSamplingOnce(val, wt, W, rand);
           if (best == null || r.totalV > best.totalV) {best = r;}
       }
       return best;
}

private static Result monteCarloTopK(int[] val, int[] wt, int W, int trials, int K, long seed) {
        int n  = val.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;

        Arrays.sort(idx, (a,b) -> {
       int c = Integer.compare(val[b], val[a]);
       if (c != 0) return c;
       c=Integer.compare(wt[a], wt[b]);
       if (c != 0) return c;
       return Integer.compare(a,b);
    });
        int m = Math.min(K,n);
        int[] top = new int[m];
        for(int i =0; i<m; i++) top[i] = idx[i];
        Random rand = new Random(seed);
        Result best = null;
        for (int t = 0; t<trials; t++) {
            for(int i =m-1;i>0; i--) {
                int j = rand.nextInt(i+1);
                int tmp = top[i]; top[i] = top[j]; top[j] = tmp;
            }
            Result r = packEval(top,val,wt,W,false,rand);
            if (best == null || r.totalV > best.totalV) {best = r;}
        }
        return best;
}
    private static Result packEval(int[] order, int[] val, int[] wt, int W, boolean coinFlip, Random rand) {
        int n = val.length;
        int[] choose = new int[n];
        int totalW = 0, totalV= 0;
        for (int i = 0; i < n; i++) {
            if (wt[i] == 0 && val[i] > 0) {
                choose[i] = 1; totalV += val[i];
            }
        }
        for (int idx:order) {
            if(wt[idx] == 0) continue;
            if (coinFlip && !rand.nextBoolean()) continue;
            if (totalW + wt[idx] <= W) {
                choose[idx] = 1;
                totalW += wt[idx];
                totalV += val[idx];
            }
        }
        return new Result(choose, totalV, totalW);
    }

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
