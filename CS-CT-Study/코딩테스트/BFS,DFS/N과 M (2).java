import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    static int n;
    static int targetCount;
    static int[] result;

    public static void main(String[] args) throws IOException {
        input();
        combination(1, 0);
    }

    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        targetCount = Integer.parseInt(st.nextToken());
        result = new int[targetCount];
    }

    private static void combination(int start, int cnt) {
        if(targetCount == cnt){
            for(int i = 0; i < cnt; i++){
                System.out.print(result[i] + " ");
            }
            System.out.println();
            return;
        }


        for (int i = start; i <= n; i++) {
            result[cnt] = i;
            combination(i + 1, cnt + 1);

        }
    }
}
