package com.sloth.boot.common.util;

import cn.hutool.core.collection.CollUtil;
import com.sloth.boot.common.base.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 树形结构工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class TreeUtil {

    /**
     * 构建树形结构
     *
     * @param list     节点列表
     * @param rootId   根节点ID
     * @param <T>      节点类型
     * @return 树形结构列表
     */
    public static <T extends TreeNode> List<T> buildTree(List<T> list, Long rootId) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }

        // 转换为 Map，方便查找
        Map<Long, T> nodeMap = list.stream().collect(Collectors.toMap(TreeNode::getId, node -> node));

        // 构建树形结构
        List<T> treeList = new ArrayList<>();
        for (T node : list) {
            Long parentId = node.getParentId();
            if (parentId == null || parentId.equals(rootId)) {
                // 根节点
                treeList.add(node);
            } else {
                // 子节点，查找父节点
                T parent = nodeMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(node);
                }
            }
        }

        return treeList;
    }

    /**
     * 展开树形结构为扁平列表
     *
     * @param tree     树形结构
     * @param <T>      节点类型
     * @return 扁平列表
     */
    public static <T extends TreeNode> List<T> flattenTree(List<T> tree) {
        List<T> flatList = new ArrayList<>();
        if (CollUtil.isEmpty(tree)) {
            return flatList;
        }

        for (T node : tree) {
            flatList.add(node);
            if (CollUtil.isNotEmpty(node.getChildren())) {
                flatList.addAll(flattenTree(node.getChildren()));
            }
        }

        return flatList;
    }
}
