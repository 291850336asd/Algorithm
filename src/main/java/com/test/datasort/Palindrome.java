package com.test.datasort;

import java.util.Map;

/**
 * 回文数
 */
public class Palindrome {
    public static void main(String[] args) {

    }

    public static char[] manacherString(String str){
        char[] charArr = str.toCharArray();
        char[] res = new char[str.length()*2 +1];
        int index = 0;
        for (int i = 0; i != res.length; i++) {
            res[i] = (i&1) == 0 ? '#' : charArr[index++];
        }
        return res;
    }

    public static int maxLcpslength(String s){
        if(s== null || s.length() == 0){
            return 0;
        }
        char[] str = manacherString(s); // 121 -> #1#2#1#
        int[] pArr = new int[str.length]; // 回文半径数组
        int C= -1; //中心
        int R = -1; //回文有边界再往右一个位置 最右的有效区R-1
        int max = Integer.MIN_VALUE; //扩出来的最大值
        for (int i = 0; i != str.length ; i++) { // 每个位置求回文半径
            pArr[i] = R > i ? Math.min(pArr[2*C -i], R-i) : 1;
            while (i + pArr[i] < str.length && i-pArr[i] > -1){
                if(str[i + pArr[i]] == str[i-pArr[i]]){
                    pArr[i] ++;
                } else {
                    break;
                }
            }
            if(i + pArr[i] > R){
                R = i + pArr[i];
                C = i;
            }
            max = Math.max(max, pArr[i]);
        }
        return max - 1;
    }
}
