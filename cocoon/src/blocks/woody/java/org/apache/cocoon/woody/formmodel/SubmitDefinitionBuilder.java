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
package org.apache.cocoon.woody.formmodel;

import org.apache.cocoon.woody.util.DomHelper;
import org.w3c.dom.Element;

/**
 * Builds a <code>&lt;wd:submit></code> widget. A submit is an action that terminates
 * the current form. It can either require the form to be valid (in which case it will
 * be redisplayed if not valid) or terminate it without validation (e.g. a "cancel" button).
 * <p>
 * The syntax is as follows :
 * <pre>
 *   &lt;wd:submit id="sub-id" action-command="cmd" validate="false">
 * </pre>
 * The "validate" attribute can have the value <code>true</code> or <code>false</code>
 * and determines if the form is to be validated (defaults to true).
 * 
 * @author <a href="http://www.apache.org/~sylvain/">Sylvain Wallez</a>
 * @version CVS $Id: SubmitDefinitionBuilder.java 433543 2006-08-22 06:22:54Z crossley $
 */
public class SubmitDefinitionBuilder  extends ActionDefinitionBuilder {
    
    public WidgetDefinition buildWidgetDefinition(Element widgetElement) throws Exception {
        
        SubmitDefinition definition = (SubmitDefinition)super.buildWidgetDefinition(widgetElement);
        definition.setValidateForm(DomHelper.getAttributeAsBoolean(widgetElement, "validate", true));
        return definition;
    }

    protected ActionDefinition createDefinition() {
        return new SubmitDefinition();
    }
}
