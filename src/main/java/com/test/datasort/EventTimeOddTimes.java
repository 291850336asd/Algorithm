package com.test.datasort;

public class EventTimeOddTimes {

    public static void printOddTimeNum1(int[] arr){
        int eor = 0;
        for (int cur : arr){
            eor ^=cur;
        }
        System.out.println(eor);
    }

    public static void printOddTimesNum2(int[] arr){
        int eor = 0, onlyOne = 0;
        for (int curNum: arr){
            eor ^= curNum;
        }
        int rightOne = eor & (~eor + 1);
        for (int cur : arr){
            if((cur & rightOne) == 0){
                onlyOne ^= cur;
            }
        }

        System.out.println(onlyOne + "  " + (eor^onlyOne));
    }

}
