package org.szh.util;

import org.apache.commons.codec.binary.Base64;

import java.util.UUID;

/**
 * @Description: Base64 编码包含大小写字母26位，加上数字，+，/共64字符，
 * uuid 32 个字符
 * 利用uuid 随机性，唯一性，base64字符编码生成22字符
 * @Author Terry
 * @DATE 2020/11/12 9:21
 * @Version 1.0
 **/
public class LinkCodeUtil {

    public static String generateUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String generateCode() {
        UUID uuid = UUID.randomUUID();
        return generateCode(uuid);
    }

    protected static String generateCode(UUID uuid) {
        byte[] byUuid = new byte[16];
        long least = uuid.getLeastSignificantBits();
        long most = uuid.getMostSignificantBits();
        long2bytes(most, byUuid, 0);
        long2bytes(least, byUuid, 8);
        return Base64.encodeBase64URLSafeString(byUuid);
    }

    protected static void long2bytes(long value, byte[] bytes, int offset) {
        for (int i = 7; i > -1; i--) {
            bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
        }
    }

    public static String generateCode(String id) {
        UUID uuid = UUID.fromString(id);
        return generateCode(uuid);
    }

    public static String parseCode(String code) {
        if (code.length() != 22) {
            throw new IllegalArgumentException("Invalid code!");
        }
        byte[] byUuid = Base64.decodeBase64(code + "==");
        long most = bytes2long(byUuid, 0);
        long least = bytes2long(byUuid, 8);
        UUID uuid = new UUID(most, least);
        return uuid.toString();
    }

    protected static long bytes2long(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 7; i > -1; i--) {
            value |= (((long) bytes[offset++]) & 0xFF) << 8 * i;
        }
        return value;
    }

//    public static void main(String[] args) {
//        String code = generateCode();
//        System.out.println(code.length());
//    }
}
