package org.jenkinsci.plugins.dirdigger;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.StringParameterDefinition;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class DirDiggerDefinition extends ParameterDefinition {

    private final String root;

    @DataBoundConstructor
    public DirDiggerDefinition(String name, String description, String root) {
        super(name, description);
        this.root = root;
    }

    @Override
    public ParameterValue createValue(StaplerRequest staplerRequest, JSONObject jsonObject) {
        return null;
    }

    @Override
    public ParameterValue createValue(StaplerRequest staplerRequest) {
        return null;
    }

    public String getRoot() {
        return root;
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
