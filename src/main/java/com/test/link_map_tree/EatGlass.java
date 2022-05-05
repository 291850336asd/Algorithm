package com.test.link_map_tree;

public class EatGlass {
    public static String winner1(int n){
        if(n < 5){
            return (n==0 || n==2) ? "后手" : "先手";
        }
        int base = 1; //先手决定吃的草
        while (base <=n){
            // 后手返回了后手 就是先手
            if(winner1(n -base).equals("后手")){
                return "先手";
            }
            if(base > n/4){ //防止base*4之后溢出
                break;
            }
            base *=4;
        }
        return "后手";
    }
    public static String winner2(int n){
        if((n%5 == 0) || (n%5 == 2)){
            return "后手";
        } else {
            return "先手";
        }
    }

}
