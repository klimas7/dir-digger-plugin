package org.jenkinsci.plugins.dirdigger;

import hudson.Extension;
import hudson.Util;
import hudson.cli.CLICommand;
import hudson.model.*;
import hudson.util.DescribableList;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtension;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtensionDescriptor;
import org.jenkinsci.plugins.dirdigger.extensions.impl.DirFilter;
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
    private DescribableList<DirDiggerExtension, DirDiggerExtensionDescriptor> extensions;

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
        DirDiggerValueBuilder dirDiggerValueBuilder = new DirDiggerValueBuilder();
        dirDiggerValueBuilder.withName(jo.getString("name"))
                .withRoot(root);

        Object value = jo.get("value");
        if (value instanceof String) {
            dirDiggerValueBuilder.addValue(value);
        } else if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            for (Object jsonElement : JSONArray.toCollection(jsonArray, String.class)) {
                dirDiggerValueBuilder.addValue(jsonElement);
            }
        }

        return dirDiggerValueBuilder.build();
    }

    @Override
    public ParameterValue createValue(StaplerRequest request) {
        DirDiggerValueBuilder dirDiggerValueBuilder = new DirDiggerValueBuilder();
        dirDiggerValueBuilder.withName(getName())
                .withRoot(root);

        String[] values = request.getParameterValues(getName());
        dirDiggerValueBuilder.addValues(values);

        return dirDiggerValueBuilder.build();
    }

    @Override
    public ParameterValue createValue(CLICommand command, String value) throws IOException, InterruptedException {
        DirDiggerValueBuilder dirDiggerValueBuilder = new DirDiggerValueBuilder();
        dirDiggerValueBuilder.withName(getName())
                .withRoot(root)
                .addValue(value);

        return dirDiggerValueBuilder.build();
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
        List<DirFilter> dirFilters = getDirFilters(extensions);
        FileTreeBuilder.build(fileTree, depth, dirFilters);
    }

    private List<DirFilter> getDirFilters(DescribableList<DirDiggerExtension, DirDiggerExtensionDescriptor> extensions) {
        List<DirFilter> dirFilters = new ArrayList<>();
        for (DirDiggerExtension extension : extensions) {
            if (extension instanceof DirFilter) {
                dirFilters.add((DirFilter) extension);
            }
        }
        return dirFilters;
    }

    public List<String> getFiles(Integer level) {
        return FileTreeBuilder.getFileNameFromLevel(fileTree, level);
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

        public FormValidation doCheckRoot(@QueryParameter String value) {
            File root = new File(value);
            if (!root.exists() || !root.canExecute()) {
                return FormValidation.error("File not exists or can't execute");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckDepth(@QueryParameter String value) {
            if (StringUtils.isBlank(value) || !StringUtils.isNumeric(value)) {
                return FormValidation.error("Depth must be number");
            }
            return FormValidation.ok();
        }
    }
}
