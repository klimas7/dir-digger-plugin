package org.jenkinsci.plugins.dirdigger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import hudson.Extension;
import hudson.model.Job;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersDefinitionProperty;
import hudson.util.ListBoxModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class DirDiggerDefinition extends ParameterDefinition {
    private final String root;
    private final Integer depth;
    private TreeNode<String> fileTree;

    @DataBoundConstructor
    public DirDiggerDefinition(String name, String description, String root, Integer depth) {
        super(name, description);
        this.root = root;
        this.depth = depth;
        this.fileTree = new TreeNode<>(root);
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringBuilder strValue = new StringBuilder();
        strValue.append(root).append(File.separator);
        Object value = jo.get("value");
        if (value instanceof String) {
            strValue.append(value).append(File.separator);
        } else if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            for (Object element : JSONArray.toCollection(jsonArray, String.class)) {
                strValue.append(element).append(File.separator);
            }
        }
        strValue.setLength(strValue.length() - 1);
        DirDiggerValue dirDiggerValue = new DirDiggerValue(jo.getString("name"), strValue.toString());
        return dirDiggerValue;
    }

    @Override
    public ParameterValue createValue(StaplerRequest staplerRequest) {
        //TODO
        return null;
    }

    public String getRoot() {
        return root;
    }

    public Integer getDepth() {
        return depth;
    }

    public void initTree() {
        fileTree = new TreeNode<>(root);
        FileTreeBuilder.build(fileTree, depth);
    }

    public List<String> getFiles(Integer level) {
        return FileTreeBuilder.getFileFromLevel(fileTree, level);
    }

    public List<String> getFiles(String jsonFileNames) {
        return FileTreeBuilder.getFileFromTree(fileTree, jsonFileNames);
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        @Override
        public String getDisplayName() {
            return "Dir Digger";
        }

        public ListBoxModel doFillValueItems(@AncestorInPath Job job, @QueryParameter String parameterName, @QueryParameter String jsonFileNames) {
            ListBoxModel items = new ListBoxModel();
            ParametersDefinitionProperty property = (ParametersDefinitionProperty) job.getProperty(ParametersDefinitionProperty.class);
            if (property != null) {
                ParameterDefinition parameterDefinition = property.getParameterDefinition(parameterName);
                if (parameterDefinition instanceof DirDiggerDefinition) {
                    DirDiggerDefinition dirDigger = (DirDiggerDefinition) parameterDefinition;
                    for (String file : dirDigger.getFiles(jsonFileNames)) {
                        items.add(file);
                    }
                }
            }
            return items;
        }
    }
}
