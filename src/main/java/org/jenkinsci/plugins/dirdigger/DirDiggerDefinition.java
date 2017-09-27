package org.jenkinsci.plugins.dirdigger;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;

import java.util.HashMap;
import java.util.Map;

import hudson.model.StringParameterValue;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

public class DirDiggerDefinition extends ParameterDefinition {

    private final String root;

    @DataBoundConstructor
    public DirDiggerDefinition(String name, String description, String root) {
        super(name, description);
        this.root = root;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
        value.setDescription(getDescription());
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest staplerRequest) {
        //TODO
        return null;
    }

    public String getRoot() {
        return root;
    }

    @Exported
    public Map<String, String> getFiles() {
        Map<String, String> files = new HashMap<>();
        files.put("/opt/Test", "Test");
        files.put("/opt/Test2", "Test2");
        return files;
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor
    {
        @Override
        public String getDisplayName() {
            return "Dir Digger";
        }
    }
}
