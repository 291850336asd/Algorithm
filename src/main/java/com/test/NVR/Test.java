package com.test.NVR;

import com.alibaba.fastjson.JSONObject;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.NativeLong;
import java.io.File;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Test {

    static HCNetSDK hCNetSDK = (HCNetSDK) Native.loadLibrary("C:\\Users\\sunme\\Downloads\\CH-HCNetSDKV6.1.9.4_build20220412_win32_20220419185243\\CH-HCNetSDKV6.1.9.4_build20220412_win32\\库文件\\HCNetSDK.dll", HCNetSDK.class);

    public static void main(String[] args) {


        int  lUserID = 1;

        boolean b = hCNetSDK.NET_DVR_Init();
        System.out.println(b);
        if (lUserID > -1) {
            //先注销
            hCNetSDK.NET_DVR_Logout_V30(lUserID);
            lUserID = -1;
        }
        HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        int iPort = 8000;
        lUserID = hCNetSDK.NET_DVR_Login_V30("192.168.5.20",
                (short) iPort, "admin", "1q2w3e4r", m_strDeviceInfo);

        int userID = lUserID;
        if (userID == -1) {
//            m_sDeviceIP = "";//登录未成功,IP置为空
            int error;
            error=hCNetSDK.NET_DVR_GetLastError();
            System.out.println(error);
        } else {
//            CreateDeviceTree();
        }



        IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
        boolean bRet = false;
        int iChannelNum = -1;

        HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        m_strIpparaCfg.write();
        Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
        bRet = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, 0, lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
        m_strIpparaCfg.read();

        List<String> devices = new ArrayList<>();
        if (!bRet){
            //设备不支持,则表示没有IP通道
            for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++){
                devices.add("Camera" + (iChannum + m_strDeviceInfo.byStartChan));
            }
        }else{
            for(int iChannum =0; iChannum < HCNetSDK.MAX_IP_CHANNEL; iChannum++) {
                if (m_strIpparaCfg.struIPChanInfo[iChannum].byEnable == 1) {
                    devices.add("IPCamera" + (iChannum + m_strDeviceInfo.byStartChan));
                }
            }
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 5,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            try {
                Thread.sleep(10000);
                threadPoolExecutor = new ThreadPoolExecutor(0, 5,
                        10L, TimeUnit.SECONDS,
                        new SynchronousQueue<Runnable>(),
                        new ThreadPoolExecutor.DiscardOldestPolicy());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadPoolExecutor.execute(new Thread(()->{
                initStream((int) userID, finalI);
            }));
        }

    }

    private static void initStream(int lUserID, int finalI) {
        int m_lLoadHandle = 0;
        int m_iChanShowNum = 38;  // 32 + 6; IPCamara开头表示IP通道
        //子字符创中获取通道号,IP通道号要加32
        //Camara开头表示模拟通道 不需要加32
        HCNetSDK.NET_DVR_TIME struStartTime;
        HCNetSDK.NET_DVR_TIME struStopTime;
        struStartTime = new HCNetSDK.NET_DVR_TIME();
        struStopTime = new HCNetSDK.NET_DVR_TIME();
        struStartTime.dwYear =  2022;
        struStartTime.dwMonth = 6;
        struStartTime.dwDay = 24;
        struStartTime.dwHour = 12;
        struStartTime.dwMinute = 12;
        struStartTime.dwSecond = 12;
        struStopTime.dwYear = 2022;
        struStopTime.dwMonth = 6;
        struStopTime.dwDay = 24;
        struStopTime.dwHour = 13;
        struStopTime.dwMinute = 12;
        struStopTime.dwSecond = 12;


//        int lFindHandle = hCNetSDK.NET_DVR_FindFile(lUserID,
//                m_iChanShowNum, 0, struStartTime, struStopTime);
//        if (lFindHandle < 0) {
//            int errorcode = hCNetSDK.NET_DVR_GetLastError();
//            System.out.println("hcsdk 按时间查找录像文件失败,错误码:" + errorcode);
//            hCNetSDK.NET_DVR_FindClose(lFindHandle);
//            return;
//        }
//        // 文件查找结果信息结构体。
//        HCNetSDK.NET_DVR_FINDDATA_V40 lpFindData = new HCNetSDK.NET_DVR_FINDDATA_V40();
//        int lFindNextFile_V40;
//        JSONObject hisList = new JSONObject(true);
//        // 文件列表序号
//        int videoindex = 1;
//        while (true) {
//            // 逐个获取查找到的文件信息
//            lFindNextFile_V40 = hCNetSDK.NET_DVR_FindNextFile_V40(lFindHandle, lpFindData);
//            // 正在查找请等待
//            if (lFindNextFile_V40 == 1002) {
//                continue;
//            }
//            // 获取文件信息成功
//            if (lFindNextFile_V40 == 1000) {
//                Map<String, Object> video = new LinkedHashMap<>();
//                //NET_DVR_FINDDATA_V40结构体中可以得到文件名、文件开始时间、文件结束时间、文件大小、文件类型等信息，根据需要返回。
//                video.put("starttime", lpFindData.struStartTime);
//                video.put("endtime", lpFindData.struStopTime);
//                hisList.put(String.valueOf(videoindex), video);
//                videoindex++;
//                continue;
//            }
//            if (lFindNextFile_V40 == 1003) {
//                System.out.println("hcsdk 没有更多的文件，查找结束");
//                break;
//            }
//        }
//        // 结束查找 释放资源
//        hCNetSDK.NET_DVR_FindClose(lFindHandle);
//
        String sFileName = "D:/ffmpeg-master-latest-win64-gpl/ffmpeg-master-latest-win64-gpl/bin/" + 192 + m_iChanShowNum + struStartTime.toStringTitle() + struStopTime.toStringTitle() +finalI+ ".mp4";
        if(!new File("D:/ffmpeg-master-latest-win64-gpl/ffmpeg-master-latest-win64-gpl/bin/").exists()){
            new File("D:/ffmpeg-master-latest-win64-gpl/ffmpeg-master-latest-win64-gpl/bin/").mkdirs();
        }
        m_lLoadHandle = hCNetSDK.NET_DVR_GetFileByTime(lUserID, m_iChanShowNum, struStartTime, struStopTime, sFileName);
        System.out.println("m_lLoadHandle: " + m_lLoadHandle  + "  hCNetSDK.NET_DVR_GetLastError: " + hCNetSDK.NET_DVR_GetLastError());
        if (m_lLoadHandle >= 0)
        {
            hCNetSDK.NET_DVR_PlayBackControl(m_lLoadHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
            Timer Downloadtimer = new Timer();//新建定时器
            Downloadtimer.schedule(new DownloadTask(m_lLoadHandle), 0, 3000);//0秒后开始响应函数
        }
    }

    static class DownloadTask extends java.util.TimerTask
    {
        int m_lLoadHandle = -1;
        public DownloadTask(int m_lLoadHandle){
            this.m_lLoadHandle = m_lLoadHandle;
        }

        //定时器函数
        @Override
        public void run()
        {
            IntByReference nPos = new IntByReference(0);
            hCNetSDK.NET_DVR_PlayBackControl(m_lLoadHandle, HCNetSDK.NET_DVR_PLAYGETPOS, 4, nPos);
            if (nPos.getValue() > 100)
            {
                hCNetSDK.NET_DVR_StopGetFile(m_lLoadHandle);
                m_lLoadHandle=-1;
                cancel();
            }
            if (nPos.getValue() == 100)
            {
                hCNetSDK.NET_DVR_StopGetFile(m_lLoadHandle);
                m_lLoadHandle=-1;
                cancel();
            }
        }
    }
}
