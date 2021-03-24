package org.szh.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;


public class AppUtil {

    private AppUtil() {
    }

    private static String codes = "***************************";
    private static Random r = new Random();

    public static String getExceptionStackTraceAsString(Exception exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String generateSHA256(String text) {
        return DigestUtils.sha256Hex(text);
    }

    public static String readResourceToString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static boolean isNotValidPhoneNumber(String phoneNumber) {
        String patternString = "^\\+[1-9]\\d{1,14}$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(phoneNumber);
        return !matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String patternString = "\\+(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|\n" +
                "2[98654321]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|\n" +
                "4[987654310]|3[9643210]|2[70]|7|1)\\d{1,14}$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     * 生成6位邀请码
     *
     * @return
     */
    public static String generateInviteCode() {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = r.nextInt(codes.length());
            stringBuilder.append(codes.charAt(index));
        }
        return stringBuilder.toString();

    }

    /**
     * 生成4位验证码
     *
     * @return
     */
    public static String generateVerifyCode() {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = r.nextInt(codes.length());
            stringBuilder.append(codes.charAt(index));
        }
        return stringBuilder.toString();

    }

    public static String generateRandomVerifyCode(String seed) {
        String uuid = UUID.randomUUID().toString();
        String verifyCode = generateSHA256(seed + uuid + Math.random());
        return verifyCode;

    }

    public static String getRealIp(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;

    }

    public static boolean isUrl(String text) {
        String rex = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$";
        return matcher(text, rex);
    }

    public static boolean matcher(String text, String rex) {
        text = text == null ? "" : text;
        Pattern pattern = Pattern.compile(rex);
        return pattern.matcher(text).matches();
    }

    public static boolean isValidColor(String color) {
        String rex = "^#[0-9a-fA-F]{6}$";
        return matcher(color, rex);
    }
}
