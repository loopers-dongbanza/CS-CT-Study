import java.util.*;

class Solution {
    public int solution(int n, int[] lost, int[] reserve) {

        int[] uniforms = new int[n + 1];


        for (int student : lost) {
            uniforms[student]--;
        }


        for (int student : reserve) {
            uniforms[student]++;
        }


        for (int i = 1; i <= n; i++) {
            if (uniforms[i] == -1) {
                if (i > 1 && uniforms[i - 1] == 1) {
                    uniforms[i]++;
                    uniforms[i - 1]--;
                } else if (i < n && uniforms[i + 1] == 1) {
                    uniforms[i]++;
                    uniforms[i + 1]--;
                }
            }
        }


        int count = 0;
        for (int i = 1; i <= n; i++) {
            if (uniforms[i] >= 0) {
                count++;
            }
        }

        return count;
    }
}