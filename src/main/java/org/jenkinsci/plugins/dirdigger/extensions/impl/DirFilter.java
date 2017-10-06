package org.jenkinsci.plugins.dirdigger.extensions.impl;

import hudson.Extension;
import hudson.util.FormValidation;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtension;
import org.jenkinsci.plugins.dirdigger.extensions.DirDiggerExtensionDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

        public FormValidation doCheckRegexFilter(@QueryParameter String value) {
            try {
                Pattern.compile(value);
            }
            catch (PatternSyntaxException ex) {
                return FormValidation.error("Invalid pattern: " + ex.getMessage());
            }
            return FormValidation.ok();
        }
    }
}
