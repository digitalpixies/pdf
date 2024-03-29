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

/**
 * NewJXPathBinding provides an implementation of a {@link Binding}
 * that references a class of bindings.
 * <p>
 * NOTES: <ol>
 * <li>This Binding assumes that the provided widget-id points to a
 * class that contains other widgets.</li>
 * </ol>
 *
 * @author Timothy Larson
 * @version CVS $Id: NewJXPathBinding.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class NewJXPathBinding extends ComposedJXPathBindingBase {

    private final String widgetId;

    private Binding classBinding;

    /**
     * Constructs NewJXPathBinding
     * @param commonAtts
     * @param widgetId
     * @param childBindings
     */
    public NewJXPathBinding(JXPathBindingBuilderBase.CommonAttributes commonAtts, String widgetId, JXPathBindingBase[] childBindings) {
        super(commonAtts, childBindings);
        this.widgetId = widgetId;
        this.classBinding = null;
    }

    private void resolve() {
        classBinding = getClass(widgetId);
    }

    /**
     * Narrows the scope on the form-model to the member widget-field, and
     * narrows the scope on the object-model to the member xpath-context
     * before continuing the binding over the child-bindings.
     */
    public void doLoad(Widget frmModel, JXPathContext jxpc) throws BindingException {
        if (classBinding == null)
            resolve();
        if (classBinding instanceof ClassJXPathBinding) {
            Binding[] subBindings = ((ComposedJXPathBindingBase)classBinding).getChildBindings();
            if (subBindings != null) {
                int size = subBindings.length;
                for (int i = 0; i < size; i++) {
                    subBindings[i].loadFormFromModel(frmModel, jxpc);
                }
            }
        } else {
            classBinding.loadFormFromModel(frmModel, jxpc);
        }
    }

    /**
     * Narrows the scope on the form-model to the member widget-field, and
     * narrows the scope on the object-model to the member xpath-context
     * before continuing the binding over the child-bindings.
     */
    public void doSave(Widget frmModel, JXPathContext jxpc) throws BindingException {
        if (classBinding == null)
            resolve();
        if (classBinding instanceof ClassJXPathBinding) {
            Binding[] subBindings = ((ComposedJXPathBindingBase)classBinding).getChildBindings();
            if (subBindings != null) {
                int size = subBindings.length;
                for (int i = 0; i < size; i++) {
                    subBindings[i].saveFormToModel(frmModel, jxpc);
                }
            }
        } else {
            classBinding.saveFormToModel(frmModel, jxpc);
        }
    }

    public String toString() {
        return "NewJXPathBinding [widget=" + this.widgetId + "]";
    }
}
