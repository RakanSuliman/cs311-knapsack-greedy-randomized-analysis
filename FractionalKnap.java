import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class FractionalKnap  {

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

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

//        Read headers as n W
        String head = nonEmpty(br);
        if (head == null) {System.err.println("No Input"); return;}

        String[] h = head.split("\\s+");
        if (h.length < 2) {System.err.println("First line must be: <n> <W>"); return;}

        int n,BigW;
        try {
            n = Integer.parseInt(h[0]);
            BigW = Integer.parseInt(h[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid <n> or <W>");
            return;
        }
        if (n<0 || BigW<0)  {System.err.println("<n> or <W> must be non-negative"); return;}

        Item[] items = new Item[n];
        for (int i = 0; i < n; i++) {
            String line = nonEmpty(br);
            if (line == null) {System.err.println("Expected " + n  +" items " + "\n Got " + i); return;}
            String[] t = line.split("\\s+");
            if (t.length <2) {System.err.println("Item line must be <v> <W>"); return;}
            int v, w;
            try {
                v = Integer.parseInt(t[0]);
                w = Integer.parseInt(t[1]);
            } catch ( NumberFormatException e) {System.err.println("Invalid <n> or <W> at line " + i+1); return;}
            if (v<0 || w<0) {System.err.println("Invalid must be non-negative <v> or <W> at item #" + i+1); return;}
            items[i] = new Item(i,v,w);
        }
//        Sort by v/w ratio DESCENDING
        Item[] order = items.clone();
        Arrays.sort(order, (a,b) -> {
            if (a.ratio == b.ratio) {return 0;}
            return (a.ratio > b.ratio) ? -1 : 1;
        });

        double[] x = new double[n];
        double remaining = BigW;
        double totalV = 0.0, totalW = 0.0;

        for (Item it: order) {
            if (remaining <= 0) break;
            if(it.w == 0) {
                if (it.v > 0) {
                    x[it.idx] = 1.0;
                    totalV += it.v;
                }
                continue;
            }

            if (it.w <= remaining) {
                x[it.idx] = 1.0;
                remaining -= it.w;
                totalV += it.v;
                totalW += it.w;
            } else {
                double frac = remaining / it.w;
                x[it.idx] = frac;
                totalW += frac * it.w;
                totalV += frac * it.v;
                remaining = 0;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<n; i++) {
            if(i>0) sb.append(" ");
            sb.append(format6_dig(x[i]));
        }

        System.out.println(sb.toString());
        System.out.println("#value=" + format6_dig(totalV) + " #Weight= " + format6_dig(totalW) + " #capacity= " + BigW);
    }
//    HELPERS
    public static String nonEmpty(BufferedReader br) throws IOException {
        for (String s; (s=br.readLine()) != null; ) {
            s = s.trim();
            if (!s.isEmpty()) return s;
        }
        return null;
    }
    public static String format6_dig(double x) {
        String s = String.format(java.util.Locale.US, "%.6f", x);
        return s.equals("-0.000000") ? "0.000000": s;
    }
}
