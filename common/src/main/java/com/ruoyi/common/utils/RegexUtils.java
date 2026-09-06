package com.ruoyi.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则校验工具类
 *
 * @author ruoyi
 */
public class RegexUtils
{
    /** 手机号（简单匹配） */
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /** 邮箱 */
    private static final String EMAIL_REGEX = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    /** 身份证（18位） */
    private static final String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$";

    /** 固定电话 */
    private static final String LANDLINE_REGEX = "^0\\d{2,3}-?\\d{7,8}$";

    /** 邮政编码 */
    private static final String POSTAL_CODE_REGEX = "^[1-9]\\d{5}$";

    /** 网址URL */
    private static final String URL_REGEX = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";

    /** IP地址（IPv4） */
    private static final String IP_V4_REGEX = "^(?:\\d{1,3}\\.){3}\\d{1,3}$";

    /** 纯数字 */
    private static final String DIGITS_REGEX = "^\\d+$";

    /** 纯字母 */
    private static final String LETTERS_REGEX = "^[a-zA-Z]+$";

    /** 字母和数字 */
    private static final String LETTERS_DIGITS_REGEX = "^[a-zA-Z0-9]+$";

    /** 大写字母 */
    private static final String UPPER_CASE_REGEX = "^[A-Z]+$";

    /** 小写字母 */
    private static final String LOWER_CASE_REGEX = "^[a-z]+$";

    /** 中文 */
    private static final String CHINESE_REGEX = "^[\\u4e00-\\u9fa5]+$";

    /** 金额（保留2位小数） */
    private static final String MONEY_REGEX = "^(0|[1-9]\\d*)(\\.\\d{1,2})?$";

    /** 车牌号（新能源+普通） */
    private static final String PLATE_NUMBER_REGEX = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤川青藏琼宁][A-HJ-NP-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$";

    /**
     * 匹配手机号
     */
    public static boolean isPhone(String value)
    {
        return matches(PHONE_REGEX, value);
    }

    /**
     * 匹配邮箱
     */
    public static boolean isEmail(String value)
    {
        return matches(EMAIL_REGEX, value);
    }

    /**
     * 匹配身份证号
     */
    public static boolean isIdCard(String value)
    {
        return matches(ID_CARD_REGEX, value);
    }

    /**
     * 匹配固定电话
     */
    public static boolean isLandline(String value)
    {
        return matches(LANDLINE_REGEX, value);
    }

    /**
     * 匹配邮政编码
     */
    public static boolean isPostalCode(String value)
    {
        return matches(POSTAL_CODE_REGEX, value);
    }

    /**
     * 匹配URL
     */
    public static boolean isUrl(String value)
    {
        return matches(URL_REGEX, value);
    }

    /**
     * 匹配IPv4地址
     */
    public static boolean isIpV4(String value)
    {
        if (!matches(IP_V4_REGEX, value))
        {
            return false;
        }
        String[] parts = value.split("\\.");
        for (String part : parts)
        {
            int num = Integer.parseInt(part);
            if (num < 0 || num > 255)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为纯数字
     */
    public static boolean isDigits(String value)
    {
        return matches(DIGITS_REGEX, value);
    }

    /**
     * 是否为纯字母
     */
    public static boolean isLetters(String value)
    {
        return matches(LETTERS_REGEX, value);
    }

    /**
     * 是否为字母和数字组合
     */
    public static boolean isLettersAndDigits(String value)
    {
        return matches(LETTERS_DIGITS_REGEX, value);
    }

    /**
     * 是否为纯大写字母
     */
    public static boolean isUpperCase(String value)
    {
        return matches(UPPER_CASE_REGEX, value);
    }

    /**
     * 是否为纯小写字母
     */
    public static boolean isLowerCase(String value)
    {
        return matches(LOWER_CASE_REGEX, value);
    }

    /**
     * 是否为纯中文
     */
    public static boolean isChinese(String value)
    {
        return matches(CHINESE_REGEX, value);
    }

    /**
     * 匹配金额格式
     */
    public static boolean isMoney(String value)
    {
        return matches(MONEY_REGEX, value);
    }

    /**
     * 匹配车牌号
     */
    public static boolean isPlateNumber(String value)
    {
        return matches(PLATE_NUMBER_REGEX, value);
    }

    /**
     * 长度校验：最小长度
     */
    public static boolean minLength(String value, int min)
    {
        return value != null && value.length() >= min;
    }

    /**
     * 长度校验：最大长度
     */
    public static boolean maxLength(String value, int max)
    {
        return value != null && value.length() <= max;
    }

    /**
     * 长度校验：范围
     */
    public static boolean betweenLength(String value, int min, int max)
    {
        return value != null && value.length() >= min && value.length() <= max;
    }

    /**
     * 是否包含中文
     */
    public static boolean containsChinese(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return false;
        }
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher m = p.matcher(value);
        return m.find();
    }

    /**
     * 是否包含特殊字符
     */
    public static boolean containsSpecialChars(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return false;
        }
        Pattern p = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]");
        Matcher m = p.matcher(value);
        return m.find();
    }

    /**
     * 通用正则匹配
     */
    public static boolean matches(String regex, String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return false;
        }
        return Pattern.matches(regex, value);
    }

    /**
     * 通用正则匹配（可指定 flags）
     */
    public static boolean matches(String regex, String value, int flags)
    {
        if (StringUtils.isEmpty(value))
        {
            return false;
        }
        return Pattern.compile(regex, flags).matcher(value).matches();
    }
}
