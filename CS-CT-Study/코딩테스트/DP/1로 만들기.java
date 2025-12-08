import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    static int[] memorized;

    public static void main(String[] args) throws IOException {


        // bottum up
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        int[] memorize = new int[n + 1];

        for (int i = 2; i <= n; i++) {
            memorize[i] = memorize[i - 1] + 1;
            if (i % 2 == 0) memorize[i] = Math.min(memorize[i], memorize[i / 2] + 1);
            if (i % 3 == 0) memorize[i] = Math.min(memorize[i], memorize[i / 3] + 1);
        }
        System.out.println(memorize[n]);
    }
}
