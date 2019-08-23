package com.kang.common.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class PercentUtils
{
  public static String toNumber(String value)
  {
    return toNumber(value, 6);
  }
  
  public static String toNumber(String value, int scale)
  {
    if (StringUtils.isBlank(value)) {
      return null;
    }
    return StringUtil.toString(toNumber(new BigDecimal(value), scale));
  }
  
  public static BigDecimal toNumber(BigDecimal value)
  {
    return toNumber(value, 6);
  }
  
  public static BigDecimal toNumber(BigDecimal value, int scale)
  {
    if (value == null) {
      return null;
    }
    return DecimalUtils.divide(value, DecimalUtils.HUNDRED, scale, 1);
  }
  
  public static String toPercent(String _v)
  {
    if (StringUtils.isBlank(_v)) {
      return null;
    }
    return StringUtil.toString(toPercent(new BigDecimal(_v)));
  }
  
  public static BigDecimal toPercent(BigDecimal value)
  {
    if (value == null) {
      return null;
    }
    return DecimalUtils.multiply(value, DecimalUtils.HUNDRED);
  }
}
