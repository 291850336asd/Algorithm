package com.test.link_map_tree;

import java.util.ArrayList;
import java.util.List;

public class PrintAllSubSreTest {

    public static void main(String[] args) {
        char[] chs = "abc".toCharArray();
        process(chs, 0, new ArrayList<Character>());
    }

    private static void process(char[] str, int i, List<Character> res) {
        if(i == str.length){
            System.out.println(res);
            return;
        }
        List<Character> resKeep = copyList(res);
        resKeep.add(str[i]);
        process(str, i+1, resKeep);
        List<Character> resNoInclude = copyList(res);
        process(str, i+1, resNoInclude);

    }

    private static List<Character> copyList(List<Character> res) {
        return new ArrayList<>(res);
    }


}
