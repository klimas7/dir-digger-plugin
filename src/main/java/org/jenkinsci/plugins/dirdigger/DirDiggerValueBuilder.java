package org.jenkinsci.plugins.dirdigger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DirDiggerValueBuilder {
    private String name;
    private String root;
    private final List<String> values;

    public DirDiggerValueBuilder() {
        this.values = new ArrayList<String>();
    }

    public DirDiggerValueBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public DirDiggerValueBuilder withRoot(String root) {
        this.root = root;
        return this;
    }

    public DirDiggerValueBuilder addValue(Object value) {
        if (StringUtils.isNotBlank((String) value)) {
            values.add((String) value);
        }
        return this;
    }

    public DirDiggerValueBuilder addValues(String[] values) {
        for (String value : values) {
            addValue(value);
        }
        return this;
    }

    public DirDiggerValue build() {
        StringBuilder stringValue = new StringBuilder();

        for (String value : values) {
            stringValue.append(value).append(File.separator);
        }

        if (stringValue.indexOf(root) != 0) {
            stringValue.insert(0, root).insert(root.length(), File.separator);
        }

        stringValue.setLength(stringValue.length() - 1);
        DirDiggerValue dirDiggerValue = new DirDiggerValue(name, stringValue.toString());
        return dirDiggerValue;
    }
}
