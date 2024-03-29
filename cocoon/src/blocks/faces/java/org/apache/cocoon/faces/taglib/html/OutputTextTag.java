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
package org.apache.cocoon.faces.taglib.html;

import org.apache.cocoon.faces.FacesUtils;
import org.apache.cocoon.faces.taglib.UIComponentTag;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;

/**
 * @version CVS $Id: OutputTextTag.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class OutputTextTag extends UIComponentTag {

    private String converter;
    private String value;
    private String escape;
    private String style;
    private String styleClass;
    private String title;


    public void setConverter(String converter) {
        this.converter = converter;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setEscape(String escape) {
        this.escape = escape;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRendererType() {
        return "javax.faces.Text";
    }

    public String getComponentType() {
        return "javax.faces.HtmlOutputText";
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        UIOutput output;
        try {
            output = (UIOutput) component;
        } catch (ClassCastException cce) {
            throw new FacesException("Tag <" + getClass().getName() + "> expected UIOutput. " +
                                     "Got <" + component.getClass().getName() + ">");
        }

        if (converter != null) {
            if (FacesUtils.isExpression(converter)) {
                output.setValueBinding("converter", createValueBinding(converter));
            } else {
                output.setConverter(getApplication().createConverter(converter));
            }
        }

        if (value != null) {
            if (FacesUtils.isExpression(value)) {
                output.setValueBinding("value", createValueBinding(value));
            } else {
                output.setValue(value);
            }
        }

        setBooleanProperty(component, "escape", escape);

        setProperty(component, "style", style);
        setProperty(component, "styleClass", styleClass);
        setProperty(component, "title", title);
    }

    public void recycle() {
        super.recycle();
        converter = null;
        value = null;
        escape = null;
        style = null;
        styleClass = null;
        title = null;
    }
}
