package org.jenkinsci.plugins.dirdigger;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> implements Serializable {
    private final Integer level;
    private final T data;
    private final List<TreeNode<T>> children;

    public TreeNode(T data) {
        this(data, 0);
    }

    private TreeNode(T data, Integer level) {
        this.data = data;
        this.level = level;
        this.children = new LinkedList<TreeNode<T>>();
    }

    public T getData() {
        return data;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public Integer getLevel() {
        return level;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public TreeNode<T> getFirstChild() {
        return children.get(0);
    }

    public TreeNode<T> addChild(T child) {
        TreeNode<T> childNode = new TreeNode<T>(child, this.level + 1);
        this.children.add(childNode);
        return childNode;
    }
}
