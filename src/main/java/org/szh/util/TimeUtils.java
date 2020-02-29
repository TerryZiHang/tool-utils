package org.szh.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Terry Zi
 *
 */
public class TimeUtils {

    private static final int[] end_time = { 10, 0, 0 };// 截单数据
    private static final int cannel_minute = 15;// 支付时间

    public static Calendar calendar() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Date today() {
        return calendar().getTime();
    }

    public static Date today(int hh, int mm, int ss) {
        return initDate(calendar(), hh, mm, ss);
    }

    public static Date initDate(Date time, int hh, int mm, int ss) {
        Calendar calendar = calendar();
        calendar.setTime(time);
        return initDate(calendar, hh, mm, ss);
    }

    /**
     * 自定义起始时间
     * 
     * @date 2019年2月20日 上午11:55:44
     * @param calendar
     * @param hh
     * @param mm
     * @param ss
     * @return
     */
    public static Date initDate(Calendar calendar, int hh, int mm, int ss) {
        calendar.set(Calendar.HOUR_OF_DAY, hh);
        calendar.set(Calendar.MINUTE, mm);
        calendar.set(Calendar.SECOND, ss);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取未来某天的当前时间
     * 
     * @date 2019年2月20日 上午11:59:09
     * @param i
     * @return
     */
    public static Date tomorrow(int i) {
        Calendar calendar = calendar();
        calendar.add(Calendar.DAY_OF_MONTH, i);
        return calendar.getTime();
    }

    /**
     * 获取截单时间
     * 
     * @date 2019年2月21日 上午11:57:59
     * @return
     */
    public static Date getDeadline() {
        return today(end_time[0], end_time[1], end_time[2]);
    }

    /**
     * 获取指定格式的时间
     * 
     * @date 2019年2月21日 上午11:58:13
     * @param time
     * @param pattern
     * @return
     */
    public static String text(Date time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(time);
    }

    public static String text(String pattern) {
        return text(today(), pattern);
    }

    public static String text() {
        return text(today(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String text(Date time) {
        return text(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String textDate() {
        return text(today(), "yyyy-MM-dd");
    }

    /**
     * 获取取消时间
     * 
     * @date 2019年2月21日 上午11:58:27
     * @param add_time
     * @param dev_time
     * @return
     */
    public static Date getCanelTime(Date add_time, Date dev_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dead_line = getDeadline();
        Calendar calendar = calendar();
        calendar.setTime(add_time);
        calendar.add(Calendar.MINUTE, cannel_minute);
        if (sdf.format(dev_time).equals(sdf.format(dead_line))
                && calendar.getTime().after(dead_line)) {
            return dead_line;
        } else {
            return calendar.getTime();
        }
    }

    /**
     * 是否过期
     * 
     * @date 2019年2月21日 上午11:58:40
     * @param dev_time
     * @return
     */
    public static boolean isTime(Date dev_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dead_line = getDeadline();
        if (sdf.format(dev_time).equals(sdf.format(dead_line)) && new Date().after(dead_line)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取距离当前某天指定格式的时间
     * 
     * @date 2019年2月21日 上午11:59:24
     * @param count
     * @return
     */
    public static String getOverdue(int count) {
        return text(tomorrow(count), "yyyy-MM-dd");
    }

    /**
     * 获取未来某天的指定时间
     * 
     * @date 2019年6月24日 下午2:45:26
     * @param day
     * @param hh
     * @param mm
     * @param ss
     * @return
     */
    public static Date tomorow(int i, int hh, int mm, int ss) {
        Calendar calendar = calendar();
        calendar.add(Calendar.DAY_OF_MONTH, i);
        return initDate(calendar, hh, mm, ss);
    }

    /**
     * 时间差值格式化
     *
     * @param endTime
     * @param beginTime
     * @return
     * @date 2019年10月31日
     */
    public static String getFormatDate(Date endTime, Date beginTime) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endTime.getTime() - beginTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
    }
}
