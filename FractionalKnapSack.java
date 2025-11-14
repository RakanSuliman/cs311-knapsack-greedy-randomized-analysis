import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class FractionalKnapSack  {

    static class Item {
        int idx, v,w;
        double ratio;
        Item(int idx, int v, int w) {
            this.idx = idx;
            this.v = v;
            this.w = w;
            if (w==0) {
                this.ratio= (v>0) ? Double.POSITIVE_INFINITY : 0.0;
            } else {
                this.ratio = (double) v / (double) w;
            }
        }
    }

    static class Result {
        final double[] x;
        final double totalV, totalW;
        Result(double[] x, double totalV, double totalW) {this.x = x;this.totalV = totalV;this.totalW = totalW;}
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
        int n, W;
        try {
            n = Integer.parseInt(fh[0]);
            W = Integer.parseInt(fh[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid n/W");
            return;
        }

//        Reading the items
        Item[] items = new Item[n];
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
            int v, w;
            try {
                v = Integer.parseInt(t[0]);
                w = Integer.parseInt(t[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid format or item on line " + (i + 1));
                return;
            }
            if (v < 0 || w < 0) {
                System.err.println("Non-negative values only");
                return;
            }
            items[i] = new Item(i, v, w);
        }
// RUN ALL

        System.out.println("Greedy 1: highest value/weight ratio");
        Result r1 = runFractional(items,W,1);
        System.out.println("#Value" + format6_dig(r1.totalV) + " #Weight" + format6_dig(r1.totalW) + " #Capacity " + W);


        System.out.println("Greedy 2: highest value first");
        Result r2 = runFractional(items,W,2);
        System.out.println("#Value" + format6_dig(r2.totalV) + " #Weight" + format6_dig(r2.totalW) + " #Capacity " + W);


        System.out.println("Greedy 3: lowest value first");
        Result r3 = runFractional(items,W,3);
        System.out.println("#Value" + format6_dig(r3.totalV) + " #Weight" + format6_dig(r3.totalW) + " #Capacity " + W);


    }

    private static Result runFractional(Item[] items, int W, int mode) {
        int n =  items.length;
        Item[] order = items.clone();
        Arrays.sort(order, (a,b) -> {
            switch (mode) {
                case 2: {
                    int c = Integer.compare(b.v, a.v);
                    if (c != 0) return c;
                    c = Integer.compare(a.w, b.w);
                    if (c != 0) return c;
                    return Integer.compare(a.idx, b.idx);
                }
                case 3: {
                    int c = Integer.compare(a.v, b.v);
                    if (c != 0) return c;
                    c = Integer.compare(a.w, b.w);
                    if (c != 0) return c;
                    return Integer.compare(a.idx, b.idx);
                }
                default:
                    int c = Double.compare(b.ratio, a.ratio);
                    if (c != 0) return c;
                    c = Integer.compare(b.v, a.v);
                    if (c != 0) return c;
                    c = Integer.compare(a.w, b.w);
                    if (c != 0) return c;
                    return Integer.compare(a.idx, b.idx);
            }
        });

        double[] x = new double[n];
        double remaining = W;
        double totalV = 0.0, totalW = 0.0;

        for(Item it: order) {
            if (remaining <= 0) break;

            if(it.w ==0) {
                if (it.v > 0) {x[it.idx] = 1.0; totalV += it.v;}
                continue;
            }
            if (it.w <= remaining) {
                x[it.idx] = 1.0;
                remaining -= it.w;
                totalV += it.v;
                totalW += it.w;
            } else {
                double frac =  remaining / it.w;
                x[it.idx] = frac;
                remaining = 0;
                totalV += frac*it.v;
                totalW += frac*it.w;
            }
        }
        return new Result(x, totalV, totalW);
    }
//    HELPERS
private static String noneEmpty(BufferedReader br) throws IOException {
    for (String s; (s=br.readLine())!=null;) {
        s = s.trim();
        if(!s.isEmpty()) return s;
    }
    return null;
}
    public static String format6_dig(double x) {
        String s = String.format(java.util.Locale.US, "%.6f", x);
        return s.equals("-0.000000") ? "0.000000": s;
    }


}
