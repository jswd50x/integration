package com.kang.common.util.http;
/**
 * @Desctiption API请求公共常数
 * @Date:2019/3/14 14:34
 * @Author:wk
 */
public class SdkConstParam {

    /**
     * 签名算法
     */
    public static final String SIGNATURE_METHOD = "HmacSHA256";
    /**
     * 字符集
     */
    public static final String CHARACTER_SET = "UTF-8";
    /**
     * 默认时间格式
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 当前版本号
     */
    public static final String CURRECT_VERSION = "1.0.0";
    /**
     * http内容类型
     */
    public static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    /**
     * 需要重试标识
     */
    public static final int SDK_NEED_RETRY_SEND = 1;
}
