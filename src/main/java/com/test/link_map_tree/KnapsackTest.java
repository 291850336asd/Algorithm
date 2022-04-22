package com.test.link_map_tree;

public class KnapsackTest {

    public static int process(int[] weights, int[] values, int i,int alreayweight, int bag){
        if(alreayweight > bag){
            return 0;
        }
        if(i == weights.length){
            return 0;
        }
        return Math.max(
                process(weights, values, i+1, alreayweight,bag),
                values[i] + process(weights, values, i+1, alreayweight+weights[i], bag)
                );
    }


    public static int process2(int[] weights,int[] values, int i,
                               int alreadyweight,int alreadyvalue, int bag){
        if(alreadyweight > bag){
            return 0;
        }
        if(i==values.length){
            return alreadyvalue;
        }

        return Math.max(
                process2(weights,values, i+1, alreadyweight, alreadyvalue, bag),
                process2(weights,values, i+1, alreadyweight+weights[i],
                        alreadyvalue+values[i], bag)
        );
    }



}
