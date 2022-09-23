package com.test.socketdeal;

public abstract class DeviceInfoBase {

    public static String host = "192.168.5.226";
    public static int port = 10001;

    static public class ConstantsCmd {

        public static final String cmd_11= "11 04 0000 0002";
    }

    private String convertInfo(){
        byte[] deviceBytes = getDeviceSengMsg(DeviceInfo2.ConstantsCmd.cmd_11);
        String deviceReturnRawINfo = sendToDevice(deviceBytes);
        return deviceReturnRawINfo;
    }

    protected abstract String sendToDevice(byte[] deviceBytes);

    protected abstract byte[] getDeviceSengMsg(String cmd_11);


}
