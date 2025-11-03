import java.io.*;

// This is a Code implementation of 0-1 Knapsack problem + DP [Dynamic Programming]
public class ZeroOneKnap {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

//        Read first line n [elements] W [weight]

        String first = noneEmpty(br);
        if(first == null){
            System.out.println("the input is Null");
            return;
        }
        String[] fh = first.trim().split("\\s+");
        if(fh.length < 2){
            System.out.println("The first row must contain <n> elements <W> Capacity.");
            return;
        }
//        The previous two if conditions is just to check the nullaity of the input
//        and the length of the input
        int n,w;
        try {
            n = Integer.parseInt(fh[0]);
            w = Integer.parseInt(fh[1]);
        } catch (NumberFormatException e) {
            System.err.println("The first row must contain <n> elements <W> Capacity as integers"+ " " + e.getMessage());
            return;
        }
        if (n < 0 || w < 0) {
            System.err.println("n and W must be non-negative integers");
            return;
        }
//        Read n items
        int[] wt = new int[n];
        int[] val =  new int[n];

        for (int i = 0; i < n; i++) {
            String line = noneEmpty(br);
            if (line == null) {
                System.err.println("Expected " + n + " elements " + i + " received");
                return;
            }
           String[] t = line.trim().split("\\s+");
            if (t.length < 2) {
                System.err.println("Invalid form " + i + " the valid form is <weight> <value>");
                return;
            }
            try {
                wt[i] = Integer.parseInt(t[1]);
                val[i] = Integer.parseInt(t[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid Integers items on line" + (i+1));
                return;
            }
            if (wt[i] < 0 || val[i] < 0) {
                System.err.println("n and W must be non-negative integers (item " + (i+1) + ")");
                return;
            }

        }

        int[] dp = new int[w+1];
        boolean[][] keep = new boolean[n][w+1];

        for (int i = 0; i < n; i++) {
            int w1 = wt[i], v = val[i];
            for (int j = w; j>= w1; j--) {
                int cand = dp[j-w1] + v;
                if (cand > dp[j]) {
                    dp[j] = cand;
                    keep[i][j] = true;
                }
            }
        }

        int[] choose = new int[n];
        int c = w, totalW =0, totalV = 0;
        for (int i = n-1; i >= 0; i--) {
            int w1 = wt[i], v = val[i];
            if (c >= 0 && keep[i][c]) {
                choose[i] = 1;
                c -=w1;
                totalW += w1;
                totalV += v;
            } else {
                choose[i] = 0;

            }
        }
        StringBuilder bits = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if(i>0) bits.append(" ");
            bits.append(choose[i]);
        }
        System.out.println(bits);
        System.out.println("#value = " + totalV + " #weight = " + totalW + " #capacity= " + w);
    }
    private static String noneEmpty(BufferedReader br) throws IOException {
        for (String s; (s=br.readLine())!=null;) {
            s = s.trim();
            if(!s.isEmpty()) return s;
        }
        return null;
    }

}


