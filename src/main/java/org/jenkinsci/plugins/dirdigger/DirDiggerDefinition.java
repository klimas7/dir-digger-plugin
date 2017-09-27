package org.jenkinsci.plugins.dirdigger;

import java.util.HashMap;
import java.util.Map;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class DirDiggerDefinition extends ParameterDefinition {
    private final String root;
    private final Integer depth;

    @DataBoundConstructor
    public DirDiggerDefinition(String name, String description, String root, Integer depth) {
        super(name, description);
        this.root = root;
        this.depth = depth;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        DirDiggerValue value = req.bindJSON(DirDiggerValue.class, jo);
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

    public Integer getDepth() {
        return depth;
    }

    //@Exported
    public Map<String, String> getFiles(Integer level) {
        Map<String, String> files = new HashMap<>();
        files.put("/opt/Test", "Test_" + level);
        files.put("/opt/Test2", "Test2" + level);
        return files;
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        @Override
        public String getDisplayName() {
            return "Dir Digger";
        }
    }
}
