//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package com.test;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

public class Sm4Util {
    private static final String ENCODING = "UTF-8";
    public static final String ALGORITHM_NAME = "SM4";
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS7Padding";
    public static final int DEFAULT_KEY_SIZE = 128;

    public Sm4Util() {
    }

    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, "BC");
        Key sm4Key = new SecretKeySpec(key, "SM4");
        cipher.init(mode, sm4Key);
        return cipher;
    }

    public static String encryptEcb(String hexKey, String paramStr) throws Exception {
        String cipherText = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] srcData = paramStr.getBytes("UTF-8");
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        cipherText = ByteUtils.toHexString(cipherArray);
        return cipherText;
    }

    public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher("SM4/ECB/PKCS7Padding", 1, key);
        return cipher.doFinal(data);
    }

    public static String decryptEcb(String hexKey, String cipherText) throws Exception {
        String decryptStr = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        decryptStr = new String(srcData, "UTF-8");
        return decryptStr;
    }

    public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher("SM4/ECB/PKCS7Padding", 2, key);
        return cipher.doFinal(cipherText);
    }

    public static byte[] generateKey() throws Exception {
        return generateKey(128);
    }

    public static byte[] generateKey(int keySize) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("SM4", "BC");
        kg.init(keySize, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    public static boolean verifyEcb(String hexKey, String cipherText, String paramStr) throws Exception {
        boolean flag = false;
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        byte[] decryptData = decrypt_Ecb_Padding(keyData, cipherData);
        byte[] srcData = paramStr.getBytes("UTF-8");
        flag = Arrays.equals(decryptData, srcData);
        return flag;
    }

    public static void main(String[] args) {
        try {
            System.out.println("time: 2203231413"+ ",有效时间" + 40 + ", uid:"+ 141202);
            int uid = 143402;   int nimuteAndTime = 4013; //有效期 + 分
            String info = "220323";
            int trsamsInfo = transfer48(info);
            System.out.println("转义后：" + trsamsInfo);
            int uidDivi = uid % 10000;
            String json = String.valueOf(trsamsInfo / uidDivi).length() +""+
                    trsamsInfo / uidDivi +"" + trsamsInfo % uidDivi +""+nimuteAndTime;
            System.out.println("根据uid计算有效信息有效信息：" + json);
            String key = "141202617B3BED1581EF34C94CA3177D";
            System.out.println("加密信息key:" + key);
            String encryptEcb = encryptEcb(key, json).toLowerCase();;
            System.out.println("sm4加密后的数据：" + encryptEcb);
            if(encryptEcb.charAt(0) > '9'){
                System.out.println("加密后的有效数据：" + (encryptEcb.charAt(0)-96) + json);
            } else {
                System.out.println("加密后有效的数据：" + (encryptEcb.charAt(0)) + json);
            }

            String divi = json.substring(1, 1 + Integer.parseInt(json.substring(0, 1)));
            int total = Integer.parseInt(divi)* uidDivi + Integer.parseInt(json.substring(1 + Integer.parseInt(json.substring(0, 1)), json.length()-4));
            System.out.println(total);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }


    private static int transfer48(String s){
        int length = s.length()/2;
        int total = 0;
        for(int i=0;i<length;i++){
            total += (int)(Integer.parseInt(s.substring(i*2,i*2+2)) * Math.pow(48, length-1-i));
        }
        return total;
    }
    private static int transfer48ToS(int num){

        int total = 0;
        int count = 0;
        while (num >= 48){
            total+=((num%48)*Math.pow(100, count));
            count++;
            num = num/48;
        }
        if(num> 0){
            total+=((num%48)*Math.pow(100, count));
        }
        return total;
    }


    public static long ipToLong(String ipAddress) {

        long result = 0;

        String[] ipAddressInArray = ipAddress.split("\\.");

        for (int i = 3; i >= 0; i--) {

            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ip << (i * 8);

        }

        return result;

    }


    public static int zigzag_to_int(int n)

    {

        return ((n) >>1) ^ -(n & 1);

    }
    public static int int_to_zigzag(int n)

    {

        return (n <<1) ^ (n >>31);

    }
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}
