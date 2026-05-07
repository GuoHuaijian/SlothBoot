-- Sloth Boot 示例数据库初始化脚本
-- 适用于 MySQL 8.0+

CREATE DATABASE IF NOT EXISTS `sloth_boot` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `sloth_boot`;

-- ==================== 系统用户表 ====================
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '密码',
    `nickname` VARCHAR(64) DEFAULT '' COMMENT '昵称',
    `email` VARCHAR(128) DEFAULT '' COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
    `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
    `tenant_id` VARCHAR(32) DEFAULT '' COMMENT '租户ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态（0-禁用 1-正常）',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-正常 1-已删除）',
    `version` INT DEFAULT 1 COMMENT '乐观锁',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB COMMENT='系统用户表';

-- ==================== 系统部门表 ====================
CREATE TABLE IF NOT EXISTS `sys_dept` (
    `id` BIGINT NOT NULL COMMENT '部门ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID',
    `name` VARCHAR(64) NOT NULL COMMENT '部门名称',
    `ancestors` VARCHAR(512) DEFAULT '' COMMENT '祖级列表',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='系统部门表';

-- 插入示例数据
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `ancestors`, `sort`) VALUES
(1, 0, 'Sloth Boot', '0', 1),
(2, 1, '研发部', '0,1', 1),
(3, 1, '市场部', '0,1', 2);

INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `dept_id`) VALUES
(1, 'admin', '', '管理员', 'admin@sloth-boot.com', 2),
(2, 'user', '', '普通用户', 'user@sloth-boot.com', 3);
