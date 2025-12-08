import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
    static int n;
    static int targetCount;
    static boolean[] visited;
    static int[] result;
    
    public static void main(String[] args) throws IOException {  
        input();
        permutation(0);  
    }
    
    private static void input() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        targetCount = Integer.parseInt(st.nextToken());
        visited = new boolean[n+1];
        result = new int[targetCount]; 
    }
    
    private static void permutation(int cnt) {
        if(targetCount == cnt){
            for(int i = 0; i < cnt; i++){
                System.out.print(result[i] + " ");
            }
            System.out.println();
            return;  
        }
        
        for (int i = 1; i <= n; i++) {
            if(!visited[i]){
                visited[i] = true;
                result[cnt] = i;
                permutation(cnt+1);
                visited[i] = false;
            }
        }
    }
}
