package com.test.datasort;

public class GetMaxTest {

    // 1-> 0    0->1
    public static int flip(int n){
        return n ^1;
    }

    // n>=0 ->1    n<0 -> 0
    public static int sign(int n){
        return flip((n>>31) &1);
    }
    public static int getMax1(int a, int b){
        int c= a-b;  //a\b符号不一样,存在溢出风险
        int scA = sign(c);  //a-b >=0  ->1    a-b<0 -> 0
        int scB = flip(scA);
        return a*scA + b*scB;
    }

    public static int getMax2(int a, int b){
        int c= a-b;
        int sa = sign(a);
        int sb = sign(b);
        int sc = sign(c);
        int difSab = sa ^ sb;  //a\b符号不一样为1，一样为0
        int sameSab = flip(difSab); //a\b符号一样为1，不一样为0
        int returnA = difSab * sa + sameSab * sc;
        int returnB = flip(returnA);
        return a * returnA + b * returnB;
    }




}
