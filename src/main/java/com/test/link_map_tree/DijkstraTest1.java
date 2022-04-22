package com.test.link_map_tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DijkstraTest1 {

    public static HashMap<Node, Integer> diskstra1(Node head){
        HashMap<Node, Integer> distnceMap = new HashMap<>();
        distnceMap.put(head, 0);
        HashSet<Node> selectNodes = new HashSet<>();
        Node minNode = getMinDistanceAndUnselectedNode(distnceMap, selectNodes);
        while (minNode != null){
            int distance = distnceMap.get(minNode);
            for (Edge edge: minNode.edges){
                Node toNode= edge.to;
                if(!distnceMap.containsKey(toNode)){
                    distnceMap.put(toNode, distance + edge.weight);
                }
                distnceMap.put(edge.to, Math.min(distnceMap.get(toNode), distance + edge.weight));
            }
            selectNodes.add(minNode);
            minNode = getMinDistanceAndUnselectedNode(distnceMap, selectNodes);
        }
        return distnceMap;
    }


    public static Node getMinDistanceAndUnselectedNode(HashMap<Node,Integer> distanceMap, HashSet<Node> touchedNodes){
        Node minNode = null;
        int minDistance =Integer.MAX_VALUE;
        for (Map.Entry<Node, Integer> entry: distanceMap.entrySet()){
            Node node = entry.getKey();
            int distance = entry.getValue();
            if(!touchedNodes.contains(node) && distance < minDistance){
                minNode = node;
                minDistance = distance;
            }
        }
        return minNode;
    }

    public static class Node{
        public int value;
        public ArrayList<Node> nexts;
        public ArrayList<Edge> edges;
        public Node(int data){
            this.value = data;
        }
    }

    public static class Edge{
        public int weight;
        public Node from;
        public Node to;
    }
}
