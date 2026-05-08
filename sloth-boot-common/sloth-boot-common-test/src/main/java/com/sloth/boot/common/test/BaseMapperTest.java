package com.sloth.boot.common.test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sloth.boot.starter.mybatis.core.BaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper 层测试基类。
 * <p>
 * 继承自 {@link BaseSpringBootTest}，自动注入 {@link BaseMapper} 实例， 提供通用的 CRUD
 * 辅助方法，子类可直接调用以简化 Mapper 层单元测试。
 *
 * @param <T> 实体类型，必须继承 {@link com.sloth.boot.starter.mybatis.core.BaseEntity}
 * @author sloth-boot
 * @since 1.0.0
 */
public abstract class BaseMapperTest<T extends BaseEntity> extends BaseSpringBootTest {

    @Autowired(required = false)
    protected BaseMapper<T> baseMapper;

    /**
     * 测试前初始化。
     */
    @BeforeEach
    public void setUp() {
        // 预留给子类扩展。
    }

    /**
     * 保存实体。
     *
     * @param entity 实体
     * @return 插入后的实体（包含自动生成的主键）
     */
    protected T save(T entity) {
        baseMapper.insert(entity);
        return entity;
    }

    /**
     * 根据 ID 查询实体。
     *
     * @param id 主键 ID
     * @return 实体，不存在时返回 {@code null}
     */
    protected T findById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 根据 ID 更新实体。
     *
     * @param entity 实体（主键不能为空）
     */
    protected void update(T entity) {
        baseMapper.updateById(entity);
    }

    /**
     * 根据 ID 删除实体。
     *
     * @param id 主键 ID
     */
    protected void delete(Long id) {
        baseMapper.deleteById(id);
    }
}
