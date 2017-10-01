package org.jenkinsci.plugins.dirdigger;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> implements Serializable {
    T data;
    List<TreeNode<T>> children;

    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<TreeNode<T>>();
    }

    public T getData() {
        return data;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public TreeNode<T> addChild(T child) {
        TreeNode<T> childNode = new TreeNode<T>(child);
        this.children.add(childNode);
        return childNode;
    }
}
