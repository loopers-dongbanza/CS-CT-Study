import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    static int n;
    static int money;
    static int coin[];

    public static void main(String[] args) throws IOException {

        innit();
        int result = solve();

        System.out.println(result);


    }

    private static void innit() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        money = Integer.parseInt(st.nextToken());
        coin = new int[n+1];

        for(int i = 1; i <= n; i++){
            coin[i] = Integer.parseInt(br.readLine());
        }
    }

    private static int solve() {

        int result = 0;

        while(money != 0){
            if(money >= coin[n]){
                money -= coin[n];
                result++;
            }else{
                n--;
            }

        }
        return result;
    }
}
