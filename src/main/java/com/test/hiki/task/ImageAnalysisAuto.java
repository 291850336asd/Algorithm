package com.test.hiki.task;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.sound.sampled.AudioFormat.Encoding;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.template.io.Writer;
import com.test.hiki.commonFunction.JsonFormatTool;
import com.test.hiki.commonFunction.MyWebSocketListener;

public class ImageAnalysisAuto extends Controller {
    public void index() {
        render("/UI-Resource/Task/ImageAnalysisAuto.html");
    }
    private static String strWebRootPath=PathKit.getWebRootPath();//Get the server root directory to save the alarm receiving message
    
    public void getCapability()
    {
        
    }
    
    public void CreateElementsAuto(){
        String data="";
        String filename=strWebRootPath+"\\RunHistory\\ApplyScene.txt";
        try {
            BufferedReader reader=new BufferedReader(new FileReader(filename));
            String line=reader.readLine();
            while(line!=null){
                data+=line;
                line=reader.readLine();
            }
            reader.close();
            setAttr("returnData",data);   
        } catch (IOException e) {
            // TODO �Զ����ɵ� catch ��
            e.printStackTrace();
        }finally {
            renderJson();
        }
        
    }
    
    public void PostTaskInfo(){
        String data=getPara("TaskInfo");
        String filename=strWebRootPath+"\\RunHistory\\test.txt";
        try{
            BufferedWriter writer=new BufferedWriter(new FileWriter(filename));
            writer.write(data);
            writer.close();
            setAttr("returnData", "success");
        }catch(IOException e){
            e.printStackTrace();
            setAttr("returnData", "failed");
        }finally {
            renderJson();
        }
        
    }
    
    public void CreatePicUrl()
    {
        String picName=getPara("picName");
        String picFile=getPara("picFile");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//Format the date
        String strDateTimeNow=df.format(new Date());
        String saveFilePath=strWebRootPath+"\\RunHistory\\test.txt";      
        
        try {      
            byte[] imgByte = picFile.getBytes("UTF-8");  
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(saveFilePath));//��������
            imageOutput.write(imgByte,0,imgByte.length);
            imageOutput.close();
            
        } catch (Exception e) {
            // TODO: handle exception
            this.renderJson();
        }   
        this.renderJson("success");
    }
    
    public static byte[] hex2byte(String str) { // �ַ���ת������
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
        return null;
        }
    }
}
