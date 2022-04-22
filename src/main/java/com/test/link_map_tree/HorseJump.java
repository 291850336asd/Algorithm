package com.test.link_map_tree;

public class HorseJump {

    public static int process(int x, int y ,int step){
        if(x<0 || x > 8 || y<0 || y>9){
            return 0;
        }
        if(step == 0){
            return (x==0 && y==0) ? 1: 0;
        }
        return process(x-1, y+2, step-1) +
                process(x+1, y+2, step-1) +
                process(x+2, y+1, step-1) +
                process(x+2, y-2, step-1) +
                process(x+1, y-2, step-1) +
                process(x+1, y-2, step-1) +
                process(x-2, y-1, step-1) +
                process(x-2, y-1, step-1);
    }

}
