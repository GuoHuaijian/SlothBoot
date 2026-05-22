package com.sloth.boot.starter.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.sloth.boot.starter.excel.model.ExcelErrorDetail;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 通用 Excel 读取监听器。
 * <p>
 * 支持分批处理、数据缓存、错误收集，以及可配置的批次大小和表头跳行。
 *
 * @param <T> 数据类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class ExcelReadListener<T> extends PageReadListener<T> {

    private final List<T> cachedData = new ArrayList<>();
    private final List<String> invalidRows = new ArrayList<>();
    private final List<ExcelErrorDetail> errorDetails = new ArrayList<>();

    /**
     * 是否缓存所有读取的数据。为 {@code false} 时仅通过 consumer 处理，不保留全量缓存。
     */
    private final boolean cacheData;

    /**
     * 构造函数。
     *
     * @param consumer   分批处理器
     * @param batchSize  每批处理的行数
     * @param cacheData  是否缓存所有读取的数据
     */
    public ExcelReadListener(Consumer<List<T>> consumer, int batchSize, boolean cacheData) {
        super(consumer, batchSize);
        this.cacheData = cacheData;
    }

    /**
     * 构造函数（默认缓存数据，批次大小 1000，无分批回调）。
     * <p>
     * 适用于仅需收集所有数据、无需分批处理的场景。
     */
    public ExcelReadListener() {
        this(batch -> {
        }, 1000, true);
    }

    /**
     * 构造函数（默认缓存数据，批次大小 1000）。
     *
     * @param consumer 分批处理器
     */
    public ExcelReadListener(Consumer<List<T>> consumer) {
        this(consumer, 1000, true);
    }

    /**
     * 构造函数（指定批次大小，默认缓存数据）。
     *
     * @param consumer  分批处理器
     * @param batchSize 每批处理的行数
     */
    public ExcelReadListener(Consumer<List<T>> consumer, int batchSize) {
        this(consumer, batchSize, true);
    }

    /**
     * 读取每一条数据。
     *
     * @param data    数据
     * @param context 上下文
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        if (cacheData) {
            cachedData.add(data);
        }
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
        int rowIndex = context.readRowHolder().getRowIndex();
        errorDetails.add(ExcelErrorDetail.builder()
            .errorType(ExcelErrorDetail.ErrorType.PARSE_EXCEPTION)
            .rowIndex(rowIndex)
            .message(Objects.toString(exception.getMessage(), ""))
            .build());
    }

    /**
     * 记录校验失败行。
     *
     * @param rowMessage 行消息
     */
    public void addInvalidRow(String rowMessage) {
        invalidRows.add(rowMessage);
    }

    /**
     * 添加错误详情。
     *
     * @param detail 错误详情
     */
    public void addErrorDetail(ExcelErrorDetail detail) {
        this.errorDetails.add(detail);
    }
}
