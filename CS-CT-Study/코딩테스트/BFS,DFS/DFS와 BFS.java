import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static ArrayList<Integer>[] graph;
    static boolean[] visited;
    static int n;
    static int m;
    static int startNode;
    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws IOException {

        inputAndInitGraph();

        visited = new boolean[n + 1];
        dfs(startNode);
        sb.append("\n");

        visited = new boolean[n + 1];
        bfs(startNode);

        System.out.println(sb);
    }

    private static void dfs(int point) {
        visited[point] = true;
        sb.append(point).append(" ");

        for (int next : graph[point]) {
            if (!visited[next]) {
                dfs(next);
            }
        }
    }

    private static void bfs(int startNode) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startNode);
        visited[startNode] = true;
        sb.append(startNode).append(" ");

        while (!queue.isEmpty()) {
            Integer poll = queue.poll();

            for (int next : graph[poll]) {
                if (!visited[next]) {
                    queue.add(next);
                    visited[next] = true;
                    sb.append(next).append(" ");
                }
            }
        }
    }

    private static void inputAndInitGraph() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        startNode = Integer.parseInt(st.nextToken());


        graph = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++) {
            graph[i] = new ArrayList<>();
        }


        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            graph[from].add(to);
            graph[to].add(from);
        }
        for (int i = 1; i <= n; i++) {
            Collections.sort(graph[i]);
        }
    }
}
