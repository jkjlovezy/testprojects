package com.focustech.gateway.site.util;

public class StringUtils {
    /**
     * 判断两个字符串的值是否相等
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean isEqual(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equals(s2);
    }

    /**
     * 模糊路径的prefix
     *
     * @param path 举例：XXX/test, XXX/test/**, XXX/test/
     * @return 返回: XXX/test
     */
    public static String parsePrefixPath(String path) {
        int i = path.length() - 1;
        for (; i >= 0; i--) {
            if (path.charAt(i) != '/' && path.charAt(i) != '*') {
                break;
            }
        }
        return path.substring(0, i + 1);
    }

    /**
     * 统计keyword在text中出现的次数。
     *
     * @param text
     * @param keyword
     * @return
     */
    public static int appearNums(String text, String keyword) {
        int count = 0;
        int leng = text.length();
        int j = 0;
        for (int i = 0; i < leng; i++) {
            if (text.charAt(i) == keyword.charAt(j)) {
                j++;
                if (j == keyword.length()) {
                    count++;
                    j = 0;
                }
            } else {
                i = i - j;// should rollback when not match
                j = 0;
            }
        }
        return count;
    }
}
