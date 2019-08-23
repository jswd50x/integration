package com.kang.common.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * @Desctiption http服务类
 * @Date:2019/3/14 14:34
 * @Author:wk
 */
public class HttpProxy<T extends BaseRequestParam> {

    private static final String MODULUS = "";
    private static final String ACCESS_KEY_TAIL = "&";

    private static final Logger logger = LoggerFactory.getLogger(HttpProxy.class);
    /**
     * API网关对应的URL
     */
    private final String URI;
    /**
     * 接收超时默认20秒
     */
    private final int RECEIVE_TIME_OUT;
    /**
     * 连接超时默认5秒
     */
    private final int CONNECT_TIME_OUT;

    /**
     * http客户端代理构造器
     *
     * @param gateway 网关地址
     */
    public HttpProxy(String gateway) {
        this(gateway, 5000, 20000);
    }

    /**
     * http客户端代理构造器
     *
     * @param gateway        网关地址
     * @param connectTimeOut 链接超时（毫秒）
     * @param receiveTimeOut 接收超时（毫秒）
     */
    public HttpProxy(String gateway,
                     int connectTimeOut, int receiveTimeOut) {
        URI = gateway;
        RECEIVE_TIME_OUT = receiveTimeOut;
        CONNECT_TIME_OUT = connectTimeOut;
    }

    /**
     * http客户端代理，执行get方法
     *
     * @return api请求的返回结果
     * @throws IOException 在网络通讯过程中，出现的IO异常
     */
    public String doGet(String params) {

        HttpGet httpGet = new HttpGet(URI);
        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIME_OUT)
                    .setSocketTimeout(RECEIVE_TIME_OUT).build();
            httpGet.setURI(new URI(URI + "?" + params));
            httpGet.setConfig(requestConfig);
            String apiResult = httpExecute(httpGet);
            if (null == apiResult) {
                return null;
            }
            return apiResult;
        } catch (Exception ex) {
            logger.error("请求异常Exception:{}", ex);
            return null;
        }
    }

    public String doPost(String params) {

        HttpPost post = new HttpPost(URI);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIME_OUT)
                .setSocketTimeout(RECEIVE_TIME_OUT).build();
        post.setConfig(requestConfig);
        try {
            StringEntity requestEntity = new StringEntity(params, Charset.forName(SdkConstParam.CHARACTER_SET));
            requestEntity.setContentType(SdkConstParam.CONTENT_TYPE);
            post.setEntity(requestEntity);
            String apiResult = httpExecute(post);
            if (null == apiResult) {
                return null;
            }
            return apiResult;
        } catch (Exception ex) {
            logger.error("请求异常Exception:{}", ex);
            return null;
        }

    }

    private String httpExecute(HttpRequestBase httpRequestBase) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(httpRequestBase);
        try {
            // 获取响应实体
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String body = EntityUtils.toString(responseEntity);
                return body;
            }
        } catch (Exception e) {
            logger.error("http请求发送失败", e);
            throw e;
        } finally {
            response.close();
            client.close();
        }
        return null;
    }
}
