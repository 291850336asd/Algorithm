package com.test.datasort;

public class SelectSort {

    public static void selectSort(int[] arr){
        if(arr == null || arr.length < 2){
            return;
        }
        for (int i=0; i< arr.length; i++){
            int minIdex = i;
            for (int j = i+1; j < arr.length; j++) {
                minIdex = arr[j] < arr[minIdex] ? j :minIdex;
            }
            swap(arr, i ,minIdex);
        }
    }

    public static void swap(int[] arr, int i, int j){
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
