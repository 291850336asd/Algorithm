package com.test;

import java.util.ArrayList;
import java.util.List;

public class MemoryTest {

    public static void main(String[] args) {
        System.out.println(args[0]);
        List<byte[]> aaa = new ArrayList<>();
        int i=1;
        while (i > 0){
            if(i<Integer.parseInt(args[0])){
                i++;
                aaa.add(new byte[10*1024*1024]);
            }
            System.out.println("aa" + aaa.size());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
