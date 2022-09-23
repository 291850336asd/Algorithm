package com.test.socketdeal;

public class DeviceInfo1 extends DeviceInfoBase{

    public static String host = "192.168.5.226";
    public static int port = 10001;

    static public class ConstantsCmd {

        public static final String cmd_11= "11 04 0000 0002";
    }

    @Override
    protected String sendToDevice(byte[] deviceBytes) {
        return ""+2;
    }

    @Override
    protected byte[] getDeviceSengMsg(String cmd_11) {
        return new byte[]{3,4,5};
    }


}
