package com.test;

public class TestRights {

    public static void main(String[] args) {
        System.out.println(":301".contains(":3"));
//        byte[] rootType = new byte[18];
//        String rootId = "1";
//        extracted(rootType, rootId);
//        System.out.println(rootType[0]);
//        rootId = "2";
//        rootType = new byte[18];
//        extracted(rootType, rootId);
//        System.out.println(rootType[0]);
//        rootId = "3";
//        rootType = new byte[18];
//        extracted(rootType, rootId);
//        System.out.println(rootType[0]);
//        rootId = "4";
//        rootType = new byte[18];
//        extracted(rootType, rootId);
//        System.out.println(rootType[0]);


    }

    private static void extracted(byte[] rootType, String rootId) {
        if("1".equals(rootId)) {
            rootType[0] |= (0x01 << 4);
        }
        //枪管
        if("2".equals(rootId)) {
            rootType[0] |= (0x01 << 5);
        }
        //审批
        if("3".equals(rootId)) {
            rootType[0] |= (0x01 << 6);
        }
        //紧急取枪
        if("4".equals(rootId)) {
            rootType[0] |= (0x01 << 7);
        }
        for (int i = 0; i < 4; i++) {
            rootType[0] |= (0x01 << i);
        }
//        for (int i = 4; i < 42; i++) {
//            int index = ((i - 4) / 8) + 1;
//            int iBitNum = (i - 4) % 8;
//            rootType[index] |= (0x01 << iBitNum);
//        }
    }
}
