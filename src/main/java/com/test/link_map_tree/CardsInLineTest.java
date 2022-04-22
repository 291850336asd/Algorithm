package com.test.link_map_tree;

public class CardsInLineTest {
    public static int win1(int[] arr){
        if(arr == null || arr.length == 0){
            return 0;
        }
        return Math.max(f(arr, 0, arr.length -1), s(arr, 0, arr.length -1));
    }
    public static int f(int[] arr, int i, int j){
        if(i ==j){
            return arr[i];
        }
        return Math.max(arr[i] + s(arr, i+1, j), arr[j] + s(arr, i, j-1)) ;
    }

    public static int s(int[] arr, int i, int j){
        if(i ==j){
            return 0;
        }
        return Math.min(f(arr, i+1, j), f(arr, i, j-1));
    }


    public static void main(String[] args) {
        int[] arr = {1,2,3,4,5,2};
        System.out.println(win1(arr));
        System.out.println(win1(arr));
    }
}
