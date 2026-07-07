package com.sloth.boot.example.observability.infrastructure.model.po.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo_user")
public class DemoUser extends BaseEntity {

    private String name;

    private String email;

    private String role;
}
