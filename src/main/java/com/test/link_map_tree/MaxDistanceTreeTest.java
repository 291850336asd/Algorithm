package com.test.link_map_tree;

public class MaxDistanceTreeTest {


    public static Info process(Node x){
        if(x == null){
            return new Info(0, 0);
        }
        Info leftInfo = process(x.left);
        Info right = process(x.right);
        int p1 = leftInfo.maxDistance;
        int p2= right.maxDistance;
        int p3 = leftInfo.height + 1 + right.height;
        int maxDistance = Math.max(p3, Math.max(p1, p2));
        int height = Math.max(leftInfo.height, right.height) +1;
        return new Info(maxDistance, height);
    }



    public static class Node{
        public int value;
        public Node left;
        public Node right;
        public Node(int data){
            this.value = data;
        }
    }
    public static int maxDistance(Node head){
        return process(head).maxDistance;
    }
    public static class Info{
        public int maxDistance;
        public int height;
        public Info(int dis, int h){
            maxDistance = dis;
            height = h;
        }
    }
}
