package com.bzdnet.demo.df.util;

public class StringUtils {

    private static final String UNDERSCORE = "_";

    /**
     * 下划线格式转驼峰格式
     *
     * @param underscoreString 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String underscore2CamelCase(String underscoreString) {
        StringBuilder sb = new StringBuilder();
        String[] arr = underscoreString.split(UNDERSCORE);
        for (String str : arr) {
            sb.append(str.toLowerCase().substring(0, 1).toUpperCase());
            sb.append(str.toUpperCase().substring(1));
        }
        return sb.toString().substring(0, 1).toLowerCase() + sb.toString().substring(1);
    }

    /**
     * 驼峰格式转换为下划线格式，忽略首字母
     *
     * @param camelString 需要转换的字符串
     * @return 转换后的字符串
     */
    public static String camel2UnderscoreCase(String camelString) {
        return camel2UnderscoreCase(camelString, true);
    }

    /**
     * 驼峰格式转换为下划线格式
     *
     * @param camelString 需要转换的字符串
     * @param ignoreFirst 是否忽略首字母
     * @return 转换后的字符串
     */
    public static String camel2UnderscoreCase(String camelString, boolean ignoreFirst) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelString.length(); i++) {
            if (Character.isUpperCase(camelString.charAt(i))) {
                if (i == 0 && ignoreFirst) {
                    break;
                }
                sb.append(UNDERSCORE);
            }
            sb.append(camelString.charAt(i));
        }
        return sb.toString().toLowerCase();
    }

}
