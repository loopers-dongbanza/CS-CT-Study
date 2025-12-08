import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    static int[] memorized;

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(br.readLine());

        memorized = new int[n+1];
        memorized[1] = 1;
        if(n >=2) memorized[2] = 2;
        
        for(int i = 3; i <= n; i++){
            memorized[i] = (memorized[i-1] + memorized[i-2]) % 10007;
        }

        System.out.println(memorized[n]);

    }
}
