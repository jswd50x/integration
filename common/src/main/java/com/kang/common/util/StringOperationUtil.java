/**
 * BEYONDSOFT.COM INC
 */
package com.kang.common.util;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 特殊符号转译
 *
 * @author yulijun
 * @version $Id: StringOperationUtil.java, v 0.1 2017/10/18 15:03 yulijun Exp $$
 */
public class StringOperationUtil {

    // 需要转义的特殊字符
    private static final String[] SEARCHSTR = {"\"", "\\\"", "\\", "\\\\"};
    // 转移后的字符串
    private static final String[] REPLACEMENT = {"%20#", "%21#", "%22#", "%23#"};

    /**
     * sql查询，特殊符号转译：%,_
     *
     * @param str
     * @return
     */
    public static String sqlReplace(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String resultStr = str;
        if (StringUtils.contains(resultStr, "_")) {
            resultStr = StringUtils.replace(resultStr, "_", "\\_");
        }
        if (StringUtils.contains(resultStr, "%")) {
            resultStr = StringUtils.replace(resultStr, "%", "\\%");
        }
        resultStr = resultStr.trim();
        return resultStr;
    }

    /**
     * 特殊字符转义，支持 \  "
     *
     * @param str
     * @return
     */
    public static String replaceAll(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        // 双引号转义
        return StringUtils.replaceEach(str, SEARCHSTR, REPLACEMENT);
    }

    /**
     * 把转义的字符解析成页面回显字符
     *
     * @param str
     * @return
     */
    public static String parseAll(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        return StringUtils.replaceEach(str, REPLACEMENT, SEARCHSTR);
    }

    /**
     * 过滤html标签
     */
    public static String Html2Text(String inputString) {
        //含html标签的字符串
        String htmlStr = inputString;
        String textStr = "";
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;
        try {
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            String regEx_html = "<[^>]+>";
            //定义HTML标签的正则表达式
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            //过滤script标签
            htmlStr = m_script.replaceAll("");
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            //过滤style标签
            htmlStr = m_style.replaceAll("");
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            //过滤html标签
            htmlStr = m_html.replaceAll("");
            textStr = htmlStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回文本字符串
        return textStr;
    }

//    public static boolean isJson(String content) {
//        try {
//            JSON.parseObject(content);
//            return true;
//        } catch (JsonParseException e) {
//            return false;
//        }
//    }
}
