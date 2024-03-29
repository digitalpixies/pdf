/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.woody.binding;

import org.apache.cocoon.woody.formmodel.Widget;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

/**
 * ContextJXPathBinding provides an implementation of a {@link Binding}
 * that narrows the binding scope to some xpath-context on the target
 * objectModel to load and save from.
 *
 * @version CVS $Id: ContextJXPathBinding.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class ContextJXPathBinding extends ComposedJXPathBindingBase {

    /**
     * the relative contextPath for the sub-bindings of this context
     */
    private final String xpath;

    /**
     * Constructs ContextJXPathBinding for the specified xpath sub-context
     */
    public ContextJXPathBinding(JXPathBindingBuilderBase.CommonAttributes commonAtts, String contextPath, JXPathBindingBase[] childBindings) {
        super(commonAtts, childBindings);
        this.xpath = contextPath;
    }

    /**
     * Actively performs the binding from the ObjectModel wrapped in a jxpath
     * context to the Woody-form.
     */
    public void doLoad(Widget frmModel, JXPathContext jxpc) throws BindingException {
        Pointer ptr = jxpc.getPointer(this.xpath);
        if (ptr.getNode() != null) {
            JXPathContext subContext = jxpc.getRelativeContext(ptr);
            super.doLoad(frmModel, subContext);
            if (getLogger().isDebugEnabled())
                getLogger().debug("done loading " + toString());
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("non-existent path: skipping " + toString());
            }
        }
    }

    /**
     * Actively performs the binding from the Woody-form to the ObjectModel
     * wrapped in a jxpath context.
     */
    public void doSave(Widget frmModel, JXPathContext jxpc) throws BindingException {
        Pointer ptr = jxpc.getPointer(this.xpath);
        if (ptr.getNode() == null) {
            jxpc.createPath(this.xpath);
            // Need to recreate the pointer after creating the path
            ptr = jxpc.getPointer(this.xpath);
        }
        JXPathContext subContext = jxpc.getRelativeContext(ptr);
        super.doSave(frmModel, subContext);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("done saving " + toString());
        }
    }

    public String toString() {
        return "ContextJXPathBinding [xpath=" + this.xpath + "]";
    }
}
