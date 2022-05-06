package com.test.link_map_tree;

public class MaxSumInTree {

    public static int maxSum = Integer.MIN_VALUE;

    public static int maxPath(Node head){
        p(head, 0);
        return maxSum;
    }

    public static void p(Node x, int pre){
        if(x.left == null && x.right == null){
            maxSum = Math.max(maxSum, x.value + pre);
            return;
        }
        if(x.left != null){
            p(x.left, pre + x.value);
        }
        if(x.right != null){
            p(x.right, pre + x.value);
        }
    }


    public static int process2(Node x){
        if(x.left == null && x.right == null){
            return x.value;
        }
        int next = Integer.MIN_VALUE;
        if(x.left != null){
            next = process2(x.left);
        }
        if(x.right != null){
            next = Math.max(next, process2(x.right));
        }
        return x.value + next;
    }




    public static class Node{
        public int value;
        public Node left;
        public Node right;
        public Node(int val){
            value = val;
        }

    }

}
