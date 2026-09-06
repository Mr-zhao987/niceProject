package com.ruoyi.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 树结构构建工具类
 *
 * @author ruoyi
 */
public class TreeUtils
{
    /**
     * 树节点接口 - 需要构建树的实体实现此接口
     */
    public interface TreeNode<T>
    {
        T getId();
        T getParentId();
        List<? extends TreeNode<T>> getChildren();
        void setChildren(List<? extends TreeNode<T>> children);
    }

    /**
     * 构建树（默认顶级父节点ID为0或null）
     *
     * @param nodes 所有节点列表
     * @return 树形结构列表
     */
    public static <T, E extends TreeNode<T>> List<E> build(List<E> nodes)
    {
        return build(nodes, null);
    }

    /**
     * 构建树
     *
     * @param nodes      所有节点列表
     * @param rootParentId 根节点的父ID（一般为0或null）
     * @return 树形结构列表
     */
    @SuppressWarnings("unchecked")
    public static <T, E extends TreeNode<T>> List<E> build(List<E> nodes, T rootParentId)
    {
        if (StringUtils.isEmpty(nodes))
        {
            return Collections.emptyList();
        }

        // 根据parentId分组
        Map<T, List<E>> groupMap = nodes.stream()
                .filter(node -> node.getParentId() != null)
                .collect(Collectors.groupingBy(TreeNode::getParentId));

        // 为每个节点设置子节点
        for (E node : nodes)
        {
            List<E> children = (List<E>) groupMap.get(node.getId());
            if (StringUtils.isNotEmpty(children))
            {
                // 按orderNum排序（如果节点实现了排序接口）
                children.sort((a, b) -> {
                    if (a instanceof Comparable && b instanceof Comparable)
                    {
                        return ((Comparable) a).compareTo(b);
                    }
                    return 0;
                });
                node.setChildren(children);
            }
            else
            {
                node.setChildren(new ArrayList<>());
            }
        }

        // 筛选根节点
        T finalRootParentId = rootParentId;
        return nodes.stream()
                .filter(node -> Optional.ofNullable(node.getParentId()).orElse((T) "").equals(finalRootParentId)
                        || (rootParentId == null && node.getParentId() == null))
                .collect(Collectors.toList());
    }

    /**
     * 扁平化树（将所有节点平铺为一维列表）
     *
     * @param nodes 树形结构
     * @return 扁平化列表
     */
    @SuppressWarnings("unchecked")
    public static <T, E extends TreeNode<T>> List<E> flatten(List<E> nodes)
    {
        if (StringUtils.isEmpty(nodes))
        {
            return Collections.emptyList();
        }

        List<E> result = new ArrayList<>();
        for (E node : nodes)
        {
            result.add(node);
            if (StringUtils.isNotEmpty(node.getChildren()))
            {
                result.addAll(flatten((List<E>) node.getChildren()));
            }
        }
        return result;
    }

    /**
     * 查找指定节点的所有祖先节点（自底向上）
     *
     * @param nodes     所有节点列表
     * @param nodeId    目标节点ID
     * @return 祖先节点列表（从父节点到根节点）
     */
    public static <T, E extends TreeNode<T>> List<E> findAncestors(List<E> nodes, T nodeId)
    {
        if (StringUtils.isEmpty(nodes) || nodeId == null)
        {
            return Collections.emptyList();
        }

        Map<T, E> nodeMap = nodes.stream()
                .collect(Collectors.toMap(TreeNode::getId, n -> n, (a, b) -> a));

        List<E> ancestors = new ArrayList<>();
        E current = nodeMap.get(nodeId);
        while (current != null && current.getParentId() != null)
        {
            E parent = nodeMap.get(current.getParentId());
            if (parent != null)
            {
                ancestors.add(parent);
            }
            current = parent;
        }
        return ancestors;
    }

    /**
     * 获取树的深度
     */
    @SuppressWarnings("unchecked")
    public static <T, E extends TreeNode<T>> int depth(List<E> nodes)
    {
        if (StringUtils.isEmpty(nodes))
        {
            return 0;
        }

        int maxDepth = 0;
        for (E node : nodes)
        {
            int depth = 1;
            if (StringUtils.isNotEmpty(node.getChildren()))
            {
                depth += depth((List<E>) node.getChildren());
            }
            maxDepth = Math.max(maxDepth, depth);
        }
        return maxDepth;
    }

    /**
     * 过滤树（保留匹配的节点及其祖先路径）
     *
     * @param nodes  树形结构
     * @param filter 断言函数
     * @return 过滤后的树
     */
    @SuppressWarnings("unchecked")
    public static <T, E extends TreeNode<T>> List<E> filter(List<E> nodes, java.util.function.Predicate<E> filter)
    {
        if (StringUtils.isEmpty(nodes))
        {
            return Collections.emptyList();
        }

        List<E> result = new ArrayList<>();
        for (E node : nodes)
        {
            List<E> filteredChildren = StringUtils.isNotEmpty(node.getChildren())
                    ? filter((List<E>) node.getChildren(), filter)
                    : Collections.emptyList();

            if (filter.test(node) || StringUtils.isNotEmpty(filteredChildren))
            {
                E copy = node;
                copy.setChildren(filteredChildren);
                result.add(copy);
            }
        }
        return result;
    }
}
