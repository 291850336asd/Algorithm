package com.test.datasort;

public class Two_PowerTest {

    public static boolean is2Power(int n){
        return (n & (n-1)) == 0;
    }

    public static boolean is4Power(int n){
        // 0x55555555  == ....1010101
        return (n & (n-1)) == 0 && (n & 0x55555555) != 0;
    }
}
