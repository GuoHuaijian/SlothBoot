package com.sloth.boot.starter.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 通用 Excel 读取监听器。
 *
 * @param <T> 数据类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class ExcelReadListener<T> extends PageReadListener<T> {

    private final List<T> cachedData = new ArrayList<>();
    private final List<String> errorRows = new ArrayList<>();
    private final List<String> invalidRows = new ArrayList<>();

    /**
     * 构造函数。
     *
     * @param consumer 分批处理器
     */
    public ExcelReadListener(Consumer<List<T>> consumer) {
        super(consumer, 1000);
    }

    /**
     * 读取每一条数据。
     *
     * @param data    数据
     * @param context 上下文
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedData.add(data);
        super.invoke(data, context);
    }

    /**
     * 读取异常回调。
     *
     * @param exception 异常
     * @param context   上下文
     * @throws Exception 异常
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        errorRows.add("row=" + context.readRowHolder().getRowIndex() + ", error=" + exception.getMessage());
    }

    /**
     * 记录校验失败行。
     *
     * @param rowMessage 行消息
     */
    public void addInvalidRow(String rowMessage) {
        invalidRows.add(rowMessage);
    }
}
