package com.test.link_map_tree;

public class SmallerEqualBigger {


    public static Node listPartition1(Node head, int pivot){
        if(head == null){
            return head;
        }
        return head;

    }


//    public static Node listPartition2(Node head, int prvot){
//        Node sH = null; // smallHead
//        Node sT = null; // smallTail
//        Node eH = null; // equalHead
//        Node eT = null; // equalTail
//        Node mH = null; // bigHead
//        Node mT = null; // bigTail
//        Node next = null;// save next node
//        while (head != null){
//            next = head.next;
//            head.next = null;
//            if(head.value < prvot){
//                if(sH)
//            }
//        }
//    }

    public static class Node{
        public int value;
        public Node next;
        public Node(int data){
            this.value = data;
        }
    }

}
