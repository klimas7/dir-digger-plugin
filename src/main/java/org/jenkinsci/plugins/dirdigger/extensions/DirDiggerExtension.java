package org.jenkinsci.plugins.dirdigger.extensions;

import hudson.model.AbstractDescribableImpl;

public abstract class DirDiggerExtension extends AbstractDescribableImpl<DirDiggerExtension> {

    @Override
    public DirDiggerExtensionDescriptor getDescriptor() {
        return (DirDiggerExtensionDescriptor) super.getDescriptor();
    }
}
