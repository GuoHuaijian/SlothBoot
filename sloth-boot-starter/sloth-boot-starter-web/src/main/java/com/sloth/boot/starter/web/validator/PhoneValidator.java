package com.sloth.boot.starter.web.validator;

import com.sloth.boot.common.util.ValidateUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 手机号校验器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    /**
     * 执行手机号校验。
     *
     * @param value   待校验值
     * @param context 校验上下文
     * @return 是否合法
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.isBlank() || ValidateUtil.isMobile(value);
    }
}
