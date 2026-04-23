package com.sloth.boot.starter.mybatis.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import java.util.List;

/**
 * 批量插入 SQL 注入器。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class InsertBatchSqlInjector extends DefaultSqlInjector {

    /**
     * 获取方法列表。
     *
     * @param mapperClass Mapper 类型
     * @param tableInfo   表信息
     * @return 方法列表
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methods = super.getMethodList(mapperClass, tableInfo);
        methods.add(new InsertBatchSomeColumn());
        return methods;
    }
}
