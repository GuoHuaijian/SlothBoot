package com.sloth.boot.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * 日期工具类（门面）
 * <p>
 * 提供常用的日期时间操作方法，基于 {@code java.time} API。
 * 本类保留常用便捷方法，格式化、计算、边界、转换等操作请使用对应的专用工具类：
 * <ul>
 *   <li>{@link DateFormatUtil} — 格式化与解析</li>
 *   <li>{@link DateCalcUtil} — 日期加减与比较</li>
 *   <li>{@link DateBoundaryUtil} — 日期边界提取</li>
 *   <li>{@link DateConvertUtil} — 类型转换</li>
 * </ul>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class DateUtil {

    private DateUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static LocalDate nowDate() {
        return LocalDate.now();
    }

    /**
     * 根据生日计算年龄
     * <p>
     * <pre>
     * LocalDate birthday = LocalDate.of(1990, 6, 15);
     * int age = DateUtil.getAge(birthday); // 以当前日期为参考
     * </pre>
     *
     * @param birthday 生日
     * @return 年龄
     */
    public static int getAge(LocalDate birthday) {
        return getAge(birthday, LocalDate.now());
    }

    /**
     * 根据生日和参考日期计算年龄
     * <p>
     * <pre>
     * LocalDate birthday = LocalDate.of(1990, 6, 15);
     * LocalDate reference = LocalDate.of(2024, 1, 1);
     * int age = DateUtil.getAge(birthday, reference); // 33
     * </pre>
     *
     * @param birthday  生日
     * @param reference 参考日期
     * @return 年龄
     */
    public static int getAge(LocalDate birthday, LocalDate reference) {
        return Period.between(birthday, reference).getYears();
    }

    /**
     * 判断是否为周末（周六或周日）
     *
     * @param dateTime 日期时间
     * @return 是否为周末
     */
    public static boolean isWeekend(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    /**
     * 判断是否为工作日（周一到周五）
     *
     * @param dateTime 日期时间
     * @return 是否为工作日
     */
    public static boolean isWorkday(LocalDateTime dateTime) {
        return !isWeekend(dateTime);
    }

    /**
     * 获取星期几（1=周一，7=周日）
     *
     * @param dateTime 日期时间
     * @return 星期几
     */
    public static int dayOfWeek(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek().getValue();
    }
}
