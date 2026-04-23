package com.sloth.boot.common.base;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 树节点
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
public class TreeNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 父节点ID
     */
    private Long parentId;

    /**
     * 子节点列表
     */
    private List<TreeNode> children;

    /**
     * 排序字段
     */
    private Integer sort;
}
