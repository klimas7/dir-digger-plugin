package org.jenkinsci.plugins.dirdigger.extensions.impl;

import hudson.Extension;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtension;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtensionDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.regex.Pattern;

public class DirFilter extends DirDiggerExtension {
    public static final DirFilter WILD_CARD = new DirFilter(".*");
    private final String regexFilter;
    private final Pattern pattern;

    @DataBoundConstructor
    public DirFilter(String regexFilter) {
        this.regexFilter = regexFilter;
        this.pattern = Pattern.compile(regexFilter);
    }

    public String getRegexFilter() {
        return regexFilter;
    }

    public Pattern getPattern() {
        return pattern;
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
