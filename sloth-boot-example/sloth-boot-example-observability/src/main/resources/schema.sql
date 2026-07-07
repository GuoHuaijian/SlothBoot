-- ===== 可观测性演示数据表 =====

-- 用户表
DROP TABLE IF EXISTS demo_order CASCADE;
DROP TABLE IF EXISTS demo_product CASCADE;
DROP TABLE IF EXISTS demo_user CASCADE;

CREATE TABLE demo_user (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(64)  NOT NULL,
    email       VARCHAR(128) NOT NULL,
    role        VARCHAR(16)  NOT NULL,
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    create_by   VARCHAR(64),
    update_by   VARCHAR(64),
    deleted     INT          DEFAULT 0,
    version     INT          DEFAULT 0
);

-- 商品表
CREATE TABLE demo_product (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    stock       INT NOT NULL DEFAULT 0,
    category    VARCHAR(32) NOT NULL,
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    create_by   VARCHAR(64),
    update_by   VARCHAR(64),
    deleted     INT          DEFAULT 0,
    version     INT          DEFAULT 0
);

-- 订单表（id 为 Long，由 MyBatis-Plus 雪花算法赋值；预置数据用 1001-1020）
CREATE TABLE demo_order (
    id          BIGINT PRIMARY KEY,
    order_no    VARCHAR(32) NOT NULL,
    user_id     BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    product_name VARCHAR(128),
    quantity    INT NOT NULL,
    amount      DECIMAL(10, 2) NOT NULL,
    status      VARCHAR(16) NOT NULL,
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    create_by   VARCHAR(64),
    update_by   VARCHAR(64),
    deleted     INT          DEFAULT 0,
    version     INT          DEFAULT 0
);

-- 逻辑删除列建索引，避免 MP IllegalSQLInnerInterceptor 在 dev profile 拦截
CREATE INDEX idx_demo_user_deleted ON demo_user (deleted);
CREATE INDEX idx_demo_product_deleted ON demo_product (deleted);
CREATE INDEX idx_demo_order_deleted ON demo_order (deleted);
