package com.test.link_map_tree;

import java.util.List;

public class EmployeeHappyTreeTest {

    public static int maxHappy(Employee boss){
        Info headInfo = process(boss);
        return Math.max(headInfo.laiMaxHappy, headInfo.buMaxHappy);
    }

    public static Info process(Employee x){
        if(x.nexts.isEmpty()){
            return new Info(x.happy, 0);
        }
        int lai = x.happy;
        int bu = 0;
        for(Employee next: x.nexts){
            Info nextInfo = process(next);
            lai += nextInfo.buMaxHappy;
            bu+= Math.max(nextInfo.laiMaxHappy, nextInfo.buMaxHappy);
        }
        return new Info(lai, bu);
    }



    static class Info{
        public int laiMaxHappy;
        public int buMaxHappy;
        public Info(int lai, int bu){
            laiMaxHappy = lai;
            buMaxHappy = bu;
        }
    }
    static class Employee{
        public int happy;
        List<Employee> nexts;
    }
}
