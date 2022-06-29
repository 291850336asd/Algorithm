///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
///*
// * ClientDemo.java
// *
// * Created on 2009-9-14, 19:31:34
// */
///**
// * @author Xubinfeng
// */
//
//package com.test.NVR;
//
//import com.test.NVR.osSelect;
//import com.sun.jna.Native;
//import com.sun.jna.NativeLong;
//import com.sun.jna.Pointer;
//import com.sun.jna.examples.win32.W32API.HWND;
//import com.sun.jna.ptr.ByteByReference;
//import com.sun.jna.ptr.IntByReference;
//import com.sun.jna.ptr.NativeLongByReference;
//
//import javax.swing.tree.DefaultMutableTreeNode;
//import java.util.Date;
//
///*****************************************************************************
// * 主类 ：ClientDemo
// * 用途 ：用户注册，预览，参数配置菜单
// * 容器：Jframe
// ****************************************************************************/
//public class ClientDemo {
//    /*************************************************
//     * 函数:      主类构造函数
//     * 函数描述:	初始化成员
//     *************************************************/
//
//    public ClientDemo() {
//        int lUserID = -1;
//        int lPreviewHandle =-1;
//        int lAlarmHandle = -1;
//        int lListenHandle = -1;
//        int g_lVoiceHandle = -1;
//        m_lPort= new IntByReference(-1);
//        fMSFCallBack = null;
//        fRealDataCallBack = new FRealDataCallBack();
//        fExceptionCallBack = new FExceptionCallBack_Imp();
//        m_iTreeNodeNum = 0;
//    }
//
//    static HCNetSDK hCNetSDK = null;
//    static PlayCtrl playControl = null;
//
//    public static int g_lVoiceHandle;//全局的语音对讲句柄
//
//    static HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo;//设备信息
//    static HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg;//IP参数
//    static HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo;//用户参数
//
//    boolean bRealPlay;//是否在预览.
//    String m_sDeviceIP;//已登录设备的IP地址
//
//    int  lUserID;//用户句柄
//    int  lPreviewHandle;//预览句柄
//    IntByReference m_lPort;//回调预览时播放库端口指针
//
//    int  lAlarmHandle;//报警布防句柄
//    int lListenHandle;//报警监听句柄
//
//    static FMSGCallBack fMSFCallBack;//报警回调函数实现
//    static FRealDataCallBack fRealDataCallBack;//预览回调函数实现
//    static FExceptionCallBack_Imp fExceptionCallBack;
//
//
//    int m_iTreeNodeNum;//通道树节点数目
//
//    /*************************************************
//     * 函数:      "注册"  按钮单击响应函数
//     * 函数描述:	注册登录设备
//     *************************************************/
//    private void jButtonLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoginActionPerformed
//
//        if (lUserID > -1) {
//            //先注销
//            hCNetSDK.NET_DVR_Logout_V30(lUserID);
//            lUserID = -1;
//            m_iTreeNodeNum = 0;
//        }
//        //注册
//        m_sDeviceIP = jTextFieldIPAddress.getText();//设备ip地址
//        m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
//        int iPort = Integer.parseInt(jTextFieldPortNumber.getText());
//        lUserID = hCNetSDK.NET_DVR_Login_V30(m_sDeviceIP,
//                (short) iPort, jTextFieldUserName.getText(), new String(jPasswordFieldPassword.getPassword()), m_strDeviceInfo);
//
//        long userID = lUserID;
//        if (userID == -1) {
//            m_sDeviceIP = "";//登录未成功,IP置为空
//            int error;
//            error=hCNetSDK.NET_DVR_GetLastError();
//
//        } else {
//            CreateDeviceTree();
//        }
//    }//GEN-LAST:event_jButtonLoginActionPerformed
//
//    /*************************************************
//     * 函数:      initialTableModel
//     * 函数描述:	初始化报警信息列表,写入列名称
//     *************************************************/
//    public DefaultTableModel initialTableModel() {
//        String tabeTile[];
//        tabeTile = new String[]{"时间", "报警信息", "设备信息"};
//        DefaultTableModel alarmTableModel = new DefaultTableModel(tabeTile, 0);
//        return alarmTableModel;
//    }
//
//    /*************************************************
//     * 函数:      initialTreeModel
//     * 函数描述:  初始化设备树
//     *************************************************/
//    private DefaultTreeModel initialTreeModel() {
//        m_DeviceRoot = new DefaultMutableTreeNode("Device");
//        DefaultTreeModel myDefaultTreeModel = new DefaultTreeModel(m_DeviceRoot);//使用根节点创建模型
//        return myDefaultTreeModel;
//    }
//
//    /*************************************************
//     * 函数:      " 退出"  按钮响应函数
//     * 函数描述:	注销并退出
//     *************************************************/
//    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
//        //如果在预览,先停止预览, 释放句柄
//        if (lPreviewHandle > -1) {
//            hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle);
//            if (framePTZControl != null) {
//                framePTZControl.dispose();
//            }
//        }
//
//        //报警撤防
//        if (lAlarmHandle != -1) {
//            hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle);
//        }
//        //停止监听
//        if (lListenHandle != -1) {
//            hCNetSDK.NET_DVR_StopListen_V30(lListenHandle);
//        }
//
//        //如果已经注册,注销
//        if (lUserID > -1) {
//            hCNetSDK.NET_DVR_Logout_V30(lUserID);
//        }
//        //cleanup SDK
//        hCNetSDK.NET_DVR_Cleanup();
//        this.dispose();
//    }//GEN-LAST:event_jButtonExitActionPerformed
//
//    /*************************************************
//     * 函数:       "清空报警信息"  菜单项响应函数
//     * 函数描述:	单击清空信息列表
//     *************************************************/
//    private void jMenuItemRemoveAlarmMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemRemoveAlarmMousePressed
//    {//GEN-HEADEREND:event_jMenuItemRemoveAlarmMousePressed
//        //删除所有行
//        ((DefaultTableModel) jTableAlarm.getModel()).getDataVector().removeAllElements();
//        //把改变显示到列表控件
//        ((DefaultTableModel) jTableAlarm.getModel()).fireTableStructureChanged();
//    }//GEN-LAST:event_jMenuItemRemoveAlarmMousePressed
//
//    /*************************************************
//     * 函数:      "报警监听"  菜单项响应函数
//     * 函数描述:   选中开始监听,取消结束监听
//     *************************************************/
//    private void jRadioButtonMenuListenActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonMenuListenActionPerformed
//    {//GEN-HEADEREND:event_jRadioButtonMenuListenActionPerformed
//        if (jRadioButtonMenuListen.isSelected() == true)//选择监听
//        {
//            if (lListenHandle == -1)
//            //尚未监听,开始监听
//            {
//                if (fMSFCallBack == null) {
//                    fMSFCallBack = new FMSGCallBack();
//                }
//                Pointer pUser = null;
//                if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack, pUser)) {
//                    System.out.println("设置回调函数失败!");
//                }
//
//                //本地IP地址置为null时自动获取本地IP
//                lListenHandle = hCNetSDK.NET_DVR_StartListen_V30(null, (short) 7200, fMSFCallBack, null);
//                if (lListenHandle == -1) {
//                    JOptionPane.showMessageDialog(this, "开始监听失败");
//                    jRadioButtonMenuListen.setSelected(false);
//                }
//            }
//        } else
//        //停止监听
//        {
//            if (lListenHandle != -1) {
//                if (!hCNetSDK.NET_DVR_StopListen_V30(lListenHandle)) {
//                    JOptionPane.showMessageDialog(this, "停止监听失败");
//                    jRadioButtonMenuListen.setSelected(true);
//                    lListenHandle = -1;
//                } else {
//                    lListenHandle = -1;
//                }
//            }
//        }
//    }//GEN-LAST:event_jRadioButtonMenuListenActionPerformed
//
//    /*************************************************
//     * 函数:      "报警布防"  菜单项响应函数
//     * 函数描述:	选中布防听,取消撤防
//     *************************************************/
//    private void jRadioButtonMenuSetAlarmActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonMenuSetAlarmActionPerformed
//    {//GEN-HEADEREND:event_jRadioButtonMenuSetAlarmActionPerformed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//
//        if (jRadioButtonMenuSetAlarm.isSelected() == true)
//        //已选择布防
//        {
//            if (lAlarmHandle == -1)//尚未布防,需要布防
//            {
//                if (fMSFCallBack == null) {
//                    fMSFCallBack = new FMSGCallBack();
//                    Pointer pUser = null;
//                    if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack, pUser)) {
//                        System.out.println("设置回调函数失败!");
//                    }
//                }
//                lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V30(lUserID);
//                if (lAlarmHandle == -1) {
//                    JOptionPane.showMessageDialog(this, "布防失败");
//                    jRadioButtonMenuSetAlarm.setSelected(false);
//                }
//            }
//        } else
//        //未选择布防
//        {
//            if (lAlarmHandle != -1) {
//                if (!hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle)) {
//                    JOptionPane.showMessageDialog(this, "撤防失败");
//                    jRadioButtonMenuSetAlarm.setSelected(true);
//                    lAlarmHandle = -1;
//                } else {
//                    lAlarmHandle = -1;
//                }
//            }
//        }
//    }//GEN-LAST:event_jRadioButtonMenuSetAlarmActionPerformed
//
//    /*************************************************
//     * 函数:      "预览"  按钮单击响应函数
//     * 函数描述:	获取通道号,打开播放窗口,开始此通道的预览
//     *************************************************/
//    private void jButtonRealPlayActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonRealPlayActionPerformed
//    {//GEN-HEADEREND:event_jButtonRealPlayActionPerformed
////        System.out.println(panelRealplay.getWidth());
////        System.out.println(panelRealplay.getHeight());
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//
//        //如果预览窗口没打开,不在预览
//        if (bRealPlay == false) {
//            //获取窗口句柄
//            HWND hwnd = new HWND(Native.getComponentPointer(panelRealplay));
//
//            //获取通道号
//            int iChannelNum = getChannelNumber();//通道号
//            if (iChannelNum == -1) {
//                JOptionPane.showMessageDialog(this, "请选择要预览的通道");
//                return;
//            }
//
////            m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
////            m_strClientInfo.lChannel = new NativeLong(iChannelNum);
//            HCNetSDK.NET_DVR_PREVIEWINFO strClientInfo = new HCNetSDK.NET_DVR_PREVIEWINFO();
//            strClientInfo.read();
////            strClientInfo.hPlayWnd = null;  //窗口句柄，从回调取流不显示一般设置为空
//            strClientInfo.lChannel = iChannelNum;  //通道号
//            strClientInfo.dwStreamType=0; //0-主码流，1-子码流，2-三码流，3-虚拟码流，以此类推
//            strClientInfo.dwLinkMode=0; //连接方式：0- TCP方式，1- UDP方式，2- 多播方式，3- RTP方式，4- RTP/RTSP，5- RTP/HTTP，6- HRUDP（可靠传输） ，7- RTSP/HTTPS，8- NPQ
//            strClientInfo.bBlocked=1;  //0- 非阻塞取流，1- 阻塞取流
//
//            //在此判断是否回调预览,0,不回调 1 回调
//            if (jComboBoxCallback.getSelectedIndex() == 0) {
//                strClientInfo.hPlayWnd = hwnd;
//                strClientInfo.write();
//                lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V40(lUserID, strClientInfo, null , null);
//                if (lPreviewHandle<=-1)
//                {
//                    int error;
//                    error=hCNetSDK.NET_DVR_GetLastError();
//                    JOptionPane.showMessageDialog(ClientDemo.this, "预览失败,错误码："+error);
//                    return;
//
//                }
//            } else if (jComboBoxCallback.getSelectedIndex() == 1) {
//                strClientInfo.hPlayWnd = null;
//                strClientInfo.write();
//                lPreviewHandle = hCNetSDK.NET_DVR_RealPlay_V40(lUserID,
//                        strClientInfo, fRealDataCallBack, null);
//                if (lPreviewHandle<=-1)
//                {
//                    int error;
//                    error=hCNetSDK.NET_DVR_GetLastError();
//                    JOptionPane.showMessageDialog(ClientDemo.this, "预览失败,错误码："+error);
//                    return;
//                }
//            }
//
//            long previewSucValue = lPreviewHandle;
//
//            //预览失败时:
//            if (previewSucValue == -1) {
//                int error;
//                error=hCNetSDK.NET_DVR_GetLastError();
//                JOptionPane.showMessageDialog(this, "预览失败,错误码："+error);
//                return;
//            }
//
//            //预览成功的操作
//            jButtonRealPlay.setText("停止");
//            bRealPlay = true;
//
//            //显示云台控制窗口
//            framePTZControl = new JFramePTZControl(lPreviewHandle);
//            framePTZControl.setLocation(this.getX() + this.getWidth(), this.getY());
//            framePTZControl.setVisible(true);
//        }
//
//        //如果在预览,停止预览,关闭窗口
//        else {
//            hCNetSDK.NET_DVR_StopRealPlay(lPreviewHandle);
//            jButtonRealPlay.setText("预览");
//            bRealPlay = false;
//            if (m_lPort.getValue() != -1) {
//                playControl.PlayM4_Stop(m_lPort.getValue());
//                m_lPort.setValue(-1);
//            }
//            framePTZControl.dispose();
//
//            panelRealplay.repaint();
//        }
//    }//GEN-LAST:event_jButtonRealPlayActionPerformed
//
//    /*************************************************
//     * 函数:       "串口"  菜单项响应函数
//     * 函数描述:	新建窗体显示串口
//     *************************************************/
//    private void jMenuItemSerialCfgMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemSerialCfgMousePressed
//    {//GEN-HEADEREND:event_jMenuItemSerialCfgMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        JDialogSerialCfg dlgSerialCfg = new JDialogSerialCfg(this, false, lUserID, m_strDeviceInfo);
//        centerWindow(dlgSerialCfg);
//        dlgSerialCfg.setVisible(true);
//    }//GEN-LAST:event_jMenuItemSerialCfgMousePressed
//
//    /*************************************************
//     * 函数:       "报警参数"  菜单项响应函数
//     * 函数描述:	新建窗体显示报警参数
//     *************************************************/
//    private void jMenuItemAlarmCfgMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemAlarmCfgMousePressed
//    {//GEN-HEADEREND:event_jMenuItemAlarmCfgMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        JDialogAlarmCfg dlgAlarmCfg = new JDialogAlarmCfg(this, false, lUserID, m_strDeviceInfo);
//        dlgAlarmCfg.setLocation(this.getX(), this.getY());
//        dlgAlarmCfg.setVisible(true);
//    }//GEN-LAST:event_jMenuItemAlarmCfgMousePressed
//
//    /*************************************************
//     * 函数:  "通道配置"  菜单项响应函数
//     * 函数描述:点击显示通道参数配置
//     *************************************************/
//    private void jMenuItemChannelMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemChannelMousePressed
//    {//GEN-HEADEREND:event_jMenuItemChannelMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        JDialogChannelConfig dialogChannelConfig = new JDialogChannelConfig(this, false, lUserID, m_strDeviceInfo);//无模式对话框
//        dialogChannelConfig.setBounds(this.getX(), this.getY(), 670, 640);
//        dialogChannelConfig.setVisible(true);
//        //传参数
//        int iStartChan = m_strDeviceInfo.byStartChan;
//        int iChannum = m_strDeviceInfo.byChanNum;
//        //初始化通道数组合框
//        for (int i = 0; i < iChannum; i++) {
//            dialogChannelConfig.jComboBoxChannelNumber.addItem("Camera" + (i + iStartChan));
//        }
//        for (int i = 0; i < HCNetSDK.MAX_IP_CHANNEL; i++) {
//            if (m_strIpparaCfg.struIPChanInfo[i].byEnable == 1) {
//                dialogChannelConfig.jComboBoxChannelNumber.addItem("IPCamara" + (i + iStartChan));
//            }
//        }
//    }//GEN-LAST:event_jMenuItemChannelMousePressed
//
//    /*************************************************
//     * 函数:      "网络参数"  菜单项响应函数
//     * 函数描述:	点击显示网络参数配置
//     *************************************************/
//    private void jMenuItemNetworkMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemNetworkMousePressed
//    {//GEN-HEADEREND:event_jMenuItemNetworkMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开Jframe
//        JFrameNetWorkConfig frameNetwork = new JFrameNetWorkConfig(lUserID);
//        frameNetwork.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frameNetwork.setSize(550, 380);
//        centerWindow(frameNetwork);
//        frameNetwork.setVisible(true);
//    }//GEN-LAST:event_jMenuItemNetworkMousePressed
//
//    /*************************************************
//     * 函数:       "基本信息"  菜单项响应函数
//     * 函数描述:	新建窗体,显示设备基本信息
//     *************************************************/
//    private void jMenuItemBasicConfigMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemBasicConfigMousePressed
//    {//GEN-HEADEREND:event_jMenuItemBasicConfigMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        JDialogBasicConfig dlgBasicConfig = new JDialogBasicConfig(this, false, lUserID);
//        dlgBasicConfig.setSize(507, 400);
//        centerWindow(dlgBasicConfig);
//        dlgBasicConfig.setVisible(true);
//    }//GEN-LAST:event_jMenuItemBasicConfigMousePressed
//
//    /*************************************************
//     * 函数:       "设备状态"  菜单项响应函数
//     * 函数描述:	新建窗体显示设备状态
//     *************************************************/
//    private void jMenuItemDeviceStateMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemDeviceStateMousePressed
//    {//GEN-HEADEREND:event_jMenuItemDeviceStateMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        JDialogDeviceState dlgDeviceState = new JDialogDeviceState(this, false, lUserID, m_strDeviceInfo, m_sDeviceIP);
//        dlgDeviceState.setSize(680, 715);
//        centerWindow(dlgDeviceState);
//        dlgDeviceState.setVisible(true);
//    }//GEN-LAST:event_jMenuItemDeviceStateMousePressed
//
//    /*************************************************
//     * 函数:       "恢复默认参数"  菜单项响应函数
//     * 函数描述:	弹出确认框,是否恢复默认参数
//     *************************************************/
//    private void jMenuItemDefaultMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemDefaultMousePressed
//    {//GEN-HEADEREND:event_jMenuItemDefaultMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//
//        int iResponse = JOptionPane.showConfirmDialog(this, "确定恢复默认参数?", "恢复默认参数", JOptionPane.OK_CANCEL_OPTION);
//        if (iResponse == 0)
//        //确认
//        {
//            if (!hCNetSDK.NET_DVR_RestoreConfig(lUserID)) {
//                JOptionPane.showMessageDialog(this, "恢复默认参数失败");
//                return;
//            }
//        }
//        if (iResponse == 2)
//        //取消
//        {
//            return;
//        }
//    }//GEN-LAST:event_jMenuItemDefaultMousePressed
//
//    /*************************************************
//     * 函数:       "关闭"  菜单项响应函数
//     * 函数描述:	弹出确认框询问是否关机
//     *************************************************/
//    private void jMenuItemShutDownMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemShutDownMousePressed
//    {//GEN-HEADEREND:event_jMenuItemShutDownMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//
//        int iResponse = JOptionPane.showConfirmDialog(this, "确定关闭设备?", "关机", JOptionPane.OK_CANCEL_OPTION);
//        //确认
//        if (iResponse == 0) {
//            if (!hCNetSDK.NET_DVR_ShutDownDVR(lUserID)) {
//                JOptionPane.showMessageDialog(this, "关闭设备失败");
//                return;
//            }
//        }
//        //取消
//        if (iResponse == 2) {
//            return;
//        }
//    }//GEN-LAST:event_jMenuItemShutDownMousePressed
//
//    /*************************************************
//     * 函数:       "重启"  菜单项响应函数
//     * 函数描述:	弹出确认框询问是否重启设备
//     *************************************************/
//    private void jMenuItemRebootMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemRebootMousePressed
//    {//GEN-HEADEREND:event_jMenuItemRebootMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//
//        int iResponse = JOptionPane.showConfirmDialog(this, "确定重启设备?", "重启", JOptionPane.OK_CANCEL_OPTION);
//        //确认
//        if (iResponse == 0) {
//            if (!hCNetSDK.NET_DVR_RebootDVR(lUserID)) {
//                JOptionPane.showMessageDialog(this, "设备重启失败");
//                return;
//            }
//        }
//        //取消
//        if (iResponse == 2) {
//            return;
//        }
//    }//GEN-LAST:event_jMenuItemRebootMousePressed
//
//    /*************************************************
//     * 函数:       "升级"  菜单项响应函数
//     * 函数描述:	新建窗体,显示升级选项
//     *************************************************/
//    private void jMenuItemUpgradeMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemUpgradeMousePressed
//    {//GEN-HEADEREND:event_jMenuItemUpgradeMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        JDialogUpGrade dlgUpgrade = new JDialogUpGrade(this, false, lUserID);
//        dlgUpgrade.setSize(440, 265);
//        centerWindow(dlgUpgrade);
//        dlgUpgrade.setVisible(true);
//    }//GEN-LAST:event_jMenuItemUpgradeMousePressed
//
//    /*************************************************
//     * 函数:       "格式化"  菜单项响应函数
//     * 函数描述:	新建窗体,显示格式化选项
//     *************************************************/
//    private void jMenuItemFormatMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemFormatMousePressed
//    {//GEN-HEADEREND:event_jMenuItemFormatMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        JDialogFormatDisk dlgFormatDisk = new JDialogFormatDisk(this, false, lUserID);
//        centerWindow(dlgFormatDisk);
//        dlgFormatDisk.setVisible(true);
//    }//GEN-LAST:event_jMenuItemFormatMousePressed
//
//    /*************************************************
//     * 函数:       "校时"  菜单项响应函数
//     * 函数描述:	新建窗体,显示校时选项
//     *************************************************/
//    private void jMenuItemCheckTimeMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemCheckTimeMousePressed
//    {//GEN-HEADEREND:event_jMenuItemCheckTimeMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        JDialogCheckTime dlgCheckTime = new JDialogCheckTime(this, false, lUserID);
//        centerWindow(dlgCheckTime);
//        dlgCheckTime.setVisible(true);
//    }//GEN-LAST:event_jMenuItemCheckTimeMousePressed
//
//    /*************************************************
//     * 函数:       "时间回放"  菜单项响应函数
//     * 函数描述:	新建窗体时间回放
//     *************************************************/
//    private void jMenuItemPlayTimeMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemPlayTimeMousePressed
//    {//GEN-HEADEREND:event_jMenuItemPlayTimeMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        JDialogPlayBackByTime dlgPlayTime = new JDialogPlayBackByTime(this, false, lUserID, m_sDeviceIP);
//        centerWindow(dlgPlayTime);
//        dlgPlayTime.setVisible(true);
//    }//GEN-LAST:event_jMenuItemPlayTimeMousePressed
//
//    /*************************************************
//     * 函数:      "回放"  按文件  菜单项响应函数
//     * 函数描述:	点击打开回放界面
//     *************************************************/
//    private void jMenuItemPlayBackRemoteMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemPlayBackRemoteMousePressed
//    {//GEN-HEADEREND:event_jMenuItemPlayBackRemoteMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//        //打开JDialog
//        //无模式对话框
//        JDialogPlayBack dialogPlayBack = new JDialogPlayBack(this, false, lUserID);
//        dialogPlayBack.setBounds(this.getX(), this.getY(), 730, 650);
//        centerWindow(dialogPlayBack);
//        dialogPlayBack.setVisible(true);
//    }//GEN-LAST:event_jMenuItemPlayBackRemoteMousePressed
//
//    /*************************************************
//     * 函数:      "用户配置"  按文件  菜单项响应函数
//     * 函数描述:   点击打开对话框,开始用户配置
//     *************************************************/
//    private void jMenuItemUserConfigMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemUserConfigMousePressed
//    {//GEN-HEADEREND:event_jMenuItemUserConfigMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//
//        JDialogUserConfig dlgUserConfig = new JDialogUserConfig(this, false, lUserID, m_strDeviceInfo, m_strIpparaCfg);
//        centerWindow(dlgUserConfig);
//        dlgUserConfig.setVisible(true);
//    }//GEN-LAST:event_jMenuItemUserConfigMousePressed
//
//    /*************************************************
//     * 函数:      "语音对讲"  按文件  菜单项响应函数
//     * 函数描述:   点击打开对话框,开始语音对讲相关操作
//     *************************************************/
//    private void jMenuItemVoiceComMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jMenuItemVoiceComMousePressed
//    {//GEN-HEADEREND:event_jMenuItemVoiceComMousePressed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//
//        JDialogVoiceTalk dlgVoiceTalk = new JDialogVoiceTalk(this, false, lUserID, m_strDeviceInfo);
//        centerWindow(dlgVoiceTalk);
//        dlgVoiceTalk.setVisible(true);
//    }//GEN-LAST:event_jMenuItemVoiceComMousePressed
//
//
//    /*************************************************
//     * 函数:      "Ip接入"  按文件  菜单项响应函数
//     * 函数描述:   点击打开对话框,IP接入配置
//     *************************************************/
//    private void jMenuItemIPAccessActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemIPAccessActionPerformed
//    {//GEN-HEADEREND:event_jMenuItemIPAccessActionPerformed
//        if (lUserID == -1) {
//            JOptionPane.showMessageDialog(this, "请先注册");
//            return;
//        }
//
//        JDialogIPAccessCfg dlgIPAccess = new JDialogIPAccessCfg(this, false, lUserID, m_strDeviceInfo);
//        centerWindow(dlgIPAccess);
//        dlgIPAccess.setVisible(true);
//    }//GEN-LAST:event_jMenuItemIPAccessActionPerformed
//
//    /*************************************************
//     * 函数:      "播放窗口"  双击响应函数
//     * 函数描述:   双击全屏预览当前预览通道
//     *************************************************/
//    private void panelRealplayMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_panelRealplayMousePressed
//    {//GEN-HEADEREND:event_panelRealplayMousePressed
//        if (!bRealPlay) {
//            return;
//        }
//        //鼠标单击事件为双击
//        if (evt.getClickCount() == 2) {
//            //新建JWindow 全屏预览
//            final JWindow wnd = new JWindow();
//            //获取屏幕尺寸
//            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//            wnd.setSize(screenSize);
//            wnd.setVisible(true);
//
//            final HWND hwnd = new HWND(Native.getComponentPointer(wnd));
//            m_strClientInfo.hPlayWnd = hwnd;
//            final int lRealHandle = hCNetSDK.NET_DVR_RealPlay_V30(lUserID,
//                    m_strClientInfo, null, null, true);
//
//            //JWindow增加双击响应函数,双击时停止预览,退出全屏
//            wnd.addMouseListener(new java.awt.event.MouseAdapter() {
//                public void mousePressed(java.awt.event.MouseEvent evt) {
//                    if (evt.getClickCount() == 2) {
//                        //停止预览
//                        hCNetSDK.NET_DVR_StopRealPlay(lRealHandle);
//                        wnd.dispose();
//                    }
//                }
//            });
//
//        }
//    }//GEN-LAST:event_panelRealplayMousePressed
//
//    /*************************************************
//     * 函数:    centerWindow
//     * 函数描述:窗口置中
//     *************************************************/
//    public static void centerWindow(Container window) {
//        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        int w = window.getSize().width;
//        int h = window.getSize().height;
//        int x = (dim.width - w) / 2;
//        int y = (dim.height - h) / 2;
//        window.setLocation(x, y);
//    }
//
//    /*************************************************
//     * 函数:    CreateDeviceTree
//     * 函数描述:建立设备通道树
//     *************************************************/
//    private void CreateDeviceTree() {
//        //ibrBytesReturned 实际收到的数据长度指针，不能为NULL
//        IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
//        boolean bRet;
//
//        HCNetSDK.NET_DVR_IPPARACFG_V40 m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG_V40();
//        m_strIpparaCfg.write();
//        //lpIpParaConfig 接收数据的缓冲指针
//        Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
//        bRet = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG_V40, 0, lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
//        m_strIpparaCfg.read();
//
//        if (!bRet) {
//            //设备不支持,则表示没有IP通道
//            for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
//
//            }
//        } else {
//            //设备支持IP通道
//            for (int iChannum = 0; iChannum < HCNetSDK.MAX_CHANNUM_V30; iChannum++) {
//                if (m_strIpparaCfg.struStreamMode[iChannum].byGetStreamType == 0) {
//                    m_strIpparaCfg.struStreamMode[iChannum].uGetStream.setType(HCNetSDK.NET_DVR_IPCHANINFO.class);
//                    if (m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.byEnable == 1) {
//
//                        m_iTreeNodeNum++;
//                    }
//                    else {
//                        m_iTreeNodeNum++;
//
//                    }
//
//                }
//
//            }
//
//        }
//    }
//
//    /*************************************************
//     * 函数:    getChannelNumber
//     * 函数描述:从设备树获取通道号
//     *************************************************/
//    int getChannelNumber() {
//        int iChannelNum = -1;
//
//        return iChannelNum;
//    }
//
//    /*************************************************
//     * 函数:       主函数
//     * 函数描述:新建ClientDemo窗体并调用接口初始化SDK
//     *************************************************/
//    public static void main(String args[]) {
//        try {
//            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//
//                if (hCNetSDK == null&&playControl==null) {
//                    if (!CreateSDKInstance()) {
//                        System.out.println("Load SDK fail");
//                        return;
//                    }
//                    if (!CreatePlayInstance()) {
//                        System.out.println("Load PlayCtrl fail");
//                        return;
//                    }
//                }
//                //linux系统建议调用以下接口加载组件库
//                if (osSelect.isLinux()) {
//                    HCNetSDK.BYTE_ARRAY ptrByteArray1 = new HCNetSDK.BYTE_ARRAY(256);
//                    HCNetSDK.BYTE_ARRAY ptrByteArray2 = new HCNetSDK.BYTE_ARRAY(256);
//                    //这里是库的绝对路径，请根据实际情况修改，注意改路径必须有访问权限
//                    String strPath1 = "/home/hik/LinuxSDK/libcrypto.so.1.1";
//                    String strPath2 = "/home/hik/LinuxSDK/libssl.so.1.1";
//
//                    System.arraycopy(strPath1.getBytes(), 0, ptrByteArray1.byValue, 0, strPath1.length());
//                    ptrByteArray1.write();
//                    hCNetSDK.NET_DVR_SetSDKInitCfg(3, ptrByteArray1.getPointer());
//
//                    System.arraycopy(strPath2.getBytes(), 0, ptrByteArray2.byValue, 0, strPath2.length());
//                    ptrByteArray2.write();
//                    hCNetSDK.NET_DVR_SetSDKInitCfg(4, ptrByteArray2.getPointer());
//
//                    String strPathCom = "/home/hik/LinuxSDK/";
//                    HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
//                    System.arraycopy(strPathCom.getBytes(), 0, struComPath.sPath, 0, strPathCom.length());
//                    struComPath.write();
//                    hCNetSDK.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());
//                }
//
//                boolean initSuc = hCNetSDK.NET_DVR_Init();
//                if (initSuc != true) {
//                    System.out.println("初始化失败");
//                }
//                if(fExceptionCallBack == null)
//                {
//                    fExceptionCallBack = new FExceptionCallBack_Imp();
//                }
//                Pointer pUser = null;
//                if (!hCNetSDK.NET_DVR_SetExceptionCallBack_V30(0, 0, fExceptionCallBack, pUser)) {
//                    return ;
//                }
//                System.out.println("设置告警回调成功");
//                hCNetSDK.NET_DVR_SetLogToFile(3, "..//sdklog", false);
//            }
//        });
//    }
//
//    /**
//     * 动态库加载
//     *
//     * @return
//     */
//    private static boolean CreateSDKInstance() {
//        if (hCNetSDK == null) {
//            synchronized (HCNetSDK.class) {
//                String strDllPath = "";
//                try {
//                    if (osSelect.isWindows())
//                        //win系统加载库路径
//                        strDllPath = System.getProperty("user.dir")+"\\lib\\HCNetSDK.dll";
//
//                    else if (osSelect.isLinux())
//                        //Linux系统加载库路径
//                        strDllPath = "/home/hik/LinuxSDK/libhcnetsdk.so";
//                    hCNetSDK = (HCNetSDK) Native.loadLibrary(strDllPath, HCNetSDK.class);
//                } catch (Exception ex) {
//                    System.out.println("loadLibrary: " + strDllPath + " Error: " + ex.getMessage());
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 播放库加载
//     *
//     * @return
//     */
//    private static boolean CreatePlayInstance() {
//        if (playControl == null) {
//            synchronized (PlayCtrl.class) {
//                String strPlayPath = "";
//                try {
//                    if (osSelect.isWindows())
//                        //win系统加载库路径
//                        strPlayPath = System.getProperty("user.dir")+"\\lib\\PlayCtrl.dll";
//                    else if (osSelect.isLinux())
//                        //Linux系统加载库路径
//                        strPlayPath = "/home/hik/LinuxSDK/libPlayCtrl.so";
//                    playControl=(PlayCtrl) Native.loadLibrary(strPlayPath,PlayCtrl.class);
//
//                } catch (Exception ex) {
//                    System.out.println("loadLibrary: " + strPlayPath + " Error: " + ex.getMessage());
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
//
//    /******************************************************************************
//     * 内部类:   FRealDataCallBack
//     * 实现预览回调数据
//     ******************************************************************************/
//    class FRealDataCallBack implements HCNetSDK.FRealDataCallBack_V30 {
//        //预览回调
//        public void invoke(int lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser) {
//            switch (dwDataType) {
//                case HCNetSDK.NET_DVR_SYSHEAD: //系统头
//
//                    if (!playControl.PlayM4_GetPort(m_lPort)) //获取播放库未使用的通道号
//                    {
//                        break;
//                    }
//
//                    if (dwBufSize > 0) {
//                        if (!playControl.PlayM4_SetStreamOpenMode(m_lPort.getValue(), PlayCtrl.STREAME_REALTIME))  //设置实时流播放模式
//                        {
//                            break;
//                        }
//
//                        if (!playControl.PlayM4_OpenStream(m_lPort.getValue(), pBuffer, dwBufSize, 1024 * 1024)) //打开流接口
//                        {
//                            break;
//                        }
//
//                        if (!playControl.PlayM4_Play(m_lPort.getValue(), hwnd)) //播放开始
//                        {
//                            break;
//                        }
//                    }
//                case HCNetSDK.NET_DVR_STREAMDATA:   //码流数据
//                    if ((dwBufSize > 0) && (m_lPort.getValue() != -1)) {
//                        if (!playControl.PlayM4_InputData(m_lPort.getValue(), pBuffer, dwBufSize))  //输入流数据
//                        {
//                            break;
//                        }
//                    }
//            }
//        }
//    }
//
//    static class FExceptionCallBack_Imp implements HCNetSDK.FExceptionCallBack {
//        public void invoke(int dwType, int lUserID, int lHandle, Pointer pUser) {
//            System.out.println("异常事件类型:"+dwType);
//            return;
//        }
//    }
//
//}
//
