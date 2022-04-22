package com.test.link_map_tree;

public class MorrisTravesalTest {

    //搜索二叉树
    public static boolean isBST(Node head){
        if(head == null){
            return true;
        }
        Node cur = head;
        Node mostRight = null;
        int preValue = Integer.MIN_VALUE;
        while (cur != null){
            mostRight = cur.left;
            if(mostRight != null){
                while (mostRight.right != null && mostRight.right != cur){
                    mostRight = mostRight.right;
                }
                if(mostRight.right == null){
                    mostRight.right = cur;
                    cur = cur.left;
                    continue;
                } else {
                    mostRight.right = null;
                }
            }
            if(cur.value <= preValue){
                return  false;
            }
            preValue = cur.value;
            cur = cur.right;
        }
        return true;
    }

    //morris遍历、线索二叉树
    public static void morris(Node head){
        if(head == null){
            return;
        }
        Node cur = head;
        Node mostRight = null;
        while (cur != null){
            mostRight = cur.left;
            if(mostRight != null){
                while (mostRight.right != null && mostRight.right != cur){
                    mostRight = mostRight.right;
                }
                if(mostRight.right == null){
                    mostRight.right = cur;
                    cur = cur.left;
                    continue;
                } else {
                    mostRight.right = null;
                }
            }
            cur = cur.right;
        }
    }

    /**
     * 先序打印
     * @param head
     */
    public static void morrisPre(Node head){
        if(head == null){
            return;
        }
        Node cur = head;
        Node mostRight = null;
        while (cur != null){
            mostRight = cur.left;
            if(mostRight != null){
                while (mostRight.right != null && mostRight.right != cur){
                    mostRight = mostRight.right;
                }
                if(mostRight.right == null){
                    System.out.println(cur.value);
                    mostRight.right = cur;
                    cur = cur.left;
                    continue;
                } else {
                    mostRight.right = null;
                }
            } else {
                System.out.println(cur.value);
            }
            cur = cur.right;
        }
    }

    /**
     * 中序打印
     * @param head
     */
    public static void morrisMid(Node head){
        if(head == null){
            return;
        }
        Node cur = head;
        Node mostRight = null;
        while (cur != null){
            mostRight = cur.left;
            if(mostRight != null){
                while (mostRight.right != null && mostRight.right != cur){
                    mostRight = mostRight.right;
                }
                if(mostRight.right == null){
                    mostRight.right = cur;
                    cur = cur.left;
                    continue;
                } else {
                    mostRight.right = null;
                }
            }
            System.out.println(cur.value); //中
            cur = cur.right;
        }
    }

    /**
     * 后序打印
     * @param head
     */
    public static void morrisAfter(Node head){
        if(head == null){
            return;
        }
        Node cur = head;
        Node mostRight = null;
        while (cur != null){
            mostRight = cur.left;
            if(mostRight != null){
                while (mostRight.right != null && mostRight.right != cur){
                    mostRight = mostRight.right;
                }
                if(mostRight.right == null){
                    mostRight.right = cur;
                    cur = cur.left;
                    continue;
                } else {
                    mostRight.right = null;
                    printEdge(cur.left);
                }
            }
            cur = cur.right;
        }
        printEdge(head);
    }

    public static void printEdge(Node x){
        Node tail = reverseEdge(x);
        Node cur = tail;
        while (cur != null){
            System.out.println(cur.value + "");
            cur = cur.right;
        }
        reverseEdge(tail);
    }

    public static Node reverseEdge(Node from){
        Node pre = null;
        Node next = null;
        while (from != null){
            next = from.right;
            from.right = pre;
            pre = from;
            from = next;
        }
        return pre;
    }






    public static void process(Node head){
        if(head == null){
            return;
        }
        //1.先序
        process(head.left);
        //2.中序
        process((head.right));
        //3.后序
    }

    public static class Node{
        public int value;
        Node left;
        Node right;
        public Node(int data){
            this.value = data;
        }
    }
}
