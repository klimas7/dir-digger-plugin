package org.jenkinsci.plugins.dirdigger.extensions.impl.DirFilter;

def f = namespace(lib.FormTagLib);

f.entry(title:_("Filter Dir XZZ"), field:"regexFilter") {
    f.textbox()
}
