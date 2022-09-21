package com.test.socketdeal;

public class DeviceManageUtils {

    public static String DeviceGiveMeInfo(String deviceInfo){
        //1.命名获取
        String msg = getDeviceMsgFromUser(deviceInfo);

        //2.算法转换  生成设备可识别的命令
        String getDeviceRegiCmd = getDeivceKonwCMd(msg);
        //3.获取温湿度
        String getUserKnowInfo = getUserKnowCInfo(getDeviceRegiCmd);
        return getUserKnowInfo;
    }

    private static String getDeviceMsgFromUser(String deviceInfo) {
        return DeviceInfo.ConstantsCmd.cmd_11;
    }

    private static String getUserKnowCInfo(String getDeviceRegiCmd) {
        return null;
    }

    private static String getDeivceKonwCMd(String msg) {
        return null;
    }


}
