package org.jenkinsci.plugins.dirdigger.extensions.impl;

import hudson.Extension;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtension;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtensionDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class DirFilter extends DirDiggerExtension {
    private String regexpFilter;

    @DataBoundConstructor
    public DirFilter(String regexpFilter) {
        this.regexpFilter = regexpFilter;
    }

    public String getRegexpFilter() {
        return regexpFilter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return o instanceof DirFilter;
    }

    @Override
    public int hashCode() {
        return DirFilter.class.hashCode();
    }

    @Extension
    public static class DescriptorImpl extends DirDiggerExtensionDescriptor {
        @Override
        public String getDisplayName() {
            return "File filter";
        }
    }
}
