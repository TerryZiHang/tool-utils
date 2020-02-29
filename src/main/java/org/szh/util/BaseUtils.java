package org.szh.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 * @author Administrator
 * @date 2019年2月21日
 */
/**
 * 
 * 业务工具类
 * @author Terry Zi
 * @date 2019年12月27日
 * @description
 */
public final class BaseUtils {

    public static boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            return m.matches();
        }
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

    /**
     * 生成 min ~ max 范围的随机数
     * 
     * @date 2019年2月21日 上午11:54:03
     * @param min
     * @param max
     * @return
     */
    public static Integer random(int min, int max) {
        if (min >= max)
            return max;
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 生成指定范围的随机数
     * 
     * @date 2019年2月21日 上午11:54:58
     * @param digit
     * @return
     */
    public static Integer random(int digit) {
        int pow = (int) Math.pow(10, digit - 1);
        return (int) ((Math.random() * 9 + 1) * pow);
    }
    
    /**
     * 生成订单号
     * 
     * @date 2019年2月21日 上午11:56:37
     * @param orderType
     *            1:普通订单 2 团购订单 3：秒杀订单 4：现场支付订单
     * @param type 0：后台添加 1：商城添加 
     * @return
     */
    public static String createOrderNo(String orderType, int type) {
        StringBuffer orderNo = new StringBuffer();
        orderNo.append(orderType);
        orderNo.append(type);
        orderNo.append(System.nanoTime() + "");
        Random random = new Random();
        orderNo.append(random.nextInt(8999) + 1000);
        return orderNo.toString();
    }

    /**
     * 创建其他订单编码
     * 
     * @date 2019年6月27日 上午10:38:50
     * @param order_no
     * @return
     */
    public static String createOtherOrderNo(String order_no) {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String result = "LJS" + date;
        result += order_no.substring(11, 16);
        return result;
    }

    /**
     * 创建商品编码
     *
     * @param type:0 单品 1商品券 2套餐
     * @param id
     * @return
     * @date 2019年6月27日
     */
    public static String createGoodsNo(int type, String id) {
        StringBuilder goodsNo = new StringBuilder();
        goodsNo.append("LJS");
        goodsNo.append(type);
        goodsNo.append(id.substring(12,16));
        goodsNo.append((System.nanoTime() + "").substring(10));
        return goodsNo.toString();
    }

    /**
     * 创建退款单号
     *
     * @param orderType 1:普通订单 2 团购订单 3：秒杀订单
     * @return
     * @date 2018-9-27
     */
    public static String createRefundNo(String orderType) {
        StringBuffer refundNo = new StringBuffer();
        refundNo.append(orderType);
        refundNo.append(System.nanoTime() + "");
        Random random = new Random();
        refundNo.append(random.nextInt(8999) + 1000);
        return refundNo.toString();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
//            System.out.println(createRefundNo(1, "20522591781388288"));
            System.out.println(createOrderNo("1", 0));
//            System.out.println(createGoodsNo(0, "20522591781388288")); 
//            System.out.println((System.nanoTime() + "").substring(2));
        }
    }
}
