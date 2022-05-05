package com.test.link_map_tree;

public class NeedParenttheses {
    public static int needParentheses(String str){
        int count = 0;
        int ans = 0;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) == '('){
                count ++;
            } else {
                if(count == 0){
                    ans ++;
                } else {
                    count --;
                }
            }
        }
        return count + ans;
    }
}
