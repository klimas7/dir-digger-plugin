package org.jenkinsci.plugins.dirdigger;

import java.util.HashMap;
import java.util.Map;

import hudson.model.ParameterValue;
import org.kohsuke.stapler.DataBoundConstructor;

public class DirDiggerValue extends ParameterValue{
    private Map<String, String> value;

    @DataBoundConstructor
    public DirDiggerValue(String name, String description) {
        super(name, description);
        value = new HashMap<>();
    }

    @Override
    public Map<String, String> getValue() {
        return value;
    }
}
