package com.imputation.cromwell.utils;

/**
 * 字符串工具类
 */
public class StringUtils {
    /**
     * 字符串为空判断
     * @param str
     * @return
     */
    public static Boolean isEmpty(String str){
        return null == str || "".equals(str);
    }

    /**
     * 字符串不为空
     * @param str
     * @return
     */
    public static Boolean isNotEmpty(String str){
        return null != str && !"".equals(str);
    }

    /**
     * 加密证件号
     * @param str
     * @return
     */
    public static String asteriskByIdNo(String str) {
        if (org.springframework.util.StringUtils.hasText(str)) {
            str = str.trim();
            String star2;
            int i;
            if (str.length() == 18) {
                star2 = "";
//                1101011**********0
                for(i = 0; i < str.length() - 10; ++i) {
                    star2 = new StringBuilder().append(star2).append("*").toString();
                }

                str = str.substring(0, 6) + star2 + str.substring(str.length() - 4);
            } else if (str.length() == 15) {
                star2 = "";
//              130503 670401 001的含义; 13为河北，05为邢台，
//              03为桥西区，出生日期为1967年4月1日，顺序号为001。
                for(i = 0; i < str.length() - 9; ++i) {
                    star2 = star2 + "*";
                }

                str = str.substring(0, 6) + star2 + str.substring(str.length() - 3);
            }
        }

        return str;
    }

    public static String asteriskByMobile(String str) {
        if (org.springframework.util.StringUtils.hasText(str)) {
            str = str.trim();
            if (str.length() >= 11) {
                str = str.substring(0, 3) + "****" + str.substring(str.length() - 4);
            } else {
                String star2 = "";
                for(int i = 0; i < str.length() - 1; ++i) {
                    star2 = star2 + "*";
                }
                str = str.substring(0, 1) + star2;
            }
        }

        return str;
    }

    public static String asteriskByName(String str) {
        if (org.springframework.util.StringUtils.hasText(str)) {
            str = str.trim();
            if (str.length() >= 3) {
                String star2 = "";
                for(int i = 0; i < str.length() - 2; ++i) {
                    star2 = star2 + "*";
                }
                str = str.substring(0, 1) + star2 + str.substring(str.length() - 1, str.length());
            } else {
                str = str.substring(0, 1) + "*";
            }
        }

        return str;
    }


}
