package org.szh.util;

import java.util.Random;

/**
 * 券码生成工具
 * 
 * @author Terry Zi
 *
 */
public class CouponCodeUtil {

    public static String getCharAndNumr(int length) {
    	int n = 3;
        if (length >= n) {
            String val = "";
            Random random = new Random();
            int t0 = 0;
            int t1 = 0;
            int t2 = 0;
            for (int i = 0; i < length; i++) {
                String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
                if ("char".equalsIgnoreCase(charOrNum)) {
                    int choice = 0;
                    if (random.nextInt(2) % 2 == 0) {
                        choice = 65;
                        t0 = 1;
                    } else {
                        choice = 97;
                        t1 = 1;
                    }
                    val += (char) (choice + random.nextInt(26));
                } else if ("num".equalsIgnoreCase(charOrNum)){
                    val += String.valueOf(random.nextInt(10));
                    t2 = 1;
                }
            }
            if (t0 == 0 || t1 == 0 || t2 == 0) {
                val = getCharAndNumr(length); 
                return val;
            } else {
                return val;
            }
        } else {
            return null;
        }
    }

    public static String createCode(int count) {
        String code = getCharAndNumr(count);
        code += String.valueOf(System.currentTimeMillis()).substring(7);
        return code;
    }

    public static void main(String[] args) {
        System.out.println(createCode(15));
    }
}
