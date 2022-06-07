package com.test;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class IMageVerTes {
    public static void main(String[] args) throws Exception {
        URL url;
        String s="";
        try {
            url = new URL("https://hk.sz.gov.cn/user/getVerify");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] data = readInputStream(inStream);
            //new一个文件对象用来保存图片，默认保存当前工程根目录
            File imageFile = new File("1.jpeg");
            //创建输出流
            FileOutputStream outStream = new FileOutputStream(imageFile);
            //写入数据
            outStream.write(data);
            //关闭输出流
            outStream.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String code = getImgContent("1.jpeg");
        System.out.println("验证码 = " + code);
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    protected static String getImgContent(String imgUrl) {
        String content = "";
        File imageFile = new File(imgUrl);
//读取图片数字
        ITesseract instance = new Tesseract();


        File tessDataFolder = new File("D:\\Algorithm\\src\\main\\resources\\tessdata");
        instance.setLanguage("eng");//英文库识别数字比较准确
        instance.setDatapath(tessDataFolder.getAbsolutePath());
        try {
            content = instance.doOCR(imageFile).replace("\n", "");
            System.out.println(content);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return content;
    }

    /**
     * 处理图片
     * 		其实可以不对图片做处理，直接使用Tess4j直接读取图片文字。
     * 		不过不经过图片处理的图片识别率较低，大概只有10%的成功率。
     * 		经过处理的图片，识别率提高到了50%左右。
     * @param imagePath 图片的绝对或相对路径
     * @return 处理后的图片保存路径
     * @throws IOException
     */


    /**
     * 根据文件字节流获取文件MD5
     * @param fileBytes
     * @return
     */
    public static String getFileMd5(byte[] fileBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] mdBytes = md.digest(fileBytes);
            BigInteger bigInt = new BigInteger(1, mdBytes);
            return bigInt.toString(16);
        } catch (Exception e) {
           e.printStackTrace();
            return null;
        }
    }
}