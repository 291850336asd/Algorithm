package com.test;

//模式
import static cn.hutool.crypto.Mode.ECB;
//加密算法
import static cn.hutool.crypto.Padding.NoPadding;
import static cn.hutool.crypto.Padding.ZeroPadding;

import cn.hutool.core.codec.Base64;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

public class Sm4Util {


    private static Logger logger = LoggerFactory.getLogger(Sm4Util.class);
    //国四加解密的秘钥需要16个字符长度
    private static String key = "A70D7AE795C1529B3375154F47296E03";

    /**
     * hutool MS4加密
     *
     * @param content
     *            加密内容
     */
    public static String encode(String content) {

        try {
            SymmetricCrypto sm4 = new SM4(ECB, ZeroPadding, ByteUtils.fromHexString(key));
            String encryptHex = sm4.encryptHex(content);
            return encryptHex;
        } catch (Exception e) {

            e.printStackTrace();
            logger.error("MS4加密失败");
        }
        return null;
    }

    /**
     * hutool 解密SM4
     *
     * @param encodeContent
     * @return
     */
    public static String decode(String encodeContent) {

        try {

            SymmetricCrypto sm4 = new SM4(ECB, ZeroPadding, key.getBytes());
            String decryptStr = new String(sm4.decrypt(encodeContent.getBytes()));
            return decryptStr;
        } catch (Exception e) {

            e.printStackTrace();
            logger.error("SM4解密失败");
        }
        return null;
    }


    public static void main(String[] args) {
        String encode = encode("050102206097");
        System.out.println(encode);
//        System.out.println(decode(encode));
//        System.out.println(decode("ED210FF5E4E5BDBB5044BAC9B67F8A2E"));
    }
}