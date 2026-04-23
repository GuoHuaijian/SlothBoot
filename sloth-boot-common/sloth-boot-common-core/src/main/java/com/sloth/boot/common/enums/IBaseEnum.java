/*
 * Copyright 2025 Sloth Boot
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sloth.boot.common.enums;

/**
 * 基础枚举接口
 * <p>
 * 所有业务枚举都应实现此接口，提供 code 和 desc 方法
 * </p>
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public interface IBaseEnum {

    /**
     * 获取枚举值
     *
     * @return 枚举值
     */
    int getCode();

    /**
     * 获取枚举描述
     *
     * @return 枚举描述
     */
    String getDesc();

    /**
     * 根据枚举值获取枚举实例
     *
     * @param clazz 枚举类
     * @param code  枚举值
     * @param <E>   枚举类型
     * @return 枚举实例
     */
    static <E extends Enum<E> & IBaseEnum> E fromCode(Class<E> clazz, int code) {
        if (clazz == null) {
            throw new IllegalArgumentException("枚举类不能为空");
        }

        for (E e : clazz.getEnumConstants()) {
            if (e.getCode() == code) {
                return e;
            }
        }

        throw new IllegalArgumentException("枚举类 " + clazz.getName() + " 中没有值为 " + code + " 的枚举");
    }
}
