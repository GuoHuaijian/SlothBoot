-- ============================================================
-- Sloth Boot 示例 - H2 数据库初始化（兼容 MySQL 语法）
-- ============================================================

-- 部门表：演示 TreeUtil 树结构、数据权限、自动填充
CREATE TABLE IF NOT EXISTS sys_dept (
    id          BIGINT       PRIMARY KEY COMMENT '部门ID（雪花算法生成）',
    name        VARCHAR(64)  NOT NULL    COMMENT '部门名称',
    parent_id   BIGINT       DEFAULT 0   COMMENT '父部门ID（0表示顶级）',
    sort        INT          DEFAULT 0   COMMENT '显示排序',
    leader      VARCHAR(64)              COMMENT '负责人',
    status      TINYINT      DEFAULT 0   COMMENT '状态（0-正常, 1-停用）',
    ancestors   VARCHAR(512) DEFAULT ''  COMMENT '祖级列表（逗号分隔，如 0,1,2）',
    create_by   VARCHAR(64)              COMMENT '创建人（自动填充）',
    create_time TIMESTAMP                COMMENT '创建时间（自动填充）',
    update_by   VARCHAR(64)              COMMENT '更新人（自动填充）',
    update_time TIMESTAMP                COMMENT '更新时间（自动填充）',
    deleted     TINYINT      DEFAULT 0   COMMENT '逻辑删除标记（0-正常, 1-已删除）',
    version     INT          DEFAULT 1   COMMENT '乐观锁版本号'
);

-- 用户表：演示 EncryptTypeHandler、JsonTypeHandler、数据权限
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       PRIMARY KEY COMMENT '用户ID（雪花算法生成）',
    dept_id     BIGINT                   COMMENT '所属部门ID',
    username    VARCHAR(64)  NOT NULL    COMMENT '用户名',
    phone       VARCHAR(128)             COMMENT '手机号（AES 加密存储）',
    id_card     VARCHAR(128)             COMMENT '身份证号（AES 加密存储）',
    email       VARCHAR(128)             COMMENT '邮箱',
    gender      TINYINT      DEFAULT 0   COMMENT '性别（0-未知, 1-男, 2-女）',
    status      TINYINT      DEFAULT 0   COMMENT '状态（0-正常, 1-停用）',
    extra_info  TEXT                     COMMENT '扩展信息（JSON 格式存储）',
    create_by   VARCHAR(64)              COMMENT '创建人（自动填充）',
    create_time TIMESTAMP                COMMENT '创建时间（自动填充）',
    update_by   VARCHAR(64)              COMMENT '更新人（自动填充）',
    update_time TIMESTAMP                COMMENT '更新时间（自动填充）',
    deleted     TINYINT      DEFAULT 0   COMMENT '逻辑删除标记（0-正常, 1-已删除）',
    version     INT          DEFAULT 1   COMMENT '乐观锁版本号'
);

-- 商品表：演示缓存策略、布隆过滤器、XSS 清洗
CREATE TABLE IF NOT EXISTS product (
    id          BIGINT       PRIMARY KEY COMMENT '商品ID（雪花算法生成）',
    name        VARCHAR(128) NOT NULL    COMMENT '商品名称',
    price       DECIMAL(10,2) NOT NULL   COMMENT '商品价格',
    stock       INT          DEFAULT 0   COMMENT '库存数量',
    description TEXT                     COMMENT '商品描述',
    status      TINYINT      DEFAULT 0   COMMENT '状态（0-上架, 1-下架）',
    create_by   VARCHAR(64)              COMMENT '创建人（自动填充）',
    create_time TIMESTAMP                COMMENT '创建时间（自动填充）',
    update_by   VARCHAR(64)              COMMENT '更新人（自动填充）',
    update_time TIMESTAMP                COMMENT '更新时间（自动填充）',
    deleted     TINYINT      DEFAULT 0   COMMENT '逻辑删除标记（0-正常, 1-已删除）',
    version     INT          DEFAULT 1   COMMENT '乐观锁版本号'
);

-- 订单表：演示分布式锁、幂等注解、限流
CREATE TABLE IF NOT EXISTS demo_order (
    id           BIGINT       PRIMARY KEY COMMENT '订单ID（雪花算法生成）',
    user_id      BIGINT       NOT NULL    COMMENT '用户ID',
    product_id   BIGINT       NOT NULL    COMMENT '商品ID',
    product_name VARCHAR(128)             COMMENT '商品名称（冗余）',
    quantity     INT          NOT NULL    COMMENT '购买数量',
    total_price  DECIMAL(10,2) NOT NULL   COMMENT '订单总价',
    status       VARCHAR(20)  DEFAULT 'PENDING' COMMENT '订单状态（PENDING/PAID/CANCELLED）',
    create_by    VARCHAR(64)              COMMENT '创建人（自动填充）',
    create_time  TIMESTAMP                COMMENT '创建时间（自动填充）',
    update_by    VARCHAR(64)              COMMENT '更新人（自动填充）',
    update_time  TIMESTAMP                COMMENT '更新时间（自动填充）',
    deleted      TINYINT      DEFAULT 0   COMMENT '逻辑删除标记（0-正常, 1-已删除）',
    version      INT          DEFAULT 1   COMMENT '乐观锁版本号'
);

-- 逻辑删除索引（IllegalSQLInnerInterceptor 要求 WHERE 条件列必须有索引）
CREATE INDEX IF NOT EXISTS idx_sys_user_deleted ON sys_user (deleted);
CREATE INDEX IF NOT EXISTS idx_sys_dept_deleted ON sys_dept (deleted);
CREATE INDEX IF NOT EXISTS idx_product_deleted ON product (deleted);
CREATE INDEX IF NOT EXISTS idx_demo_order_deleted ON demo_order (deleted);
