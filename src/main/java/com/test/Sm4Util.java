//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
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
            String json = "中国国密加解密算法";
            String key = "7287F5617B3BED1581EF34C94CA3177D";
            String encryptEcb = encryptEcb(key, json);
            System.out.println(decryptEcb(key,encryptEcb));
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}
