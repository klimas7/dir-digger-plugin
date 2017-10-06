package org.jenkinsci.plugins.dirdigger.extensions;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

public abstract class DirDiggerExtensionDescriptor extends Descriptor<DirDiggerExtension> {

    public static DescriptorExtensionList<DirDiggerExtension, DirDiggerExtensionDescriptor> all() {
        return Jenkins.getInstance().getDescriptorList(DirDiggerExtension.class);
    }
}
