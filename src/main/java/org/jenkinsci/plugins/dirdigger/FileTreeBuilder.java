package org.jenkinsci.plugins.dirdigger;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.dirdigger.extensions.impl.DirFilter;

public class FileTreeBuilder {
    private static final Logger LOGGER = Logger.getLogger(FileTreeBuilder.class.getName());

    public synchronized static void build(TreeNode<String> fileTree, Integer depth, List<DirFilter> dirFilters) {
        File root = new File(fileTree.getData());
        deeper(root, fileTree, depth, dirFilters);
    }

    private static void deeper(File root, TreeNode<String> rootTree, int depth, List<DirFilter> dirFilters) {
        if (rootTree == null || rootTree.getLevel() == depth) {
            return;
        }

        File[] files = root.listFiles();
        if (files == null) {
            return;
        }

        DirFilter dirFilter = getDirFilter(rootTree.getLevel(), dirFilters);
        for (File file : files) {
            if (matchDirFilter(file, dirFilter)) {
                if (file.isDirectory()) {
                    TreeNode child = rootTree.addChild(file.getName());
                    deeper(file, child, depth, dirFilters);
                } else {
                    rootTree.addChild(file.getName());
                }
            }
        }
    }

    private static DirFilter getDirFilter(int level, List<DirFilter> dirFilters) {
        if (dirFilters.size() > level) {
            return dirFilters.get(level);
        }
        return DirFilter.WILD_CARD;
    }

    private static boolean matchDirFilter(File file, DirFilter dirFilter) {
        return dirFilter.getPattern().matcher(file.getName()).find();
    }

    public static List<String> getFileNameFromLevel(TreeNode<String> fileTree, Integer level) {
        List<TreeNode<String>> children = getChildren(fileTree, level);
        return convert(children);
    }

    private static List<String> convert(List<TreeNode<String>> children) {
        if (children == null) {
            return Collections.emptyList();
        }
        List<String> files = new ArrayList<>(children.size());
        for (TreeNode<String> node : children) {
            files.add(node.getData());
        }
        return files;
    }

    private static List<TreeNode<String>> getChildren(TreeNode<String> fileTree, Integer level) {
        if (level.equals(fileTree.getLevel())) {
            return fileTree.getChildren();
        } else if (fileTree.hasChildren()) {
            return getChildren(fileTree.getFirstChild(), level);
        } else {
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
            LOGGER.log(Level.SEVERE, "JSON mapping exception", e);
        }
        return Collections.emptyList();
    }
}
