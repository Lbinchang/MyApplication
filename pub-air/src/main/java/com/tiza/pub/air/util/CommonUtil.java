package com.tiza.pub.air.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: CommonUtil
 * Author: DIYILIU
 * Update: 2019-06-27 19:33
 */

@Slf4j
public class CommonUtil {

    public static String bytes2BinaryStr(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();
        for (byte b : bytes) {
            strBuf.append(byte2BinaryStr(b));
        }
        return strBuf.toString();
    }

    /**
     * 字节转二进制字符串
     *
     * @param b
     * @return
     */
    public static String byte2BinaryStr(byte b) {
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            int value = (b >> i) & 0x01;
            strBuf.append(value);
        }

        return strBuf.toString();
    }

    public static double keepDecimal(double d, int digit) {
        return keepDecimal(d, 1, digit);
    }

    /**
     * 保留小数
     *
     * @param num
     * @param precision
     * @param digit
     * @return
     */
    public static double keepDecimal(Number num, double precision, int digit) {
        BigDecimal decimal = new BigDecimal(String.valueOf(num));
        decimal = decimal.multiply(new BigDecimal(precision)).setScale(digit, BigDecimal.ROUND_HALF_UP);

        return decimal.doubleValue();
    }
    /**
     * 创建时间，修改对应时间
     *
     * @param bytes
     * @return
     */
    public static Date bytesToDate(byte[] bytes) {
        if (bytes.length == 3 || bytes.length == 6) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(0));
            toDate(calendar, bytes);

            return calendar.getTime();
        }

        return null;
    }

    public static void toDate(Calendar calendar, byte[] bytes) {
        calendar.set(Calendar.YEAR, 2000 + bytes[0]);
        calendar.set(Calendar.MONTH, bytes[1] - 1);
        calendar.set(Calendar.DAY_OF_MONTH, bytes[2]);
        if (bytes.length == 6) {

            calendar.set(Calendar.HOUR_OF_DAY, bytes[3]);
            calendar.set(Calendar.MINUTE, bytes[4]);
            calendar.set(Calendar.SECOND, bytes[5]);

        } else if (bytes.length == 3) {

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
    }


    public static byte[] hexStringToBytes(String hex) {

        char[] charArray = hex.toCharArray();

        if (charArray.length % 2 != 0) {
            // 无法转义
            return null;
        }

        int length = charArray.length / 2;
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {

            String b = new String(new char[]{charArray[i * 2], charArray[i * 2 + 1]});
            bytes[i] = (byte) Integer.parseInt(b, 16);
        }

        return bytes;
    }


    public static byte[] dateToBytes(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR) - 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return new byte[]{(byte) year, (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) second};
    }



    public static int getNoSin(byte b) {
        if (b >= 0) {
            return b;
        } else {
            return 256 + b;
        }
    }

    public static String bytesToStr(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (byte a : bytes) {
            buf.append(String.format("%02X", getNoSin(a)));
        }

        return buf.toString();
    }

    public static String bytesToString(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (byte a : bytes) {
            buf.append(String.format("%02X", getNoSin(a))).append(" ");
        }

        return buf.substring(0, buf.length() - 1);
    }

    /**
     * 异或校验
     *
     * @param bytes
     * @return
     */
    public static byte checkCode(byte[] bytes) {
        byte b = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            b ^= bytes[i];
        }

        return b;
    }

    public static byte checkCode(byte[] bytes, int index, int length) {
        byte b = bytes[index];
        for (int i = 1; i < length; i++) {
            b ^= bytes[index + i];
        }

        return b;
    }


    /**
     * 下划线转驼峰
     * @param str
     * @return
     */
    public static String camel(String str) {
        //利用正则删除下划线，把下划线后一位改成大写
        Pattern pattern = Pattern.compile("_(\\w)");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if(matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        }else {
            return sb.toString();
        }

        return camel(sb.toString());
    }


    /**
     * 驼峰转下划线
     * @param str
     * @return
     */
    public static String underline(String str) {
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if(matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb,"_"+matcher.group(0).toLowerCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        }else {
            return sb.toString();
        }

        return underline(sb.toString());
    }





}
