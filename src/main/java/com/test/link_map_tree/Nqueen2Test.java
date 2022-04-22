package com.test.link_map_tree;

public class Nqueen2Test {
    public static int num2(int n){
        if(n < 1 || n>32){
            return 0;
        }
       int limit = n == 32? -1 : (1<<n)-1;
        return process2(limit, 0, 0, 0);
    }
    public static int process2(int limit, int colLim, int leftDiaLim, int rightDiaLim){
        if(colLim == limit){
            return 1;
        }
        int pos = 0;
        int mostRightOne = 0;
        pos = limit & (~(colLim | leftDiaLim|rightDiaLim));
        int res = 0;
        while (pos != 0){
            mostRightOne = pos & (~pos +1);
            pos = pos - mostRightOne;
            res += process2(limit, colLim|mostRightOne, (leftDiaLim|mostRightOne) <<1,
                    (rightDiaLim | mostRightOne) >>> 1);
        }
        return res;
    }


}
