package com.kang.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

public class StringUtil
{
  private static final Logger log = LoggerFactory.getLogger(StringUtil.class);
  public static final String DEFAULT_CHARSET = "UTF-8";
  public static final String DEFAULT_PARAM_TYPE = "ALL";
  public static final String DEFAULT_ZERO = "0";
  public static final String DEFAULT_ZERO_POINT_1 = "0.0";
  public static final String DEFAULT_ZERO_POINT_2 = "0.00";
  private static char[] LETTER = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
  private static int[] NUMBER = { 2, 3, 4, 5, 6, 7, 8, 9 };
  private static char[] SPECIAL = { '!', '@', '#', '$', '%', '^', '&', '*' };
  
  public static String byte2String(byte[] src)
  {
    return byte2String(src, "UTF-8");
  }
  
  public static String byte2String(byte[] src, String charset)
  {
    String res = null;
    try
    {
      if (src == null)
      {
        res = "";
      }
      else
      {
        if (StringUtils.isBlank(charset)) {
          charset = "UTF-8";
        }
        res = new String(src, charset);
      }
    }
    catch (Exception ex)
    {
      log.info(ex.getMessage(), ex);
      throw new RuntimeException("byte������String����");
    }
    return res;
  }
  
  public static byte[] string2Byte(String src)
  {
    return string2Byte(src, "UTF-8");
  }
  
  public static byte[] string2Byte(String src, String charset)
  {
    byte[] res = null;
    try
    {
      if (src == null) {
        src = "";
      }
      if (StringUtils.isBlank(charset)) {
        charset = "UTF-8";
      }
      res = src.getBytes(charset);
    }
    catch (Exception ex)
    {
      log.info(ex.getMessage(), ex);
      throw new RuntimeException("��������byte��������");
    }
    return res;
  }
  
  public static String toString(Object obj)
  {
    String res = "";
    if (obj != null) {
      res = String.valueOf(obj);
    }
    return res;
  }
  
  public static String toString(Object[] objs)
  {
    if (objs == null) {
      return null;
    }
    return Arrays.toString(objs);
  }
  
  public static String getStringRandomWithOutSpecial(int length)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++)
    {
      int type = getRandomInt(2);
      if (type == 0)
      {
        sb.append(NUMBER[getRandomInt(8)]);
      }
      else if (type == 1)
      {
        int temp = getRandomInt(2) % 2 == 0 ? 0 : 32;
        sb.append((char)(LETTER[getRandomInt(23)] + temp));
      }
    }
    return sb.toString();
  }
  
  public static String getStringRandomWithSpecial(int length)
  {
    StringBuffer sb = new StringBuffer();
    
    int specialCount = 3;
    for (int i = 0; i < length; i++)
    {
      int type = getRandomInt(3);
      if (type == 0)
      {
        specialCount--;
        if (specialCount <= 0)
        {
          i--;
          continue;
        }
      }
      if ((type == 0) && (specialCount > 0))
      {
        sb.append(SPECIAL[getRandomInt(8)]);
      }
      else if (type == 1)
      {
        sb.append(NUMBER[getRandomInt(8)]);
      }
      else if (type == 2)
      {
        int temp = getRandomInt(2) % 2 == 0 ? 0 : 32;
        sb.append((char)(LETTER[getRandomInt(23)] + temp));
      }
    }
    return sb.toString();
  }
  
  public static String getStringRandom(int length)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++)
    {
      String charOrNum = getRandomInt(2) % 2 == 0 ? "char" : "num";
      if ("char".equalsIgnoreCase(charOrNum))
      {
        int temp = getRandomInt(2) % 2 == 0 ? 65 : 97;
        sb.append((char)(getRandomInt(26) + temp));
      }
      else if ("num".equalsIgnoreCase(charOrNum))
      {
        sb.append(String.valueOf(getRandomInt(10)));
      }
    }
    return sb.toString();
  }
  
  public static String getIntRandom(int length)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      sb.append(getRandomInt(10));
    }
    return sb.toString();
  }
  
  public static String paramAllTypeToNull(String type)
  {
    if ((StringUtils.isNotBlank(type)) && ("ALL".equals(type))) {
      return null;
    }
    return type;
  }
  
  public static boolean toBoolean(String v)
  {
    if (StringUtils.isBlank(v)) {
      return false;
    }
    if ((!"true".equals(v)) && (!"false".equals(v))) {
      return false;
    }
    return Boolean.valueOf(v).booleanValue();
  }
  
  public static Integer toInteger(String value)
  {
    if (StringUtils.isNotBlank(value))
    {
      if (value.matches("^\\d+$")) {
        return Integer.valueOf(value);
      }
      return Integer.valueOf(0);
    }
    return Integer.valueOf(0);
  }
  
  public static Long toLong(String value)
  {
    if (StringUtils.isNotBlank(value))
    {
      if (value.matches("^\\d+$")) {
        return Long.valueOf(value);
      }
      return Long.valueOf(0L);
    }
    return Long.valueOf(0L);
  }
  
  public static Double toDouble(String value)
  {
    if (StringUtils.isNotBlank(value)) {
      return new Double(value);
    }
    return Double.valueOf(0.0D);
  }
  
  public static int getRandomInt(int bound)
  {
    int rand;
    if (bound <= 0) {
      rand = new Random().nextInt();
    } else {
      rand = new Random().nextInt(bound);
    }
    return rand;
  }
  
  public static String removeFloatZero(String s)
  {
    if ((s == null) || (s.length() < 1)) {
      return "";
    }
    double num = Double.parseDouble(s);
    NumberFormat formater;
    if (!s.contains("."))
    {
      formater = new DecimalFormat("#");
    }
    else
    {
      StringBuffer buff = new StringBuffer();
      buff.append("#.");
      for (int i = 0; i < s.length(); i++) {
        buff.append("#");
      }
      formater = new DecimalFormat(buff.toString());
    }
    return formater.format(num);
  }
  
  public static String trimAll(String str)
  {
    if (StringUtils.isBlank(str)) {
      return null;
    }
    return str.replaceAll(" ", "");
  }
}
