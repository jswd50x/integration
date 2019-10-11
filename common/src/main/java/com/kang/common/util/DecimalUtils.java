package com.kang.common.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class DecimalUtils {
    public static final BigDecimal HUNDRED = new BigDecimal(100);

    public static boolean isNull(BigDecimal b) {
        if (b == null) {
            return true;
        }
        return false;
    }

    public static boolean isNotNull(BigDecimal b) {
        if (b == null) {
            return false;
        }
        return true;
    }

    public static int compareToZero(BigDecimal targetB) {
        return compareToZero(targetB, false);
    }

    public static int compareToZero(BigDecimal targetB, boolean isNullFlag) {
        int res = 999;
        if (targetB == null) {
            if (isNullFlag) {
                throw new RuntimeException("������������");
            }
            targetB = BigDecimal.ZERO;
        }
        if (targetB.compareTo(BigDecimal.ZERO) == 0) {
            res = 0;
        } else if (targetB.compareTo(BigDecimal.ZERO) > 0) {
            res = 1;
        } else if (targetB.compareTo(BigDecimal.ZERO) < 0) {
            res = -1;
        }
        return res;
    }

    public static int compare(String s1, String s2) {
        if ((StringUtils.isBlank(s1)) || (StringUtils.isBlank(s2))) {
            throw new RuntimeException("����������������");
        }
        return compare(toDecimal(s1), toDecimal(s2));
    }

    public static int compare(BigDecimal b1, BigDecimal b2) {
        if ((b1 == null) || (b2 == null)) {
            throw new RuntimeException("����������������");
        }
        if (b1.compareTo(b2) == 0) {
            return 0;
        }
        if (b1.compareTo(b2) > 0) {
            return 1;
        }
        if (b1.compareTo(b2) < 0) {
            return -1;
        }
        return 999;
    }

    public static String scaleStr(String val) {
        if (StringUtils.isBlank(val)) {
            return null;
        }
        return scaleStr(new BigDecimal(val));
    }

    public static String scaleStr(BigDecimal _b1) {
        return StringUtil.toString(scale(_b1));
    }

    public static BigDecimal scale(BigDecimal _b1) {
        return scale(_b1, 2, 4);
    }

    public static BigDecimal scale(String val) {
        if (StringUtils.isBlank(val)) {
            return null;
        }
        return scale(new BigDecimal(val));
    }

    public static BigDecimal scale(BigDecimal _b1, int scale, int mode) {
        if (_b1 == null) {
            return null;
        }
        return _b1.setScale(scale, mode);
    }

    public static BigDecimal add(BigDecimal... bs) {
        BigDecimal res = BigDecimal.ZERO;
        for (BigDecimal b : bs) {
            if (b == null) {
                b = BigDecimal.ZERO;
            }
            res = res.add(b);
        }
        return res;
    }

    public static BigDecimal subtract(BigDecimal b1, BigDecimal b2) {
        if (b1 == null) {
            b1 = BigDecimal.ZERO;
        }
        if (b2 == null) {
            b2 = BigDecimal.ZERO;
        }
        return b1.subtract(b2);
    }

    public static BigDecimal multiply(BigDecimal b1, BigDecimal b2) {
        if (b1 == null) {
            b1 = BigDecimal.ZERO;
        }
        if (b2 == null) {
            b2 = BigDecimal.ZERO;
        }
        return b1.multiply(b2);
    }

    public static BigDecimal divide(BigDecimal b1, BigDecimal b2) {
        return divide(b1, b2, 2, 4);
    }

    public static BigDecimal divide(BigDecimal b1, BigDecimal b2, int scale) {
        return divide(b1, b2, scale, 4);
    }

    public static BigDecimal divide(BigDecimal b1, BigDecimal b2, int scale, int mode) {
        if (b1 == null) {
            b1 = BigDecimal.ZERO;
        }
        if ((b2 == null) || (compareToZero(b2) == 0)) {
            b2 = BigDecimal.ONE;
        }
        BigDecimal res = b1.divide(b2, scale, mode);
        return res;
    }

    public static Integer toInteger(BigDecimal b) {
        int i = 0;
        if (b != null) {
            i = b.intValueExact();
        }
        return Integer.valueOf(i);
    }

    public static Long toLong(BigDecimal b) {
        long l = 0L;
        if (b != null) {
            l = b.longValueExact();
        }
        return Long.valueOf(l);
    }

    public static BigDecimal toDecimal(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return new BigDecimal(value);
    }

    public static BigDecimal toDecimal(Integer value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value.intValue());
    }

    public static BigDecimal toDecimal(Long value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value.longValue());
    }
}
