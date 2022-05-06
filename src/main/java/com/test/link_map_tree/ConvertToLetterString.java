package com.test.link_map_tree;

import java.nio.charset.StandardCharsets;

public class ConvertToLetterString {

    public static void main(String[] args) {
        System.out.println(process("1111".toCharArray(), 0));
    }

    public static int process(char[] str, int i){
        if(i == str.length){
            return 1;
        }
        if(str[i] == '0'){
            return 0;
        }
        if(str[i]=='1'){
            int res = process(str, i+1);
            if(i+1 < str.length){
                res += process(str, i+2);
            }
            return res;
        }
        if(str[i] == '2'){
            int res = process(str, i+1);
            if(i+1 < str.length && (str[i+1]>='0' && str[i+1]<='6')){
                res += process(str, i+2);
            }
            return res;
        }
        return process(str, i+1);
    }


    public static int process2(char[] str, int index){
        if(index == str.length){
            return 1;
        }
        if(str[index] == '0'){
            return 0;
        }
        int res = process2(str, index + 1);
        if(index == str.length -1){
            return res;
        }
        if(((str[index] -'0')*10 + str[index+1]-'0') < 27){
            res += process2(str, index+2);
        }
        return res;
    }

    public static int dpWays(int num){
        if(num < 1){
            return 0;
        }
        char[] str = String.valueOf(num).toCharArray();
        int N = str.length;
        int[] dp = new int[N +1];
        dp[N] =1;
        dp[N-1] = str[N-1] =='0' ? 0:1;
        for (int i = N-2; i >=0; i--) {
            if(str[i] == '0'){
                dp[i] = 0;
            } else {
                dp[i] = dp[i+1] +
                        (((str[i] -'0')*10 + str[i+1]-'0') < 27 ? dp[i+2] : 0);
            }
        }
        return dp[0];
    }


}
