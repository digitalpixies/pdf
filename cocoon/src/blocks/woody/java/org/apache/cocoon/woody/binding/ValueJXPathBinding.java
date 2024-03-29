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

import org.apache.avalon.framework.logger.Logger;
import org.apache.cocoon.woody.formmodel.Widget;
import org.apache.cocoon.woody.datatype.convertor.Convertor;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;

import java.util.Locale;

/**
 * ValueJXPathBinding provides an implementation of a {@link Binding}
 * that loads and saves the information behind a specific xpath expresion
 * (pointing to an attribute or text-node) to and from a specific Woody
 * widget as identified by its id.
 *
 * @version CVS $Id: ValueJXPathBinding.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class ValueJXPathBinding extends JXPathBindingBase {

    /**
     * The xpath expression to the objectModel property
     */
    private final String xpath;

    /**
     * The id of the Woody form-widget
     */
    private final String fieldId;

    /**
     * Flag indicating if the objectModel-property can be altered or not
     */
    private final JXPathBindingBase updateBinding;

    /**
     * Optional convertor to convert values to and from strings when setting or reading
     * the from the model. Especially used in combination with XML models where everything
     * are strings.
     */
    private final Convertor convertor;

    /**
     * The locale to pass to the {@link #convertor}.
     */
    private final Locale convertorLocale;

    /**
     * Constructs FieldJXPathBinding.
     *
     * @param convertor may be null
     */
    public ValueJXPathBinding(JXPathBindingBuilderBase.CommonAttributes commonAtts, String widgetId, String xpath, JXPathBindingBase[] updateBindings,
                              Convertor convertor, Locale convertorLocale) {
        super(commonAtts);
        this.fieldId = widgetId;
        this.xpath = xpath;
        this.updateBinding = new ComposedJXPathBindingBase(JXPathBindingBuilderBase.CommonAttributes.DEFAULT, updateBindings);
        this.convertor = convertor;
        this.convertorLocale = convertorLocale;
    }

    /**
     * Actively performs the binding from the ObjectModel wrapped in a jxpath
     * context to the Woody-form-widget specified in this object.
     */
    public void doLoad(Widget frmModel, JXPathContext jxpc) throws BindingException {
        Widget widget = frmModel.getWidget(this.fieldId);
        if (widget == null) {
            throw new BindingException("The widget with the ID [" + this.fieldId
                    + "] referenced in the binding does not exist in the form definition.");
        }

        Object value = jxpc.getValue(this.xpath);
        if (value != null && convertor != null) {
            if (value instanceof String) {
                value = convertor.convertFromString((String)value, convertorLocale, null);
            } else {
                getLogger().warn("Convertor ignored on backend-value which isn't of type String.");
            }
        }

        widget.setValue(value);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Done loading " + toString() + " -- value= " + value);
        }
    }

    /**
     * Actively performs the binding from the Woody-form to the ObjectModel
     * wrapped in a jxpath context
     */
    public void doSave(Widget frmModel, JXPathContext jxpc) throws BindingException {
        Widget widget = frmModel.getWidget(this.fieldId);
        Object value = widget.getValue();
        if (value != null && convertor != null) {
            value = convertor.convertToString(value, convertorLocale, null);
        }

        Object oldValue = jxpc.getValue(this.xpath);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("value= " + value + "-- oldvalue=" + oldValue);
        }

        boolean update = false;

        if ((value == null && oldValue != null) || value != null && !value.equals(oldValue)) {
            // first update the value itself
            jxpc.createPathAndSetValue(this.xpath, value);

            // now perform any other bindings that need to be performed when the value is updated
            JXPathContext subContext = null;
            try {
                subContext = jxpc.getRelativeContext(jxpc.getPointer(this.xpath));
            } catch (JXPathException e) {
                // if the value has been set to null and the underlying model is a bean, then
                // JXPath will not be able to create a relative context
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("(Ignorable) problem binding field " + widget.getFullyQualifiedId(), e);
                }
            }
            if (subContext != null) {
                this.updateBinding.saveFormToModel(frmModel, subContext);
            }

            update = true;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("done saving " + toString() + " -- value= " + value + " -- on-update == " + update);
        }
    }

    public String toString() {
        return "ValueJXPathBinding [widget=" + this.fieldId + ", xpath=" + this.xpath + "]";
    }

    public void enableLogging(Logger logger) {
        super.enableLogging(logger);
        this.updateBinding.enableLogging(logger);
    }
}
