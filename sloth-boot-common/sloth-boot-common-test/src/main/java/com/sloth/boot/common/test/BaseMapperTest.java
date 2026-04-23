package com.sloth.boot.common.test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sloth.boot.common.base.BaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper 层测试基类。
 *
 * @param <T> 实体类型
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
     * @return 实体
     */
    protected T save(T entity) {
        baseMapper.insert(entity);
        return entity;
    }

    /**
     * 根据 ID 查询。
     *
     * @param id 主键
     * @return 实体
     */
    protected T findById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 更新实体。
     *
     * @param entity 实体
     */
    protected void update(T entity) {
        baseMapper.updateById(entity);
    }

    /**
     * 删除实体。
     *
     * @param id 主键
     */
    protected void delete(Long id) {
        baseMapper.deleteById(id);
    }
}
