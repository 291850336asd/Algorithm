package com.test.link_map_tree;

public class HanoiTest {
    public static void main(String[] args) {
        int n = 4;
        hanoi(n);
    }


    private static void func(int i,String start, String end, String other){
        if(i == 1){
            System.out.println("Move 1 from " + start + " to " + end);
        } else {
            func(i-1, start, other, end);
            System.out.println("Move " + i + " from " + start + " to " + end);
            func(i-1, other, end, start);
        }
    }

    private static void hanoi(int n) {
        if(n>0){
            func(n, "左", "右", "中");
        }
    }
}
