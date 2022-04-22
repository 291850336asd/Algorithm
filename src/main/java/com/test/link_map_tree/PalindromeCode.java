package com.test.link_map_tree;

import java.util.Stack;

/**
 * 回文
 */
public class PalindromeCode {

    // need O(1) extra space
    public static boolean isPalinadrome3(Node head){
        if(head == null || head.next == null){
            return true;
        }
        Node n1 = head;
        Node n2 = head;
        while (n2.next != null && n2.next.next != null){ // find mid node
            n1 = n1.next;
            n2 = n2.next.next;
        }
        n2 = n1.next; // n2-> right part first node
        n1.next = null; // mid.next -> null
        Node n3 = null;
        while (n2 != null){ // 右半部分逆序
            n3 = n2.next;
            n2.next = n1;
            n1 = n2; // n1 remove
            n2 = n3; // n2 remove
        }
        n3 = n1;// n3 -> save last node
        n2 = head; // n2 -> left first node
        boolean res = true;
        while (n1 != null && n2 != null){
            if(n1.value != n2.value){
                res =  false;
                break;
            }
            n1 = n1.next; // left to mid
            n2 = n2.next; // right to mid
        }
        n1 = n3.next;
        n3.next = null;
        while (n1 != null){ // recover list
            n2 = n1.next;
            n1.next = n3;
            n3 = n1;
            n1 = n2;
        }
        return res;
    }


    // need n/2 extra space
    public static boolean isPalindrome2(Node head){
        if(head == null || head.next == null){
            return true;
        }
        Node right = head.next;
        Node cur = head;
        while (cur.next != null && cur.next.next != null){
            right = right.next;
            cur = cur.next.next;
        }
        Stack<Node> stack = new Stack<>();
        while (right!= null){
            stack.push(right);
            right = right.next;
        }
        while (!stack.isEmpty()){
            if(head.value != stack.pop().value){
                return false;
            }
            head = head.next;
        }
        return true;


    }



    // need n extra space
    public static boolean isPalindrome1(Node head){
        Stack<Node> stack = new Stack<>();
        Node cur = head;
        while (cur != null){
            stack.push(cur);
            cur = cur.next;
        }
        while (head != null){
            if(head.value != stack.pop().value){
                return false;
            }
            head = head.next;
        }
        return true;
    }



    public static class Node{
        public int value;
        public Node next;
        public Node(int data){
            this.value = data;
        }
    }

}
