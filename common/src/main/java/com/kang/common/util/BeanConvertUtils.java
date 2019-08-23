package com.kang.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wk
 * @version $Id: BeanConvertUtils.java, v 0.1 2019/7/23 18:01 by Exp $$
 * @Description 实体转换
 */
public class BeanConvertUtils {

    /**
     * 对象转换
     *
     * @param source 原数据
     * @param clazz  目标类
     * @return 入参不合法时返回null
     */
    public static <T> T convert(Object source, Class<T> clazz) {
        if (source == null || clazz == null) {
            return null;
        }
        try {
            T target = clazz.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {

            throw new RuntimeException("对象转换异常", e);
        }
    }

    /**
     * 对象list转换
     *
     * @param sources 原数据
     * @param clazz   目标类
     * @return 入参不合法时返回空集合
     */
    public static <T> List<T> convertList(List<?> sources, Class<T> clazz) {
        if (CollectionUtils.isEmpty(sources) || clazz == null) {
            return Collections.emptyList();
        }
        List<T> targets = new ArrayList<>();
        sources.stream().forEach(source -> targets.add(convert(source, clazz)));
        return targets;
    }

}
