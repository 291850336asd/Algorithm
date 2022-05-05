package com.test.link_map_tree;

public class UniqueBST {

    public static int process(int n){
        if(n < 0){
            return 0;
        }
        if(n ==0 || n==1){
            return 1;
        }
        if(n == 2){
            return 2;
        }
        int res = 0;
        for (int leftNUm = 0; leftNUm < n-1; leftNUm++) {
            int leftWays = process(leftNUm);
            int rightWays = process(n-1 -leftNUm);
            res += leftWays * rightWays;
        }
        return res;
    }

    public static int numTrees(int n){
        if(n < 2){
            return 1;
        }
        int[] dp = new int[n+1];
        dp[0] = 1;
        for (int i = 1; i < n+1; i++) {   //节点个数为i的时候
            for (int j = 0; j <= i-1; j++) {  //左侧节点格式为j-1,右侧节点个数为i-j
                dp[i] += dp[j]*dp[i-j-1];
            }
        }
        return dp[n];
    }


}
