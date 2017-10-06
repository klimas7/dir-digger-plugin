package org.jenkinsci.plugins.dirdigger;

import com.sonyericsson.rebuild.RebuildParameterPage;
import com.sonyericsson.rebuild.RebuildParameterProvider;
import hudson.Extension;
import hudson.model.ParameterValue;

@Extension(optional = true)
public class DirDiggerRebuild extends RebuildParameterProvider {

    @Override
    public RebuildParameterPage getRebuildPage(ParameterValue parameterValue) {
        return new RebuildParameterPage(DirDiggerValue.class, "value.jelly");
    }
}
