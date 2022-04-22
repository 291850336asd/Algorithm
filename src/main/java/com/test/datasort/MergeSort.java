package com.test.datasort;

public class MergeSort {

    public static void mergeSort(int[] arr){
        if(arr == null || arr.length < 2){
            return;
        }
        process(arr, 0, arr.length -1);
    }

    public static void process(int[] arr, int l ,int r){
        if(l == r){
            return;
        }

        int mid = l + (r-l) >> 1;
        process(arr,l, mid);
        process(arr,mid+ 1, r);
        merge(arr, l, mid, r);
    }

    public static void merge(int[] arr, int l,int m, int r){
        int[] helper = new int[r-l+1];
        int i = 0;
        int p1=l;
        int p2 = m+1;
        while (p1 <=m && p2<=r){
            helper[i++] = arr[p1] <= arr[p2] ? arr[p1++] : arr[p2++];
        }
        while (p1<=m){
            helper[i++] = arr[p1++];
        }
        while (p2<=r){
            helper[i++] = arr[p2++];
        }

        for (i = 0; i < helper.length; i++) {
            arr[l + i] = helper[i];
        }

    }



}
