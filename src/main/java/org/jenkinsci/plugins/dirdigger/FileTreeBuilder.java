package org.jenkinsci.plugins.dirdigger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileTreeBuilder {
    public synchronized static void build(TreeNode<String> fileTree, Integer depth) {
        if (isTreeInitialize(fileTree)) {
            return;
        }
        File root = new File(fileTree.getData());
        deeper(root, fileTree, depth);
    }

    private static void deeper(File root, TreeNode rootTree, int depth) {
        if (depth == 0) {
            return;
        }
        for (File file : root.listFiles()) {
            if (file.isDirectory()) {
                TreeNode child = rootTree.addChild(file.getName());
                deeper(file, child, depth -1);
            } else {
                rootTree.addChild(file.getName());
            }
        }
    }


    private static boolean isTreeInitialize(TreeNode<String> fileTree)
    {
        return fileTree.getChildren().size() > 0;
    }

    public static List<String> getFileFromLevel(TreeNode<String> fileTree, Integer level) {
        List<TreeNode<String>> children = getChildren(fileTree, level);
        return convert(children);
    }

    private static List<String> convert(List<TreeNode<String>> children) {
        if (children == null) {
            return Collections.emptyList();
        }
        List<String> files = new ArrayList<>(children.size());
        for (TreeNode<String> node : children) {
            files.add(node.data);
        }
        return files;
    }

    private static List<TreeNode<String>> getChildren(TreeNode<String> fileTree, Integer level) {
        if (level == 1) {
            return fileTree.getChildren();
        }
        else if (fileTree.getChildren().size() > 0) {
            return getChildren(fileTree.getChildren().get(0), level - 1);
        }
        else {
            return Collections.emptyList();
        }
    }

    public static List<String> getFileFromTree(TreeNode<String> fileTree, String jsonFileNames) {
        List<String> files = getFileNames(jsonFileNames);
        List<TreeNode<String>> children = fileTree.getChildren();
        for (String file : files) {
            for (TreeNode<String> x : children) {
                if (file.equals(x.getData())) {
                    children = x.getChildren();
                    break;
                }
            }
        }
        return convert(children);
    }

    private static List<String> getFileNames(String jsonFileNames) {
        ObjectMapper obj = new ObjectMapper();
        try {
            return obj.readValue(jsonFileNames, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
