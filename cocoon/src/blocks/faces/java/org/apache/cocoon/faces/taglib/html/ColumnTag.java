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

import org.apache.cocoon.faces.taglib.UIComponentTag;

import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;

/**
 * @version CVS $Id: ColumnTag.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class ColumnTag extends UIComponentTag {

    public String getRendererType() {
        return null;
    }

    public String getComponentType() {
        return "javax.faces.Column";
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        if (!(component instanceof UIColumn)) {
            throw new FacesException("Tag <" + getClass().getName() + "> expected UIColumn. " +
                                     "Got <" + component.getClass().getName() + ">");
        }
    }
}
