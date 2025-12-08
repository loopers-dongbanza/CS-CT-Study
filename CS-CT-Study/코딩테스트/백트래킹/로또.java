import java.io.*;
import java.util.*;

public class Main {
    static int k;
    static int[] S;
    static int[] result = new int[6];
    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        boolean first = true;

        while ((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line);
            k = Integer.parseInt(st.nextToken());

            if (k == 0) break;


            S = new int[k];
            for (int i = 0; i < k; i++) {
                S[i] = Integer.parseInt(st.nextToken());
            }

            if (!first) {
                sb.append("\n");
            }
            first = false;

            dfs(0, 0);
        }

        System.out.print(sb.toString());
    }


    private static void dfs(int idx, int count) {

        if (count == 6) {
            for (int i = 0; i < 6; i++) {
                sb.append(result[i]);
                if (i < 5) sb.append(" ");
            }
            sb.append("\n");
            return;
        }


        int need = 6 - count;
        int remain = k - idx;
        if (remain < need) {
            return;
        }

        for (int i = idx; i < k; i++) {
            result[count] = S[i];
            dfs(i + 1, count + 1);
        }
    }
}
