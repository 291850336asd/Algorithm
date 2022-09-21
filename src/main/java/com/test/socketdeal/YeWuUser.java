package com.test.socketdeal;

import java.util.ArrayList;
import java.util.List;

public class YeWuUser {

    public void getTempOrHum(String deviceInfo){
        UserUtils.getTempOrHum(deviceInfo);
    }

    public void stillGetTempOrHUm(){
        //1获取所有串口数据
        List<String> allDeviceINfos = new ArrayList<>();
        //2.循环更新设备数据信息
        for (int i = 0; i < allDeviceINfos.size(); i++) {
            UserUtils.getTempOrHum(allDeviceINfos.get(i));
        }
    }

}
