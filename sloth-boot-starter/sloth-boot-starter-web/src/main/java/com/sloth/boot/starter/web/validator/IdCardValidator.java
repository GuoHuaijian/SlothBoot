package com.sloth.boot.starter.web.validator;

import com.sloth.boot.common.util.ValidateUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 身份证校验器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class IdCardValidator implements ConstraintValidator<IdCard, String> {

    /**
     * 执行身份证校验。
     *
     * @param value   待校验值
     * @param context 校验上下文
     * @return 是否合法
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.isBlank() || ValidateUtil.isIdCard(value);
    }
}
