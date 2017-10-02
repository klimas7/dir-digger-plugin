package org.jenkinsci.plugins.dirdigger;

import java.io.File;
import java.util.List;
import java.util.UUID;

import hudson.Extension;
import hudson.Util;
import hudson.model.Job;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.Saveable;
import hudson.util.DescribableList;
import hudson.util.ListBoxModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtension;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtensionDescriptor;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class DirDiggerDefinition extends ParameterDefinition {
    private final UUID uuid;
    private final String root;
    private final Integer depth;
    private TreeNode<String> fileTree;

    /**
     * All the configured extensions attached to this.
     */
    private DescribableList<DirDiggerExtension,DirDiggerExtensionDescriptor> extensions;

    @DataBoundConstructor
    public DirDiggerDefinition(String name, String description, String root, Integer depth, List<DirDiggerExtension> extensions) {
        super(name, description);
        uuid = UUID.randomUUID();
        this.root = root;
        this.depth = depth;
        this.fileTree = new TreeNode<>(root);

        this.extensions = new DescribableList<DirDiggerExtension, DirDiggerExtensionDescriptor>(Saveable.NOOP, Util.fixNull(extensions));
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

    public DescribableList<DirDiggerExtension, DirDiggerExtensionDescriptor> getExtensions() {
        return extensions;
    }

    public String getDivUUID() {
        StringBuilder randomSelectName = new StringBuilder();
        randomSelectName.append(getName()).append("-").append(uuid);
        return randomSelectName.toString();
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

        public List<DirDiggerExtensionDescriptor> getExtensionDescriptors() {
            return DirDiggerExtensionDescriptor.all();
        }
    }
}
