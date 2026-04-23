package com.sloth.boot.starter.web.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 枚举值校验器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private Set<Integer> intValues;
    private Set<String> strValues;

    /**
     * 初始化校验器。
     *
     * @param constraintAnnotation 注解信息
     */
    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.intValues = Arrays.stream(constraintAnnotation.intValues()).boxed().collect(Collectors.toSet());
        this.strValues = Arrays.stream(constraintAnnotation.strValues()).collect(Collectors.toSet());
    }

    /**
     * 执行枚举值校验。
     *
     * @param value   待校验值
     * @param context 校验上下文
     * @return 是否合法
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value instanceof Number number && !intValues.isEmpty()) {
            return intValues.contains(number.intValue());
        }
        if (value instanceof CharSequence charSequence && !strValues.isEmpty()) {
            return strValues.contains(charSequence.toString());
        }
        return intValues.isEmpty() && strValues.isEmpty();
    }
}
