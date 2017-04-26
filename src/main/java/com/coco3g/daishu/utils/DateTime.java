package com.coco3g.daishu.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lisen on 16/1/4.
 */
public class DateTime {

    public static String getCurrentDateTime() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd ");
        String now = sf.format(date);
        return now;
    }

    public static String getCurrentTime() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(d);
        return now;
    }

    public static String convertDate(Date d, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String datetime = sdf.format(d);
        return datetime;
    }

    public static long getTimesTamp(String pattern, String dateString) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date;
        try {
            date = df.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static int getBetweenDayNumber(String dateA, String dateB) {
        long dayNumber = 0;
        long mins = 60L * 1000L;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(dateA);
            Date d2 = df.parse(dateB);
            dayNumber = (d2.getTime() - d1.getTime()) / mins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) dayNumber;
    }

    /**
     * 日期转换时间戳
     *
     * @param user_time
     * @return
     */
    public static String getTime(String user_time) {
        // String re_time = null;
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d;
        try {

            d = sdf.parse(user_time);
            long l = d.getTime();
            str = String.valueOf(l);
            // re_time = str.substring(0, 10);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取当前时间格式化后的字符串
     *
     * @param pattern 格式，如"yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的字符串
     */
    public static String getDateFormated(String pattern) {
        Date date = new Date();
        SimpleDateFormat formater = new SimpleDateFormat(pattern);
        String dateFormated = formater.format(date);
        return dateFormated;
    }

    /**
     * 获取指定时间格式化后的字符串
     *
     * @param pattern 格式，如"yyyy-MM-dd HH:mm:ss"
     * @param date    日期
     * @return 格式化后的字符串
     */
    public static String getDateFormated(String pattern, Date date) {
        SimpleDateFormat formater = new SimpleDateFormat(pattern);
        String dateFormated = formater.format(date);
        return dateFormated;
    }

    /**
     * 获取指定时间格式化后的字符串
     *
     * @param pattern 格式，如"yyyy-MM-dd HH:mm:ss"
     * @param date    日期
     * @return 格式化后的字符串
     */
    public static String getDateFormated(String pattern, long date) {
        SimpleDateFormat formater = new SimpleDateFormat(pattern);
        String dateFormated = formater.format(new Date(date));
        return dateFormated;
    }

    /**
     * 获取指定时间格式化后的字符串(12小时制)
     *
     * @param pattern 格式，如"yyyy-MM-dd hh:mm:ss aa"
     * @param date    日期
     * @return 格式化后的字符串
     */
    public static String getDateFormatedBy12(String pattern, long date) {
        SimpleDateFormat formater = new SimpleDateFormat(pattern, Locale.ENGLISH);
        String dateFormated = formater.format(new Date(date));
        return dateFormated;
    }

    /**
     * 今天是星期几
     *
     * @return
     */
    public static String getWeekday() {
        Date date = new Date();
        String weekday = "";
        final String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        weekday = dayNames[dayOfWeek - 1];
        return weekday;
    }

    /**
     * 某一天是星期几
     *
     * @param date
     * @return
     */
    public static String getWeekday(Date date) {
        String weekday = "";
        final String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        weekday = dayNames[dayOfWeek - 1];
        return weekday;
    }

    /**
     * 某一天是星期几
     *
     * @param date
     * @return
     */
    public static String getWeekday(long date) {
        String weekday = "";
        final String dayNames[] = {"周天", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        weekday = dayNames[dayOfWeek - 1];
        return weekday;
    }

    /**
     * @param dyear
     * @param dmouth
     * @return
     */
    public static int calDayByYearAndMonth(int dyear, int dmouth) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
        Calendar rightNow = Calendar.getInstance();
        try {
            rightNow.setTime(simpleDate.parse(dyear + "/" + dmouth));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);// 根据年月获取月份天数
    }

    public static String getTomorrow() {
        try {
            // 获取当前日期
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd ");
            String nowDate = sf.format(date);
            // 通过日历获取下一天日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(sf.parse(nowDate));
            cal.add(Calendar.DAY_OF_YEAR, 1);
            String nextDate_1 = sf.format(cal.getTime());
            return nextDate_1;
            // System.out.println(nextDate_1);
            // // 通过秒获取下一天日期
            // long time = (date.getTime() / 1000) + 60 * 60 * 24;// 秒
            // date.setTime(time * 1000);// 毫秒
            // String nextDate_2 = sf.format(date).toString();
            // System.out.println(nextDate_2);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "";
    }

    /**
     * 获取当前时间两个小时后的时间
     *
     * @return
     */
    public static String afterTwoHourToNowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 2);
        SimpleDateFormat df = new SimpleDateFormat("HH");
        return df.format(calendar.getTime());
    }

    /**
     * 两个时间相差距离多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long 返回值
     */
    public static long getDistanceTimes(String str1, String str2) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // long[] times = { day, hour, min, sec };
        Log.e("day", day + "--");
        long time = sec + min * 60 + hour * 60 * 60 + day * 24 * 60 * 60;
        return time;
    }
}
