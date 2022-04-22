package com.test.datasort;

public class BSearch {

    public static int bsearch(int[] array, int low, int high, int target){
        if(low > high){
            return -1;
        }
        int mid = (low + high)/2;
        if(array[mid] > target){
            return bsearch(array, low, mid-1, target);
        }
        if(array[mid] < target){
            return bsearch(array, mid +1, mid, target);
        }
        return mid;
    }
}
