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

public class Sm4Utils {
    private static final String ENCODING = "UTF-8";
    public static final String ALGORITHM_NAME = "SM4";
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/ZeroBytePadding";
    public static final int DEFAULT_KEY_SIZE = 128;

    public Sm4Utils() {
    }

    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    public static String encryptEcb(String hexKey, String paramStr) throws Exception {
        String cipherText = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] srcData = HexUtils.hexString2Bytes(paramStr);
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        cipherText = ByteUtils.toHexString(cipherArray);
        return cipherText;
    }

    public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 1, key);
        return cipher.doFinal(data);
    }

    public static String decryptEcb(String hexKey, String cipherText) throws Exception {
        String decryptStr = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        return HexUtils.bytes2HexString(srcData);
    }

    public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 2, key);
        return cipher.doFinal(cipherText);
    }

    public static byte[] generateKey() throws Exception {
        return generateKey(DEFAULT_KEY_SIZE);
    }

    public static byte[] generateKey(int keySize) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(keySize, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    public static boolean verifyEcb(String hexKey, String cipherText, String paramStr) throws Exception {
        boolean flag = false;
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        byte[] decryptData = decrypt_Ecb_Padding(keyData, cipherData);
        byte[] srcData = paramStr.getBytes();
        flag = Arrays.equals(decryptData, srcData);
        return flag;
    }

    public static int hexCharToInt(char c) {
        if (c >= '0' && c <= '9') return (c - '0');
        if (c >= 'A' && c <= 'F') return (c - 'A' + 10);
        if (c >= 'a' && c <= 'f') return (c - 'a' + 10);
        throw new RuntimeException("invalid hex char '" + c + "'");
    }

    public static byte[] hexStringToBytes(String s) {
        byte[] ret;
        if (s == null) return null;
        int sz = s.length();
        ret = new byte[sz / 2];
        for (int i = 0; i < sz; i += 2) {
            ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4)
                    | hexCharToInt(s.charAt(i + 1)));
        }
        return ret;
    }


    public static void main(String[] args) {
        try {
//            String key = "A70D7AE795C1529B3375154F47296E03";
//            String json = "05 01 02 20 60 97";
//            String json = "0501022060970000";
            String data = "050102402157";
            String key = "7287F5617B3BED1581EF34C94CA3177D";
            String encryStr = encryptEcb(key, data);
            System.out.println(encryStr);
            String s = decryptEcb(key, encryStr);
            System.out.println(s);

        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}
