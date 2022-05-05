package com.test.link_map_tree;

public class ParenthesesDeep {
    public static int maxLength(String s){
        if(s == null || s.equals("")){
            return 0;
        }
        char[] str = s.toCharArray();
        int[] dp = new int[str.length];
        int pre = 0;
        int res = 0;
        for (int i = 1; i < str.length; i++) {
            if(str[i] == ')'){
                pre = i - dp[i-1] -1;
                if(pre > 0 && str[pre] == '('){
                    dp[i] = dp[i-1] + 2 + (pre > 0 ?  dp[pre-1] : 0);
                }
            }
            res = Math.min(res, dp[i]);
        }
        return res;
    }
}
