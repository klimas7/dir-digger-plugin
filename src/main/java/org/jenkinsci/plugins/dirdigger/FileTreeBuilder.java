package org.jenkinsci.plugins.dirdigger;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jenkinsci.plugins.dirdigger.extensions.impl.DirFilter;

public class FileTreeBuilder {
    public synchronized static void build(TreeNode<String> fileTree, Integer depth, List<DirFilter> dirFilters) {
        if (isTreeInitialize(fileTree)) {
            return;
        }
        File root = new File(fileTree.getData());
        deeper(root, fileTree, 0, depth, dirFilters);
    }

    private static void deeper(File root, TreeNode rootTree, int level, int depth, List<DirFilter> dirFilters) {
        if (level == depth || root == null || rootTree == null) {
            return;
        }
        File[] files = root.listFiles();
        if (files == null) {
            return;
        }

        DirFilter dirFilter = DirFilter.WILD_CARD;
        if (dirFilters.size() > level) {
            dirFilter = dirFilters.get(level);
        }
        for (File file : files) {
            if (matchDirFilter(file, dirFilter)) {
                if (file.isDirectory()) {
                    TreeNode child = rootTree.addChild(file.getName());
                    deeper(file, child, level +1, depth, dirFilters);
                } else {
                    rootTree.addChild(file.getName());
                }
            }
        }
    }

    private static boolean matchDirFilter(File file, DirFilter dirFilter) {
        return dirFilter.getPattern().matcher(file.getName()).find();
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
