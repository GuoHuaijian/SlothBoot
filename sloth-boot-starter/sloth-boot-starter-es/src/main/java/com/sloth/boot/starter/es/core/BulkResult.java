package com.sloth.boot.starter.es.core;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量操作结果。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class BulkResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否全部成功。
     */
    private boolean success;

    /**
     * 成功数量。
     */
    private int successCount;

    /**
     * 失败数量。
     */
    private int failCount;

    /**
     * 失败项明细。
     */
    private List<FailItem> failItems = new ArrayList<>();

    /**
     * 新增失败项。
     *
     * @param id      文档 ID
     * @param reason  失败原因
     */
    public void addFailItem(String id, String reason) {
        failItems.add(new FailItem(id, reason));
        failCount++;
    }

    /**
     * 失败项。
     */
    @Data
    public static class FailItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String id;
        private final String reason;
    }
}
