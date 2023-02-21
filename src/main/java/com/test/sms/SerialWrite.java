package com.test.sms;
import java.io.*;
import java.util.*;
import javax.comm.*;
public class SerialWrite {

    static CommPortIdentifier myPort;
    static SerialPort serialPort;
    static OutputStream outputStream;

    String tophone;//对方手机号码
    String smstext;//短信内容

    public  SerialWrite (){}

    public int SerialR(){


        String cmgf = "AT+CMGF=1"+(char)13;
        String cmgs ="AT+CMGS="+(char)34 + this.getTophone() + (char)34 + (char)13 +this.getSmstext()+ (char)26;
        String messageString = cmgf + cmgs ;

        try{
            myPort = CommPortIdentifier.getPortIdentifier("COM1");
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }

        try{
            serialPort = (SerialPort)myPort.open("SimpleWriteApp", 2000);
        } catch (PortInUseException e) {
            System.out.println("串口打开失败");
            return 0;
            //e.printStackTrace();
        }
        try{
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        try {
            serialPort.setSerialPortParams(9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {}
        try {
            outputStream.write(messageString.getBytes());
            System.out.println(messageString);
            System.out.println("短心发送成功");
            System.out.println("*************************************");
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        serialPort.close();//关闭串口
        return 1;
    }

    public static void main(String args[]){
    	SerialWrite srw=new SerialWrite();
    	srw.setTophone("18600455182");
    	srw.setSmstext("dddd");
      srw.SerialR();
    }

    public String getSmstext() {
        return smstext;
    }

    public void setSmstext(String smstext) {
        this.smstext = smstext;
    }

    public String getTophone() {
        return tophone;
    }

    public void setTophone(String tophone) {
        this.tophone = tophone;
    }
}


