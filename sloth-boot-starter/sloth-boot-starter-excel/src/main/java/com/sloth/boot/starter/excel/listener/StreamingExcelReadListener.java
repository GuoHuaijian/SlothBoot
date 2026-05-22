package com.sloth.boot.starter.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.sloth.boot.starter.excel.model.ExcelErrorDetail;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 流式 Excel 读取监听器。
 * <p>
 * 不缓存所有数据，仅通过回调逐批处理，适用于超大文件的内存友好场景。
 *
 * @param <T> 数据类型
 * @author sloth-boot
 * @since 1.0.0
 */
@Getter
public class StreamingExcelReadListener<T> implements ReadListener<T> {

    private final List<T> batch = new ArrayList<>();
    private final List<ExcelErrorDetail> errorDetails = new ArrayList<>();
    private final Consumer<List<T>> batchConsumer;
    private final int batchSize;
    private int totalRows;

    /**
     * 构造函数。
     *
     * @param batchConsumer 分批处理器
     * @param batchSize     每批处理的行数
     */
    public StreamingExcelReadListener(Consumer<List<T>> batchConsumer, int batchSize) {
        this.batchConsumer = batchConsumer;
        this.batchSize = batchSize;
        this.totalRows = 0;
    }

    /**
     * 构造函数（批次大小默认 1000）。
     *
     * @param batchConsumer 分批处理器
     */
    public StreamingExcelReadListener(Consumer<List<T>> batchConsumer) {
        this(batchConsumer, 1000);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        batch.add(data);
        totalRows++;
        if (batch.size() >= batchSize) {
            flush();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!batch.isEmpty()) {
            flush();
        }
    }

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
     * 将当前批次数据推送给消费者，并清空批次缓存。
     */
    private void flush() {
        if (!batch.isEmpty()) {
            batchConsumer.accept(new ArrayList<>(batch));
            batch.clear();
        }
    }

    /**
     * 是否有错误发生。
     *
     * @return 是否有错误
     */
    public boolean hasErrors() {
        return !errorDetails.isEmpty();
    }
}
