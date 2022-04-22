package com.test.link_map_tree;

public class CoinsWays {
    public static int way(int[] arr, int aim){
        return process(arr, 0, aim);
    }

    public static int process(int[] arr, int index, int rest){
        if(index == arr.length){
            return rest== 0 ? 1:0;
        }
        int ways = 0;
        for (int zhang = 0; zhang * arr[index] <= rest; zhang++) {
            ways +=process(arr, index+1, rest-arr[index]*zhang);
        }
        return ways;
    }

}
