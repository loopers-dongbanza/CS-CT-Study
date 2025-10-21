import java.util.*;

class Solution {
    public String solution(String number, int k) {

        StringBuilder sb = new StringBuilder();

        Stack<Character> stack = new Stack<Character>();

        for(char c : number.toCharArray()){

            while (!stack.isEmpty() && stack.peek() < c && k > 0) {
                stack.pop();
                k--;
            }
            stack.push(c);
        }

        while(k > 0){
            stack.pop();
            k--;
        }

        while(!stack.isEmpty()){
            sb.append(stack.pop());
        }

        return sb.reverse().toString();
    }
}