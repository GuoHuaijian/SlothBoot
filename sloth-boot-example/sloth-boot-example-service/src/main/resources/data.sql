-- ============================================================
-- 初始数据
-- ============================================================

-- 部门初始数据
INSERT INTO sys_dept (id, name, parent_id, sort, leader, status, ancestors, create_by, create_time, update_by, update_time, deleted, version)
VALUES
    (1, 'Sloth科技',     0, 1, '管理员', 0, '0',      'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (2, '技术研发部',     1, 1, '张三',   0, '0,1',    'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (3, '产品设计部',     1, 2, '李四',   0, '0,1',    'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (4, '后端开发组',     2, 1, '王五',   0, '0,1,2',  'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (5, '前端开发组',     2, 2, '赵六',   0, '0,1,2',  'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1);

-- 用户初始数据
INSERT INTO sys_user (id, dept_id, username, phone, id_card, email, gender, status, extra_info, create_by, create_time, update_by, update_time, deleted, version)
VALUES
    (1, 1, 'admin',     '13800138000', '110101199001011234', 'admin@sloth.boot',     1, 0, '{"roles":["admin","user"],"tags":["admin"]}',        'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (2, 4, 'dev_user',  '13900139000', '110101199002021234', 'dev@sloth.boot',       1, 0, '{"roles":["developer"],"tags":["backend"]}',        'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (3, 3, 'prod_user', '13700137000', '110101199003031234', 'product@sloth.boot',   2, 0, '{"roles":["product_mgr"],"tags":["product"]}',      'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1);

-- 商品初始数据
INSERT INTO product (id, name, price, stock, description, status, create_by, create_time, update_by, update_time, deleted, version)
VALUES
    (1,  'Sloth Boot 企业版授权',  9999.00, 100, '企业级 Spring Cloud 脚手架完整授权', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (2,  'Sloth Boot 技术支持',    4999.00, 50,  '一年期专业技术支持服务', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (3,  'Redis 高级缓存课程',     299.00,  200, '涵盖布隆过滤器、分布式锁、Pub/Sub 等高级特性', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (4,  'MyBatis-Plus 实战指南',  199.00,  300, '深入掌握 BaseMapperX、数据权限、字段加密等', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (5,  'Spring AI 集成教程',     399.00,  150, '从零搭建 AI 聊天助手：同步/流式/多轮对话', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (6,  '分布式监控实战',          249.00,  180, 'JVM 监控、线程池管理、Micrometer 指标采集', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (7,  '安全加密工具包',          149.00,  250, 'AES/RSA/BCrypt/HMAC/XSS 全方位安全防护', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (8,  '动态线程池管理',          199.00,  200, '线程池动态调参、队列监控、拒绝策略配置', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (9,  'EasyExcel 批量导入导出', 99.00,   500, '百万级数据 Excel 导入导出最佳实践', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1),
    (10, 'Sa-Token 权限方案',      179.00,  300, '登录认证、权限校验、数据权限一体化方案', 0, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP, 0, 1);
